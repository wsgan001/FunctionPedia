package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.experiment.SubjectDataHandler;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer.WordBagCosineSimilarity;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordCoreSplit;

public class DocumentWordsMap implements Serializable {
	private static final long		serialVersionUID			= -1274751184374688502L;

	public static final Logger		logger						= Logger.getLogger(DocumentWordsMap.class);

	public int						threadId;
	public SubjectDataInfo			subject;

	public int						threadWordCount				= 0;
	public int						questionWordCount			= 0;
	public int						acceptedAnswerWordCount		= 0;
	public int						answersWordCount			= 0;
	public HashMap<String, Integer>	threadWordsCountMap			= new HashMap<>();
	public HashMap<String, Integer>	questionWordsCountMap		= new HashMap<>();
	public HashMap<String, Integer>	answersWordsCountMap		= new HashMap<>();
	public HashMap<String, Integer>	acceptedAnswerWordsCountMap	= new HashMap<>();

	public DocumentWordsMap(int threadId, SubjectDataInfo subject) {
		this.threadId = threadId;
		this.subject = subject;
	}

	public static void main(String[] args) {
		SubjectDataHandler.readSubjectDataFromFile(true);
		SubjectDataInfo subject = SubjectDataHandler.subjectDataMap.get(APILibrary.STANFORD_NLP);
		for (Integer threadId : subject.threadMap.keySet()) {
			DocumentWordsMap docWords = new DocumentWordsMap(threadId, subject);
			docWords.countWords();
			System.out.println("thread");
			System.out.println(docWords.threadWordsCountMap);
			System.out.println("question");
			System.out.println(docWords.questionWordsCountMap);
			System.out.println("acc answer");
			System.out.println(docWords.acceptedAnswerWordsCountMap);
			System.out.println("answers");
			System.out.println(docWords.answersWordsCountMap);
		}
	}

	public void countWords() {
		ThreadInfo thread = subject.threadMap.get(threadId);
		countWordsOfPost(thread.getQuestionId(), PostInfo.QUESTION_TYPE);
		int acAnsId = thread.getAcceptedAnswerId();
		for (int ansId : thread.getAnswersId()) {
			if (ansId == acAnsId)
				countWordsOfPost(acAnsId, PostInfo.ACCEPTED_ANSWER_TYPE);
			else
				countWordsOfPost(ansId, PostInfo.ANSWER_TYPE);
		}
	}

	// subject data info
	public void countWordsOfPost(int postId, int postType) {
		for (Integer contentId : subject.postToContentMap.get(postId)) {
			ContentInfo content = subject.getContentMap().get(contentId);
			for (int paraId : content.getParagraphsId()) {
				ParagraphInfo paragraph = subject.getParagraphMap().get(paraId);
				if (paragraph.getParagraphType() == ParagraphInfo.PARAGRAPH_TYPE_CODE)
					continue;
				for (int sentId : paragraph.getSentencesId()) {
					SentenceInfo sentence = subject.getSentenceMap().get(sentId);
					List<String> rawWords = StanfordCoreSplit
							.splitSentenceWithPunctuation(sentence.getText());
					for (String rawWord : rawWords) {
						String word = WordBagCosineSimilarity.getStemmedValidWord(rawWord);
						if (word == null)
							continue;

						threadWordCount++;
						WordBagCosineSimilarity.addWordToCountMap(threadWordsCountMap, word);
						switch (postType) {
						case PostInfo.QUESTION_TYPE:
							questionWordCount++;
							WordBagCosineSimilarity.addWordToCountMap(questionWordsCountMap, word);
							break;
						case PostInfo.ACCEPTED_ANSWER_TYPE:
							acceptedAnswerWordCount++;
							WordBagCosineSimilarity.addWordToCountMap(acceptedAnswerWordsCountMap, word);
						case PostInfo.ANSWER_TYPE:
							answersWordCount++;
							WordBagCosineSimilarity.addWordToCountMap(answersWordsCountMap, word);
							break;
						default:
							break;
						}
					}
				}
			}
		}
	}

