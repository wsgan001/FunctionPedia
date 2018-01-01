package cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.tartarus.snowball.ext.swedishStemmer;

import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.document.parser.DocumentParser;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.experiment.SubjectDataHandler;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.UserInfo;
import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.task.parser.TaskParser;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.DocumentWordsMap;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.utils.Config;

public class FeatureExtractor {
	public static final Logger logger = Logger.getLogger(FeatureExtractor.class);

	public static Document extractFeatures(int threadId, SubjectDataInfo subject) {
		Document document = new Document();
		document.setThreadId(threadId);

		ThreadInfo thread = subject.threadMap.get(threadId);

		List<Integer> phrasesId = subject.threadToPhrasesMap.get(threadId);
		Set<Integer> taskIdSet = subject.threadToTasksMap.get(threadId);

		// prepare for the vps of each tasks
		Map<Integer, List<PhraseInfo>> taskToPhraseMap = new HashMap<>();
		Map<Integer, List<VerbalPhraseStructureInfo>> taskToVPSMap = new HashMap<>();
		for (Integer phraseId : phrasesId) {
			PhraseInfo phrase = subject.phraseMap.get(phraseId);
			int taskId = phrase.getTaskId();
			if (taskId > 0 && taskIdSet.contains(taskId)) {
				VerbalPhraseStructureInfo vps = TaskParser.getPhraseStructure(phrase);
				if (taskToVPSMap.containsKey(taskId)) {
					taskToVPSMap.get(taskId).add(vps);
				}
				else {
					List<VerbalPhraseStructureInfo> vpsList = new ArrayList<>();
					vpsList.add(vps);
					taskToVPSMap.put(taskId, vpsList);
				}

				if (taskToPhraseMap.containsKey(taskId)) {
					taskToPhraseMap.get(taskId).add(phrase);
				}
				else {
					List<PhraseInfo> phraseList = new ArrayList<>();
					phraseList.add(phrase);
					taskToPhraseMap.put(taskId, phraseList);
				}
			}
		}

		// pre-process: sort answer list
		List<PostInfo> answerList = new ArrayList<>();
		for (int id : thread.getAnswersId()) {
			PostInfo answer = subject.getPostMap().get(id);
			answerList.add(answer);
		}
		answerList.sort(new Comparator<PostInfo>() {
			@Override
			public int compare(PostInfo p1, PostInfo p2) {
				return p2.getScore() - p1.getScore(); // descending
			}
		});

		DocumentWordsMap documentWordsMap = new DocumentWordsMap(threadId, subject);
		documentWordsMap.countWords();

		// handle each task
		for (Integer taskId : taskIdSet) {
			TaskInfo task = subject.taskMap.get(taskId);

			TaskVector vector = new TaskVector(task);

			List<PhraseInfo> phraseList = taskToPhraseMap.get(taskId);
			List<VerbalPhraseStructureInfo> vpsList = taskToVPSMap.get(taskId);
			if (phraseList.size() != vpsList.size()) {
				logger.error("Not equal: phrase size and vps size!!!");
				continue;
			}
			if (phraseList.size() <= 0) {
				logger.error("Empty phrase/vps list!!!");
				continue;
			}

			// following collected infos should be same for all the phrases/vps,
			// use ele[0] as example.
			VerbalPhraseStructureInfo firstVPS = vpsList.get(0);

			/** task info **/
			vector.length = task.getText().length();
			vector.wordCount = StringUtils.countMatches(task.getText(), TaskInfo.POS_SEPARATOR);
			vector.verbCount = StringUtils.countMatches(task.getText(),
					TaskInfo.POS_SEPARATOR + TaskInfo.POS_VERB);
			vector.nounCount = StringUtils.countMatches(task.getText(),
					TaskInfo.POS_SEPARATOR + TaskInfo.POS_NOUN)
					+ StringUtils.countMatches(task.getText(),
							TaskInfo.POS_SEPARATOR + TaskInfo.POS_KERNEL_NOUN);
			if (firstVPS.getSubNP() != null)
				vector.npWordCount = firstVPS.getSubNP().toWordList().size();
			else
				vector.npWordCount = 0;
			if (firstVPS.getSubPPList() != null && firstVPS.getSubPPList().size() > 0)
				vector.ppWordCount = firstVPS.getSubPPListAsWordList().size();
			else
				vector.ppWordCount = 0;

			vector.taskFrequency = phraseList.size();

			/** page rank - task similarity **/
			if (taskIdSet.size() <= 1) {
				// no other tasks
				vector.similarityWithTasksMax = 0;
				vector.similarityWithTasksAvrg = 0;
			}
			else {
				for (Integer otherTaskId : taskIdSet) {
					if (otherTaskId == taskId)
						continue;
					TaskInfo otherTask = subject.taskMap.get(otherTaskId);
					float similarity = TaskSimilarity.calculateSimilarity(task.getText(),
							otherTask.getText());
					vector.similarityWithTasksAvrg += similarity;
					vector.similarityWithTasksMax = vector.similarityWithTasksMax >= similarity
							? vector.similarityWithTasksMax : similarity;
				}
				vector.similarityWithTasksAvrg /= (taskIdSet.size() - 1); // except
																			// itselt
			}

			/** text similarity **/
			HashMap<String, Integer> wordbag = firstVPS.toWordBagCountMap();
			String kernelVerb = task.getKernelVerb();
			String kernelNoun = task.getKernelNoun();
			HashMap<String, Integer> verbWordbag = firstVPS.getVerbWordBagCountMap();
			HashMap<String, Integer> nounWordbag = firstVPS.getNounWordBagCountMap();

			vector.tfInThread = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag,
					documentWordsMap.threadWordsCountMap);
			vector.tfInQuestion = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag,
					documentWordsMap.questionWordsCountMap);
			vector.tfInAcceptedAnswer = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag,
					documentWordsMap.acceptedAnswerWordsCountMap);
			vector.tfInAnswers = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag,
					documentWordsMap.answersWordsCountMap);

			vector.verbTFInThread = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag,
					documentWordsMap.threadWordsCountMap);
			vector.verbTFInQuestion = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag,
					documentWordsMap.questionWordsCountMap);
			vector.verbTFInAcceptedAnswer = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag,
					documentWordsMap.acceptedAnswerWordsCountMap);
			vector.verbTFInAnswers = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag,
					documentWordsMap.answersWordsCountMap);

			vector.nounTFInThread = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag,
					documentWordsMap.threadWordsCountMap);
			vector.nounTFInQuestion = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag,
					documentWordsMap.questionWordsCountMap);
			vector.nounTFInAcceptedAnswer = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag,
					documentWordsMap.acceptedAnswerWordsCountMap);
			vector.nounTFInAnswers = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag,
					documentWordsMap.answersWordsCountMap);

			Integer kvt = documentWordsMap.threadWordsCountMap.get(kernelVerb);
			vector.kernelVerbTFInThread = (kvt == null) ? 0 : kvt;
			Integer kvq = documentWordsMap.questionWordsCountMap.get(kernelVerb);
			vector.kernelVerbTFInQuestion = (kvq == null) ? 0 : kvq;
			Integer kvac = documentWordsMap.acceptedAnswerWordsCountMap.get(kernelVerb);
			vector.kernelVerbTFInAcceptedAnswer = (kvac == null) ? 0 : kvac;
			Integer kvas = documentWordsMap.answersWordsCountMap.get(kernelVerb);
			vector.kernelVerbTFInAnswers = (kvas == null) ? 0 : kvas;

			Integer knt = documentWordsMap.threadWordsCountMap.get(kernelNoun);
			vector.kernelNounTFInThread = (knt == null) ? 0 : knt;
			Integer knq = documentWordsMap.questionWordsCountMap.get(kernelNoun);
			vector.kernelNounTFInQuestion = (knq == null) ? 0 : knq;
			Integer knac = documentWordsMap.acceptedAnswerWordsCountMap.get(kernelNoun);
			vector.kernelNounTFInAcceptedAnswer = (knac == null) ? 0 : knac;
			Integer knas = documentWordsMap.answersWordsCountMap.get(kernelNoun);
			vector.kernelNounTFInAnswers = (knas == null) ? 0 : knas;

			/** thread meta info **/
			vector.threadAnswersCount = thread.getAnswersId().length;
			vector.threadFavorites = thread.getFavoriteCount();
			vector.threadScores = thread.getVote();
			vector.threadViews = thread.getViewCount();

			for (int i = 0; i < phraseList.size(); i++) {
				PhraseInfo phrase = phraseList.get(i);

				// prepare related(context) data
				String srcPath = phrase.getSourcePath();
				String srcType = srcPath.substring(0, srcPath.indexOf(DocumentParser.PATH_ID_MARKER));
				int srcId = Integer.valueOf(srcPath.substring(
						srcPath.indexOf(DocumentParser.PATH_ID_MARKER)
								+ DocumentParser.PATH_ID_MARKER.length(),
						srcPath.indexOf(DocumentParser.PATH_SEPARATOR))).intValue();
				// System.out.println(srcType);
				// System.out.println(srcId);
				int contentId = Integer.valueOf(srcPath.substring(
						srcPath.indexOf(DocumentParser.PATH_CONTENT) + DocumentParser.PATH_CONTENT.length()
								+ DocumentParser.PATH_ID_MARKER.length(),
						srcPath.indexOf(DocumentParser.PATH_SEPARATOR,
								srcPath.indexOf(DocumentParser.PATH_CONTENT))))
						.intValue();
				// System.out.println(contentId);
				int paraId = Integer.valueOf(srcPath.substring(srcPath.indexOf(DocumentParser.PATH_PARAGRAPH)
						+ DocumentParser.PATH_PARAGRAPH.length() + DocumentParser.PATH_ID_MARKER.length(),
						srcPath.indexOf(DocumentParser.PATH_SEPARATOR,
								srcPath.indexOf(DocumentParser.PATH_PARAGRAPH))))
						.intValue();
				// System.out.println(paraId);
				int sentenceId = Integer.valueOf(srcPath.substring(
						srcPath.indexOf(DocumentParser.PATH_SENTENCE) + DocumentParser.PATH_SENTENCE.length()
								+ DocumentParser.PATH_ID_MARKER.length()))
						.intValue();
				// System.out.println(sentenceId);
				ContentInfo content = subject.contentMap.get(contentId);
				ParagraphInfo paragraph = subject.paragraphMap.get(paraId);
				SentenceInfo sentence = subject.sentenceMap.get(sentenceId);

				/** task info - proofs **/
				phrase.setProofs(Proof.extractProofs(phrase.getProofString()));
				// keep the best result
				vector.isVPNP = vector.isVPNP || phrase.hasProof(ProofType.FORM_VP_NP);
				vector.isVPNPPP = vector.isVPNPPP || phrase.hasProof(ProofType.FORM_VP_NP_PP);
				vector.isVPPP = vector.isVPPP || phrase.hasProof(ProofType.FORM_VP_PP);
				vector.contextQAWordsImmediate = vector.contextQAWordsImmediate
						|| phrase.hasProof(ProofType.CONTEXT_IMMEDIATE);
				vector.contextQAWordsNearBy = vector.contextQAWordsNearBy
						|| phrase.hasProof(ProofType.CONTEXT_NEARBY);
				vector.contextQAWordsPreceding = vector.contextQAWordsPreceding
						|| phrase.hasProof(ProofType.CONTEXT_PRECEDING);

				int phraseScore = phrase.getProofScore();
				vector.proofScoreMax = (vector.proofScoreMax >= phraseScore) ? vector.proofScoreMax
						: phraseScore;
				vector.proofScoreAvrg += phraseScore;// sum phase

				/** content info **/
				int contentLength = content.getText().length();
				vector.contentLengthMax = vector.contentLengthMax >= contentLength ? vector.contentLengthMax
						: contentLength;
				vector.contentLengthMin = vector.contentLengthMin <= contentLength ? vector.contentLengthMin
						: contentLength;
				vector.contentLengthAvrg += contentLength; // sum

				for (int paraOfContentId : content.getParagraphsId()) {
					ParagraphInfo para = subject.paragraphMap.get(paraOfContentId);
					if (para.getParagraphType() == ParagraphInfo.PARAGRAPH_TYPE_CODE) {
						vector.containsCodeInContent = true;
						break;
					}
				}

				/** paragraph info **/
				int paragraphLength = paragraph.getText().length();
				vector.paragraphLengthMax = vector.paragraphLengthMax >= paragraphLength
						? vector.paragraphLengthMax : paragraphLength;
				vector.paragraphLengthMin = vector.paragraphLengthMin <= paragraphLength
						? vector.paragraphLengthMin : paragraphLength;
				vector.paragraphLengthAvrg += paragraphLength; // sum

				int paragraphIndex = paragraph.getIndexAsChild();
				float paragraphRelativePosition = (paragraphIndex + 1)
						/ (float) content.getParagraphsId().length;
				vector.paragraphPositionFirst = (vector.paragraphPositionFirst <= paragraphIndex)
						? vector.paragraphPositionFirst : paragraphIndex;
				vector.paragraphPositionLast = (vector.paragraphPositionLast >= paragraphIndex)
						? vector.paragraphPositionLast : paragraphIndex;
				vector.paragraphPositionAvrg += paragraphIndex;
				vector.paragraphRelativePositionFirst = (vector.paragraphRelativePositionFirst <= paragraphRelativePosition)
						? vector.paragraphRelativePositionFirst : paragraphRelativePosition;
				vector.paragraphRelativePositionLast = (vector.paragraphRelativePositionLast >= paragraphRelativePosition)
						? vector.paragraphRelativePositionLast : paragraphRelativePosition;
				vector.paragraphRelativePositionAvrg += paragraphRelativePosition;

				vector.isInFirstParagraph = vector.isInFirstParagraph || paragraphIndex == 0;
				vector.isInLastParagraph = vector.isInLastParagraph
						|| (paragraphIndex == content.getParagraphsId().length - 1);
				vector.isTheOnlyParagraphInContent = vector.isTheOnlyParagraphInContent
						|| content.getParagraphsId().length <= 1;
				// context code
				if (paragraphIndex > 0) {
					ParagraphInfo previousPara = subject.paragraphMap
							.get(content.getParagraphsId()[paragraphIndex - 1]);
					if (previousPara.getParagraphType() == ParagraphInfo.PARAGRAPH_TYPE_CODE)
						vector.isAfterCode = true;
				}
				if (paragraphIndex < content.getParagraphsId().length - 1) {
					ParagraphInfo nextPara = subject.paragraphMap
							.get(content.getParagraphsId()[paragraphIndex + 1]);
					if (nextPara.getParagraphType() == ParagraphInfo.PARAGRAPH_TYPE_CODE)
						vector.isBeforeCode = true;
				}

				/** sentence info **/
				int sentenceLength = sentence.getText().length();
				vector.sentenceLengthMax = vector.sentenceLengthMax >= sentenceLength
						? vector.sentenceLengthMax : sentenceLength;
				vector.sentenceLengthMin = vector.sentenceLengthMin <= sentenceLength
						? vector.sentenceLengthMin : sentenceLength;
				vector.sentenceLengthAvrg += sentenceLength; // sum

				vector.containsCodeInSentence = vector.containsCodeInSentence
						|| sentence.getText().contains(DocumentParser.SENTENCE_CODE_MASK);

				int sentenceIndex = sentence.getIndexAsChild();
				float sentenceRelativePosition = (sentenceIndex + 1)
						/ (float) paragraph.getSentencesId().length;
				vector.sentencePositionFirst = (vector.sentencePositionFirst <= sentenceIndex)
						? vector.sentencePositionFirst : sentenceIndex;
				vector.sentencePositionLast = (vector.sentencePositionLast >= sentenceIndex)
						? vector.sentencePositionLast : sentenceIndex;
				vector.sentencePositionAvrg += sentenceIndex;
				vector.sentenceRelativePositionFirst = (vector.sentenceRelativePositionFirst <= sentenceRelativePosition)
						? vector.sentenceRelativePositionFirst : sentenceRelativePosition;
				vector.sentenceRelativePositionLast = (vector.sentenceRelativePositionLast >= sentenceRelativePosition)
						? vector.sentenceRelativePositionLast : sentenceRelativePosition;
				vector.sentenceRelativePositionAvrg += sentenceRelativePosition;

				vector.isInFirstSentence = vector.isInFirstSentence || sentenceIndex == 0;
				vector.isInLastSentence = vector.isInLastSentence
						|| (sentenceIndex == paragraph.getSentencesId().length - 1);
				vector.isTheOnlySentenceInParagraph = vector.isTheOnlySentenceInParagraph
						|| paragraph.getSentencesId().length <= 1;

				int ownerId = 0, lastEditorId = 0;
				int contentScore = 0;
				/** content source **/
				PostInfo srcPost = null;
				if (srcType.equals(DocumentParser.PATH_THREAD_TITLE)) {
					vector.isInTitle = true;

					srcPost = subject.postMap.get(thread.getQuestionId());
					ownerId = srcPost.getOwnerUserId();
					lastEditorId = srcPost.getLastEditorUserId();
					contentScore = srcPost.getScore();
				}
				else if (srcType.equals(DocumentParser.PATH_COMMENT_TEXT)) {
					vector.isInComment = true;
					CommentInfo comment = subject.commentMap.get(srcId);
					ownerId = comment.getUserId();
					contentScore = comment.getScore();
					srcPost = subject.postMap.get(comment.getPostId());
				}
				else if (srcType.equals(DocumentParser.PATH_POST_BODY)) {
					srcPost = subject.postMap.get(srcId);
					ownerId = srcPost.getOwnerUserId();
					lastEditorId = srcPost.getLastEditorUserId();
					contentScore = srcPost.getScore();

					switch (srcPost.getPostType()) {
					case PostInfo.QUESTION_TYPE: {
						vector.isInQuestion = true;
						break;
					}
					case PostInfo.ACCEPTED_ANSWER_TYPE: {
						vector.isInAcceptedAnswer = true;
					}
					case PostInfo.ANSWER_TYPE: {
						vector.isInAnswer = true;
						break;
					}
					default:
						break;
					}
				}

				if (vector.isInAnswer) {
					int answerIndex = answerList.indexOf(srcPost);
					float answerRelativeRank = answerList.indexOf(srcPost) / (float) answerList.size();
					// System.err.println(answerRelativeRank + "\t" +
					// answerIndex + "\t" + srcPost.getScore());
					vector.answerRelativeRankBest = (vector.answerRelativeRankBest <= answerRelativeRank)
							? vector.answerRelativeRankBest : answerRelativeRank; // Top
																					// xx%
					vector.answerRelativeRankAvrg += answerRelativeRank; // sum
				}
				else {
					vector.answerRelativeRankBest = 1;
					vector.answerRelativeRankAvrg = 1;
				}
				vector.contentScoreMax = vector.contentScoreMax >= contentScore ? vector.contentScoreMax
						: contentScore;
				vector.contentScoreAvrg += contentScore; // sum

				if (srcPost != null) {
					int commentCount = srcPost.getCommentsId().length;
					vector.commentCountMax = vector.commentCountMax >= commentCount ? vector.commentCountMax
							: commentCount;
					vector.commentCountMin = vector.commentCountMin <= commentCount ? vector.commentCountMin
							: commentCount;
					vector.commentCountAvrg += commentCount; // sum
				}

				if (ownerId > 0) {
					vector.isRegisteredUser = true;
					if (lastEditorId > 0)
						vector.isEdited = true;
					else
						lastEditorId = ownerId;

					UserInfo owner = SubjectDataHandler.userMap.get(ownerId);
					UserInfo lastEditor = SubjectDataHandler.userMap.get(lastEditorId);
					if (owner != null) {

						int ownerReputation = owner.getReputation();
						int ownerBadges1st = owner.getBadgesCountFirstClass();
						int ownerBadges2nd = owner.getBadgesCountSecondClass();
						int ownerBadges3rd = owner.getBadgesCountThirdClass();
						int ownerQuestionsCount = owner.getAskedQuestionsCount();
						int ownerAnswersCount = owner.getAnswersCount();
						float ownerAcceptedAnswerRate = ownerAnswersCount == 0 ? 0
								: owner.getAcceptedAnswersCount() / (float) ownerAnswersCount;
						int ownerUpVotes = owner.getUpVotes();
						int ownerDownVotes = owner.getDownVotes();

						vector.ownerReputationMax = vector.ownerReputationMax >= ownerReputation
								? vector.ownerReputationMax : ownerReputation;
						vector.ownerReputationSum += ownerReputation;

						vector.ownerBadges1stMax = vector.ownerBadges1stMax >= ownerBadges1st
								? vector.ownerBadges1stMax : ownerBadges1st;
						vector.ownerBadges1stSum += ownerBadges1st;

						vector.ownerBadges2ndMax = vector.ownerBadges2ndMax >= ownerBadges2nd
								? vector.ownerBadges2ndMax : ownerBadges2nd;
						vector.ownerBadges2ndSum += ownerBadges2nd;

						vector.ownerBadges3rdMax = vector.ownerBadges3rdMax >= ownerBadges3rd
								? vector.ownerBadges3rdMax : ownerBadges3rd;
						vector.ownerBadges3rdSum += ownerBadges3rd;

						vector.ownerQuestionsCountMax = vector.ownerQuestionsCountMax >= ownerQuestionsCount
								? vector.ownerQuestionsCountMax : ownerQuestionsCount;
						vector.ownerQuestionsCountSum += ownerQuestionsCount;

						vector.ownerAnswersCountMax = vector.ownerAnswersCountMax >= ownerAnswersCount
								? vector.ownerAnswersCountMax : ownerAnswersCount;
						vector.ownerAnswersCountSum += ownerAnswersCount;

						vector.ownerUpVotesMax = vector.ownerUpVotesMax >= ownerUpVotes
								? vector.ownerUpVotesMax : ownerUpVotes;
						vector.ownerUpVotesSum += ownerUpVotes;

						vector.ownerDownVotesMax = vector.ownerDownVotesMax >= ownerDownVotes
								? vector.ownerDownVotesMax : ownerDownVotes;
						vector.ownerDownVotesSum += ownerDownVotes;

						vector.ownerAcceptedAnswerRateMax = vector.ownerAcceptedAnswerRateMax >= ownerAcceptedAnswerRate
								? vector.ownerAcceptedAnswerRateMax : ownerAcceptedAnswerRate;
						vector.ownerAcceptedAnswerRateAvrg += ownerAcceptedAnswerRate;
					}
					if (lastEditor != null) {
						int lastEditorReputation = lastEditor.getReputation();
						int lastEditorBadges1st = lastEditor.getBadgesCountFirstClass();
						int lastEditorBadges2nd = lastEditor.getBadgesCountSecondClass();
						int lastEditorBadges3rd = lastEditor.getBadgesCountThirdClass();
						int lastEditorQuestionsCount = lastEditor.getAskedQuestionsCount();
						int lastEditorAnswersCount = lastEditor.getAnswersCount();
						float lastEditorAcceptedAnswerRate = lastEditorAnswersCount == 0 ? 0
								: lastEditor.getAcceptedAnswersCount() / (float) lastEditorAnswersCount;
						int lastEditorUpVotes = lastEditor.getUpVotes();
						int lastEditorDownVotes = lastEditor.getDownVotes();

						vector.lastEditorReputationMax = vector.lastEditorReputationMax >= lastEditorReputation
								? vector.lastEditorReputationMax : lastEditorReputation;
						vector.lastEditorReputationSum += lastEditorReputation;

						vector.lastEditorBadges1stMax = vector.lastEditorBadges1stMax >= lastEditorBadges1st
								? vector.lastEditorBadges1stMax : lastEditorBadges1st;
						vector.lastEditorBadges1stSum += lastEditorBadges1st;

						vector.lastEditorBadges2ndMax = vector.lastEditorBadges2ndMax >= lastEditorBadges2nd
								? vector.lastEditorBadges2ndMax : lastEditorBadges2nd;
						vector.lastEditorBadges2ndSum += lastEditorBadges2nd;

						vector.lastEditorBadges3rdMax = vector.lastEditorBadges3rdMax >= lastEditorBadges3rd
								? vector.lastEditorBadges3rdMax : lastEditorBadges3rd;
						vector.lastEditorBadges3rdSum += lastEditorBadges3rd;

						vector.lastEditorQuestionsCountMax = vector.lastEditorQuestionsCountMax >= lastEditorQuestionsCount
								? vector.lastEditorQuestionsCountMax : lastEditorQuestionsCount;
						vector.lastEditorQuestionsCountSum += lastEditorQuestionsCount;

						vector.lastEditorAnswersCountMax = vector.lastEditorAnswersCountMax >= lastEditorAnswersCount
								? vector.lastEditorAnswersCountMax : lastEditorAnswersCount;
						vector.lastEditorAnswersCountSum += lastEditorAnswersCount;

						vector.lastEditorUpVotesMax = vector.lastEditorUpVotesMax >= lastEditorUpVotes
								? vector.lastEditorUpVotesMax : lastEditorUpVotes;
						vector.lastEditorUpVotesSum += lastEditorUpVotes;

						vector.lastEditorDownVotesMax = vector.lastEditorDownVotesMax >= lastEditorDownVotes
								? vector.lastEditorDownVotesMax : lastEditorDownVotes;
						vector.lastEditorDownVotesSum += lastEditorDownVotes;

						vector.lastEditorAcceptedAnswerRateMax = vector.lastEditorAcceptedAnswerRateMax >= lastEditorAcceptedAnswerRate
								? vector.lastEditorAcceptedAnswerRateMax : lastEditorAcceptedAnswerRate;
						vector.lastEditorAcceptedAnswerRateAvrg += lastEditorAcceptedAnswerRate;
					}
				}

				/** context text similarity **/
				HashMap<String, Integer> postWordsMap = documentWordsMap
						.getWordsCountMapOfPost(srcPost.getId());
				HashMap<String, Integer> paragraphWordsMap = documentWordsMap
						.getWordsCountMapOfParagraph(paraId);
				HashMap<String, Integer> sentenceWordsMap = documentWordsMap
						.getWordsCountMapOfSentence(sentenceId);

				float tfpost = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag, postWordsMap);
				float tfpara = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag, paragraphWordsMap);
				float tfsent = WordBagCosineSimilarity.calculateCosineSimlarity(wordbag, sentenceWordsMap);
				vector.tfInPostMax = vector.tfInPostMax >= tfpost ? vector.tfInPostMax : tfpost;
				vector.tfInParagraphMax = vector.tfInParagraphMax >= tfpara ? vector.tfInParagraphMax
						: tfpara;
				vector.tfInSentenceMax = vector.tfInSentenceMax >= tfsent ? vector.tfInSentenceMax : tfsent;
				vector.tfInPostAvrg += tfpost;
				vector.tfInParagraphAvrg += tfpara;
				vector.tfInSentenceAvrg += tfsent;

				float vpost = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag, postWordsMap);
				float vpara = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag,
						paragraphWordsMap);
				float vsent = WordBagCosineSimilarity.calculateCosineSimlarity(verbWordbag, sentenceWordsMap);
				vector.verbTFInPostMax = vector.verbTFInPostMax >= vpost ? vector.verbTFInPostMax : vpost;
				vector.verbTFInParagraphMax = vector.verbTFInParagraphMax >= vpara
						? vector.verbTFInParagraphMax : vpara;
				vector.verbTFInSentenceMax = vector.verbTFInSentenceMax >= vsent ? vector.verbTFInSentenceMax
						: vsent;
				vector.verbTFInPostAvrg += vpost;
				vector.verbTFInParagraphAvrg += vpara;
				vector.verbTFInSentenceAvrg += vsent;

				float npost = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag, postWordsMap);
				float npara = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag,
						paragraphWordsMap);
				float nsent = WordBagCosineSimilarity.calculateCosineSimlarity(nounWordbag, sentenceWordsMap);
				vector.nounTFInPostMax = vector.nounTFInPostMax >= npost ? vector.nounTFInPostMax : npost;
				vector.nounTFInParagraphMax = vector.nounTFInParagraphMax >= npara
						? vector.nounTFInParagraphMax : npara;
				vector.nounTFInSentenceMax = vector.nounTFInSentenceMax >= nsent ? vector.nounTFInSentenceMax
						: nsent;
				vector.nounTFInPostAvrg += npost;
				vector.nounTFInParagraphAvrg += npara;
				vector.nounTFInSentenceAvrg += nsent;

				Integer kvpost = postWordsMap.get(kernelVerb);
				Integer kvpara = paragraphWordsMap.get(kernelVerb);
				Integer kvsent = sentenceWordsMap.get(kernelVerb);
				if (kvpost != null) {
					vector.kernelVerbTFInPostMax = vector.kernelVerbTFInPostMax >= kvpost
							? vector.kernelVerbTFInPostMax : kvpost;
					vector.kernelVerbTFInPostAvrg += kvpost;
				}
				if (kvpara != null) {
					vector.kernelVerbTFInParagraphMax = vector.kernelVerbTFInParagraphMax >= kvpara
							? vector.kernelVerbTFInParagraphMax : kvpara;
					vector.kernelVerbTFInParagraphAvrg += kvpara;
				}
				if (kvsent != null) {
					vector.kernelVerbTFInSentenceMax = vector.kernelVerbTFInSentenceMax >= kvsent
							? vector.kernelVerbTFInSentenceMax : kvsent;
					vector.kernelVerbTFInSentenceAvrg += kvsent;
				}

				Integer knpost = postWordsMap.get(kernelNoun);
				Integer knpara = paragraphWordsMap.get(kernelNoun);
				Integer knsent = sentenceWordsMap.get(kernelNoun);
				if (knpost != null) {
					vector.kernelNounTFInPostMax = vector.kernelNounTFInPostMax >= knpost
							? vector.kernelNounTFInPostMax : knpost;
					vector.kernelNounTFInPostAvrg += knpost;
				}
				if (knpara != null) {
					vector.kernelNounTFInParagraphMax = vector.kernelNounTFInParagraphMax >= knpara
							? vector.kernelNounTFInParagraphMax : knpara;
					vector.kernelNounTFInParagraphAvrg += knpara;
				}
				if (knsent != null) {
					vector.kernelNounTFInSentenceMax = vector.kernelNounTFInSentenceMax >= knsent
							? vector.kernelNounTFInSentenceMax : knsent;
					vector.kernelNounTFInSentenceAvrg += knsent;
				}

			}

			if (vector.taskFrequency > 0) {
				vector.proofScoreAvrg /= (float) vector.taskFrequency;
				vector.answerRelativeRankAvrg /= (float) vector.taskFrequency;

				vector.contentLengthAvrg /= (float) vector.taskFrequency;
				vector.contentScoreAvrg /= (float) vector.taskFrequency;
				vector.commentCountAvrg /= (float) vector.taskFrequency;

				vector.verbTFInPostAvrg /= (float) vector.taskFrequency;
				vector.verbTFInParagraphAvrg /= (float) vector.taskFrequency;
				vector.verbTFInSentenceAvrg /= (float) vector.taskFrequency;

				vector.nounTFInPostAvrg /= (float) vector.taskFrequency;
				vector.nounTFInParagraphAvrg /= (float) vector.taskFrequency;
				vector.nounTFInSentenceAvrg /= (float) vector.taskFrequency;

				vector.kernelVerbTFInPostAvrg /= (float) vector.taskFrequency;
				vector.kernelVerbTFInParagraphAvrg /= (float) vector.taskFrequency;
				vector.kernelVerbTFInSentenceAvrg /= (float) vector.taskFrequency;

				vector.kernelNounTFInPostAvrg /= (float) vector.taskFrequency;
				vector.kernelNounTFInParagraphAvrg /= (float) vector.taskFrequency;
				vector.kernelNounTFInSentenceAvrg /= (float) vector.taskFrequency;

				vector.paragraphPositionAvrg /= (float) vector.taskFrequency;
				vector.paragraphRelativePositionAvrg /= (float) vector.taskFrequency;
				vector.paragraphLengthAvrg /= (float) vector.taskFrequency;
				vector.sentencePositionAvrg /= (float) vector.taskFrequency;
				vector.sentenceRelativePositionAvrg /= (float) vector.taskFrequency;
				vector.sentenceLengthAvrg /= (float) vector.taskFrequency;

				vector.ownerReputationAvrg = vector.ownerReputationSum / (float) vector.taskFrequency;
				vector.ownerBadges1stAvrg = vector.ownerBadges1stSum / (float) vector.taskFrequency;
				vector.ownerBadges2ndAvrg = vector.ownerBadges2ndSum / (float) vector.taskFrequency;
				vector.ownerBadges3rdAvrg = vector.ownerBadges3rdSum / (float) vector.taskFrequency;
				vector.ownerQuestionsCountAvrg = vector.ownerQuestionsCountSum / (float) vector.taskFrequency;
				vector.ownerAnswersCountAvrg = vector.ownerAnswersCountSum / (float) vector.taskFrequency;
				vector.ownerUpVotesAvrg = vector.ownerUpVotesSum / (float) vector.taskFrequency;
				vector.ownerDownVotesAvrg = vector.ownerDownVotesSum / (float) vector.taskFrequency;
				vector.ownerAcceptedAnswerRateAvrg /= (float) vector.taskFrequency;

				vector.lastEditorReputationAvrg = vector.lastEditorReputationSum
						/ (float) vector.taskFrequency;
				vector.lastEditorBadges1stAvrg = vector.lastEditorBadges1stSum / (float) vector.taskFrequency;
				vector.lastEditorBadges2ndAvrg = vector.lastEditorBadges2ndSum / (float) vector.taskFrequency;
				vector.lastEditorBadges3rdAvrg = vector.lastEditorBadges3rdSum / (float) vector.taskFrequency;
				vector.lastEditorQuestionsCountAvrg = vector.lastEditorQuestionsCountSum
						/ (float) vector.taskFrequency;
				vector.lastEditorAnswersCountAvrg = vector.lastEditorAnswersCountSum
						/ (float) vector.taskFrequency;
				vector.lastEditorUpVotesAvrg = vector.lastEditorUpVotesSum / (float) vector.taskFrequency;
				vector.lastEditorDownVotesAvrg = vector.lastEditorDownVotesSum / (float) vector.taskFrequency;
				vector.lastEditorAcceptedAnswerRateAvrg /= (float) vector.taskFrequency;
			}

			document.addTaskVector(vector);
		}

		return document;
	}

	public static void main(String[] args) {
		// SubjectDataHandler.readSubjectDataFromFile(true);

		FileWriter fw;
		try {
			fw = new FileWriter(new File(Config.getSubjectDataDir() + "/vectors.txt"));
			SubjectDataInfo subject;
			subject = SubjectDataHandler.readSubjectDataFromFile(APILibrary.JAVAFX);
			SubjectDataHandler.readUserDataFromFile();

			// subject =
			// SubjectDataHandler.subjectDataMap.get(APILibrary.LUCENE);
			Document document = null;
			// int tid = 6456;
			// logger.info(tid);
			//
			// try {
			// document = FeatureExtractor.extractFeatures(tid, subject);
			// }
			// catch (Exception e) {
			// e.printStackTrace();
			// System.err.println(tid);
			// System.out.println(document);
			// System.err.println(document.getTaskVectors());
			// }
			// fw.write("====================" + document.getThreadId() +
			// "=======================\n");
			// for (TaskVector vector : document.getTaskVectors()) {
			// fw.write("[" + vector + "]\n");
			// }

			for (Integer threadId : subject.threadMap.keySet()) {
				logger.info(threadId);
				document = null;
				try {
					document = FeatureExtractor.extractFeatures(threadId, subject);
				}
				catch (Exception e) {
					e.printStackTrace();
					System.err.println(threadId);
					System.err.println(document.getTaskVectors());
				}
				fw.write("====================" + document.getThreadId() + "=======================\n");
				for (TaskVector vector : document.getTaskVectors()) {
					fw.write("**************" + vector.getTaskInfo().getText() + "************\n");
					fw.write(vector + "\n");
					fw.write("\n");
				}
				fw.write("\n");
			}
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