	public HashMap<String, Integer> getWordsCountMapOfPost(int postId) {
		HashMap<String, Integer> words = new HashMap<>();

		for (Integer contentId : subject.postToContentMap.get(postId)) {
			ContentInfo content = subject.getContentMap().get(contentId);
			for (int paraId : content.getParagraphsId()) {
				combineCountMaps(words, getWordsCountMapOfParagraph(paraId));
			}
		}
		return words;
	}

	public HashMap<String, Integer> getWordsCountMapOfParagraph(int paraId) {
		HashMap<String, Integer> words = new HashMap<>();
		ParagraphInfo paragraph = subject.getParagraphMap().get(paraId);
		if (paragraph.getParagraphType() == ParagraphInfo.PARAGRAPH_TYPE_CODE)
			return null;
		for (int sentId : paragraph.getSentencesId()) {
			combineCountMaps(words, getWordsCountMapOfSentence(sentId));
		}
		return words;
	}

	// subject data info
	public HashMap<String, Integer> getWordsCountMapOfSentence(int sentId) {
		HashMap<String, Integer> words = new HashMap<>();

		SentenceInfo sentence = subject.getSentenceMap().get(sentId);
		List<String> rawWords = StanfordCoreSplit.splitSentenceWithPunctuation(sentence.getText());
		for (String rawWord : rawWords) {
			String word = WordBagCosineSimilarity.getStemmedValidWord(rawWord);
			if (word != null)
				WordBagCosineSimilarity.addWordToCountMap(words, word);
		}
		return words;
	}

	@Deprecated
	public void countWordsWithStatic() {
		ThreadInfo thread = subject.threadMap.get(threadId);
		questionWordsCountMap = getWordCountMap(thread.getQuestionId(), subject);
		threadWordsCountMap = combineCountMaps(threadWordsCountMap, questionWordsCountMap);
		int acAnsId = thread.getAcceptedAnswerId();
		for (int ansId : thread.getAnswersId()) {
			HashMap<String, Integer> answerCountMap = getWordCountMap(ansId, subject);
			if (ansId == acAnsId)
				acceptedAnswerWordsCountMap = answerCountMap;
			answersWordsCountMap = combineCountMaps(acceptedAnswerWordsCountMap, answerCountMap);
		}
		threadWordsCountMap = combineCountMaps(threadWordsCountMap, answersWordsCountMap);
	}

	// subject data info
	@Deprecated
	public static HashMap<String, Integer> getWordCountMap(int postId, SubjectDataInfo subject) {
		HashMap<String, Integer> words = new HashMap<>();
		PostInfo post = subject.postMap.get(postId);
		for (Integer contentId : subject.postToContentMap.get(postId)) {
			ContentInfo content = subject.getContentMap().get(contentId);
			for (int paraId : content.getParagraphsId()) {
				ParagraphInfo paragraph = subject.getParagraphMap().get(paraId);
				if (paragraph.getParagraphType() == ParagraphInfo.PARAGRAPH_TYPE_CODE)
					continue;
				for (int sentId : paragraph.getSentencesId()) {
					SentenceInfo sentence = subject.getSentenceMap().get(sentId);
					List<String> rawWords = StanfordCoreSplit
							.splitSentenceWithPunctuation(sentence.getText());
					for (String rawWord : rawWords) {
						String word = WordBagCosineSimilarity.getStemmedValidWord(rawWord);
						if (word == null)
							continue;

						if (!words.containsKey(word))
							words.put(word, 1);
						else
							words.put(word, words.get(word) + 1);
					}
				}
			}
		}
		return words;
	}

	private HashMap<String, Integer> combineCountMaps(HashMap<String, Integer> map1,
			HashMap<String, Integer> map2) {
		if (map1 == null && map2 == null)
			return new HashMap<>();
		if (map1 == null)
			return map2;
		else if (map2 == null)
			return map1;

		for (String word : map2.keySet()) {
			if (map1.containsKey(word))
				map1.put(word, map1.get(word) + map2.get(word));
			else
				map1.put(word, map2.get(word));
		}
		return map1;
	}
}
