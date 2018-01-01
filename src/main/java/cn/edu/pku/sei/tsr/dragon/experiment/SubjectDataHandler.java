package cn.edu.pku.sei.tsr.dragon.experiment;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.document.dao.ContentDAO;
import cn.edu.pku.sei.tsr.dragon.document.dao.ParagraphDAO;
import cn.edu.pku.sei.tsr.dragon.document.dao.SentenceDAO;
import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.dao.PhraseDAO;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyThreadDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyUserDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOTagDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.UserInfo;
import cn.edu.pku.sei.tsr.dragon.task.dao.TaskDAO;
import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.task.parser.TaskParser;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.ModelBuilder;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Corpus;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class SubjectDataHandler {
	public static final Logger						logger						= Logger
			.getLogger(SubjectDataHandler.class);

	public static HashMap<String, Integer>			subjectTagIdMap				= new HashMap<>();
	public static HashMap<Integer, String>			subjectIdTagMap				= new HashMap<>();

	public static HashMap<String, SubjectDataInfo>	subjectDataMap				= new HashMap<>();
	public static HashMap<Integer, UserInfo>		userMap						= new HashMap<>();

	@Deprecated
	public static HashMap<String, TrainingSet>		randomTrainingSetMap		= new HashMap<>();
	@Deprecated
	public static HashMap<String, TrainingSet>		highestVotedTrainingSetMap	= new HashMap<>();

	// full corpus without any scores, rels, idfs.
	public static HashMap<String, Corpus>			subjectCorpusMap			= new HashMap<>();

	public static HashMap<String, Corpus>			trainingCorpusMap			= new HashMap<>();
	public static HashMap<String, Corpus>			randomTrainingCorpusMap		= new HashMap<>();
	public static HashMap<String, Corpus>			highVoteTrainingCorpusMap	= new HashMap<>();

	public static HashMap<String, Corpus>			annotatedCorpusMap			= new HashMap<>();

	// indexed, with idf, max-sim, cached sim, without scores(set blank for
	// using classifiers).
	public static HashMap<String, Corpus>			indexedCorpusMap			= new HashMap<>();

	public static void main(String[] args) {
		// loadSubjectsFromDataBase();
		// loadUsersFromDataBase();
		// writeUserDataToFile();
		// writeSubjectDataToFile();
		// readUserDataFromFile();

		// readSubjectDataFromFile(true);
		// printStatisticsOfSubjects();
		// generateTrainingSet();
		// writeTrainingSetToFile();
		// writeTrainingSetToAnnotationFiles();
		// readTrainingSetFromFile();

		// buildCorpora();
	}

	public static void generateTrainingSet() {
		Connection conn = DBConnPool.getConnection();
		MyThreadDAO myThreadDAO = new MyThreadDAO(conn);
		SOTagDAO soTagDAO = new SOTagDAO(conn);
		for (String subjectName : subjectDataMap.keySet()) {
			int libId = soTagDAO.getTagIdByTagName(subjectName);
			SubjectDataInfo subjectData = subjectDataMap.get(subjectName);
			// logger.info(subjectData.printCount());

			// Highest Voted Training Set
			TrainingSet highestVotedTrainingSet = new TrainingSet(subjectName);
			int[] highestVotedThreadsId = myThreadDAO.getThreadsIdByLibraryTagIdOrderByVoteLimitK(libId,
					TrainingSet.SIZE);
			for (int i = 0; i < highestVotedThreadsId.length; i++) {
				int threadId = highestVotedThreadsId[i];
				ThreadInfo thread = subjectData.threadMap.get(threadId);
				highestVotedTrainingSet.getSelectedThreads().add(thread);
			}

			highestVotedTrainingSetMap.put(subjectName, highestVotedTrainingSet);
			logger.info("Highest voted training set:" + highestVotedTrainingSet.getSubjectName() + "\t"
					+ highestVotedTrainingSet.getSelectedThreads().size());

			// Random Training Set
			TrainingSet randomTrainingSet = new TrainingSet(subjectName);
			int bias = 20;
			double samplingRatio = ((double) TrainingSet.SIZE + bias) / subjectData.threadMap.size();
			Random random = new Random(System.currentTimeMillis());
			for (Integer threadId : subjectData.threadMap.keySet()) {
				if (random.nextDouble() <= samplingRatio) {
					ThreadInfo thread = subjectData.threadMap.get(threadId);

					// filter out those threads that got a negative score.
					if (thread.getVote() >= 0)
						randomTrainingSet.getSelectedThreads().add(thread);
				}
			}
			randomTrainingSetMap.put(subjectName, randomTrainingSet);
			logger.info("random training set:" + randomTrainingSet.getSubjectName() + "\t"
					+ randomTrainingSet.getSelectedThreads().size());

		}
		DBConnPool.closeConnection(conn);
	}

	public static void loadUsersFromDataBase() {
		Connection conn = DBConnPool.getConnection();

		logger.info("loading users...");
		MyUserDAO userDAO = new MyUserDAO(conn);
		List<UserInfo> users = userDAO.getAll();

		logger.info("loaded " + users.size() + " users.");
		for (UserInfo user : users) {
			SubjectDataHandler.userMap.put(user.getId(), user);
		}

		DBConnPool.closeConnection(conn);
	}

	public static void loadSubjectsTagMap() {
		Connection conn = DBConnPool.getConnection();
		try {
			SOTagDAO soTagDAO = new SOTagDAO(conn);
			String[] subjectTags = Config.getSubjectTags().split(";");
			for (String tag : subjectTags) {
				int id = soTagDAO.getTagIdByTagName(tag);
				subjectTagIdMap.put(tag, id);
				subjectIdTagMap.put(id, tag);

				SubjectDataInfo subjectData = new SubjectDataInfo(tag);
				subjectDataMap.put(tag, subjectData);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
		}
	}

	public static void loadSubjectsFromDataBase() {
		loadSubjectsTagMap();

		Connection conn = DBConnPool.getConnection();
		try {
			MyThreadDAO threadDAO = new MyThreadDAO(conn);
			MyPostDAO postDAO = new MyPostDAO(conn);
			MyCommentDAO commentDAO = new MyCommentDAO(conn);
			ContentDAO contentDAO = new ContentDAO(conn);
			ParagraphDAO paragraphDAO = new ParagraphDAO(conn);
			SentenceDAO sentenceDAO = new SentenceDAO(conn);
			PhraseDAO phraseDAO = new PhraseDAO(conn);
			TaskDAO taskDAO = new TaskDAO(conn);

			List<ThreadInfo> threads = threadDAO.getAll();
			int threadCount = threads.size();
			logger.info("Loaded " + threadCount + " threads.");
			long t0 = System.currentTimeMillis(), t1;
			for (int i = 0; i < threads.size(); i++) {
				ThreadInfo thread = threads.get(i);
				// System.out.println(thread.getQuestionId() + "\tvote:" +
				// thread.getVote());
				int threadId = thread.getId();
				int tagId = thread.getLibraryTagId();
				// libraryTagId is null
				if (tagId <= 0)
					continue;

				String tag = subjectIdTagMap.get(tagId);
				SubjectDataInfo subjectData = subjectDataMap.get(tag);

				List<Integer> answersIdOfThread = new ArrayList<>();
				List<Integer> phrasesIdOfThread = new ArrayList<>();
				Set<Integer> tasksIdOfThread = new HashSet<>();

				List<ContentInfo> contents = new ArrayList<>();

				List<ContentInfo> threadTitleContents = contentDAO
						.getBySourceTypeAndId(ContentInfo.SOURCE_TYPE_THREAD_TITLE, threadId);
				contents.addAll(threadTitleContents);

				List<Integer> contentsIdOfThread = new ArrayList<>();
				for (ContentInfo content : threadTitleContents) {
					contentsIdOfThread.add(content.getId());
				}
				subjectData.threadToContentMap.put(threadId, contentsIdOfThread);

				List<PostInfo> posts = postDAO.getByThreadId(threadId);
				for (PostInfo post : posts) {
					// updated for ac answer type 160817
					if (post.getPostType() == PostInfo.ANSWER_TYPE
							|| post.getPostType() == PostInfo.ACCEPTED_ANSWER_TYPE)
						answersIdOfThread.add(post.getId());

					List<Integer> commentsIdOfPost = new ArrayList<>();

					List<ContentInfo> postBodyContents = contentDAO
							.getBySourceTypeAndId(ContentInfo.SOURCE_TYPE_POST_BODY, post.getId());
					contents.addAll(postBodyContents);

					List<Integer> contentsIdOfPost = new ArrayList<>();
					for (ContentInfo content : postBodyContents) {
						contentsIdOfPost.add(content.getId());
					}
					subjectData.postToContentMap.put(post.getId(), contentsIdOfPost);

					List<CommentInfo> comments = commentDAO.getByPostId(post.getId());
					for (CommentInfo comment : comments) {
						commentsIdOfPost.add(comment.getId());

						List<ContentInfo> commentContents = contentDAO
								.getBySourceTypeAndId(ContentInfo.SOURCE_TYPE_COMMENT_TEXT, comment.getId());
						contents.addAll(commentContents);

						List<Integer> contentsIdOfComment = new ArrayList<>();
						for (ContentInfo content : commentContents) {
							contentsIdOfComment.add(content.getId());
						}
						subjectData.commentToContentMap.put(comment.getId(), contentsIdOfComment);

						subjectData.commentMap.put(comment.getId(), comment);
					}

					post.setCommentsId(Utils.convertIntegerListToIntArray(commentsIdOfPost));
					subjectData.postMap.put(post.getId(), post);

				}

				thread.setAnswersId(Utils.convertIntegerListToIntArray(answersIdOfThread));
				// System.err.println(Arrays.toString(thread.getAnswersId()));
				// System.err.println("================"+thread.getAcceptedAnswerId());
				subjectData.threadMap.put(threadId, thread);
				// System.err.println("answers of thread:" + threadId +
				// Arrays.toString(thread.getAnswersId()));

				for (ContentInfo content : contents) {
					List<Integer> paragraphsIdOfContent = new ArrayList<>();

					List<ParagraphInfo> paragraphs = paragraphDAO.getByParentId(content.getId());
					for (ParagraphInfo paragraph : paragraphs) {
						paragraphsIdOfContent.add(paragraph.getId());
						List<Integer> sentencesIdOfParagraph = new ArrayList<>();

						List<SentenceInfo> sentences = sentenceDAO.getByParentId(paragraph.getId());
						for (SentenceInfo sentence : sentences) {
							sentencesIdOfParagraph.add(sentence.getId());
							List<Integer> phrasesIdOfSentence = new ArrayList<>();

							/**
							 * retrieve all phrases or only those valid ones?
							 * (proofscore >= threshold)
							 **/
							// all phrases
							// List<PhraseInfo> phrases =
							// phraseDAO.getByParentId(sentence.getId());
							// valid phrases
							List<PhraseInfo> phrases = phraseDAO.getPhrasesByParentIdByScoreThreshold(
									sentence.getId(), TaskParser.PHRASE_SCORE_THRESHOLD);

							for (PhraseInfo phrase : phrases) {
								// recover proofs from string 160818
								phrase.setProofs(Proof.extractProofs(phrase.getProofString()));

								phrasesIdOfSentence.add(phrase.getId());
								phrasesIdOfThread.add(phrase.getId());

								if (phrase.getTaskId() > 0) {
									TaskInfo task = taskDAO.getById(phrase.getTaskId());
									int taskId = task.getId();
									if (subjectData.taskMap.get(taskId) != null) {
										// existed, duplicate task
										// add this thread to task's
										// be-mentioned threads list
										subjectData.taskToThreadsMap.get(taskId).add(threadId);
										subjectData.taskToPhrasesMap.get(taskId).add(phrase.getId());
									}
									else {
										// new come-acrossed task
										subjectData.taskMap.put(taskId, task);

										Set<Integer> threadList = new HashSet<>();
										threadList.add(threadId);
										subjectData.taskToThreadsMap.put(taskId, threadList);

										List<Integer> phraseList = new ArrayList<>();
										phraseList.add(phrase.getId());
										subjectData.taskToPhrasesMap.put(taskId, phraseList);
									}
									// System.err.println("task-thread map:" +
									// taskId
									// +
									// subjectData.taskToThreadsMap.get(taskId));
									// System.err.println("task-phrase map:" +
									// taskId
									// +
									// subjectData.taskToPhrasesMap.get(taskId));
									tasksIdOfThread.add(taskId);
								}
								subjectData.phraseMap.put(phrase.getId(), phrase);
							}
							sentence.setPhrasesId(Utils.convertIntegerListToIntArray(phrasesIdOfSentence));
							subjectData.sentenceMap.put(sentence.getId(), sentence);
							// System.err.println("phrases of sentence:" +
							// sentence.getId()
							// + Arrays.toString(sentence.getPhrasesId()));

						}
						paragraph.setSentencesId(Utils.convertIntegerListToIntArray(sentencesIdOfParagraph));
						subjectData.paragraphMap.put(paragraph.getId(), paragraph);
						// System.err.println("sents of paras:" +
						// paragraph.getId()
						// + Arrays.toString(paragraph.getSentencesId()));
					}
					content.setParagraphsId(Utils.convertIntegerListToIntArray(paragraphsIdOfContent));
					subjectData.contentMap.put(content.getId(), content);
					// System.err.println("paras of content:" + content.getId()
					// + Arrays.toString(content.getParagraphsId()));
				}

				subjectData.threadToPhrasesMap.put(threadId, phrasesIdOfThread);
				subjectData.threadToTasksMap.put(threadId, tasksIdOfThread);
				// System.err.println(
				// "thread-phrase map:" + threadId +
				// subjectData.threadToPhrasesMap.get(threadId));
				// System.err
				// .println("thread-task map:" + threadId +
				// subjectData.threadToTasksMap.get(threadId));
				// System.err.println(
				// "thread-content map:" + threadId +
				// subjectData.threadToContentMap.get(threadId));

				if (i % 100 == 99) {
					t1 = System.currentTimeMillis();
					float avrg = (t1 - t0) / (float) (i + 1);
					float remaining = (threadCount - i - 1) * avrg / 1000;
					logger.info("threads handled:" + (i + 1) + " avrg_time:" + avrg + "ms. remaining:"
							+ remaining + "s.");
				}

			}

			for (String key : subjectDataMap.keySet()) {
				SubjectDataInfo subjectData = subjectDataMap.get(key);

				for (Integer taskId : subjectData.taskMap.keySet()) {
					// set phrase ids array for each task in this subject.
					int[] phrasesId = Utils
							.convertIntegerListToIntArray(subjectData.taskToPhrasesMap.get(taskId));
					subjectData.taskMap.get(taskId).setPhrasesId(phrasesId);
				}
				// logger.info(subjectData.printCount());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
		}
	}

	public static void writeUserDataToFile() {
		String path = Config.getSubjectDataDir() + File.separator + "userMap" + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(SubjectDataHandler.userMap, path);
		logger.info("** user data has been written to " + path);

	}

	public static void readUserDataFromFile() {
		String userMapPath = Config.getSubjectDataDir() + File.separator + "userMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		SubjectDataHandler.userMap = (HashMap<Integer, UserInfo>) ObjectIO.readObject(new File(userMapPath));

		logger.info("user map:" + SubjectDataHandler.userMap.size());
	}

	public static void writeSubjectDataToFile() {
		// write separately
		for (String key : subjectDataMap.keySet()) {
			SubjectDataInfo subjectData = subjectDataMap.get(key);
			String path = Config.getSubjectDataDir() + File.separator + key + ObjectIO.DAT_FILE_EXTENSION;
			ObjectIO.writeObject(subjectData, path);
			logger.info("** [" + key + "] subject data has been written to " + path);
		}

		// all in one file
		String allSubjectDataMapPath = Config.getSubjectDataDir() + File.separator + "subjectDataMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(subjectDataMap, allSubjectDataMapPath);
		logger.info("** subject data map has been written to:" + allSubjectDataMapPath);
	}

	public static SubjectDataInfo readSubjectDataFromFile(String subjectName) {
		logger.info("read subject data for one subject: " + subjectName);
		String subjectDataPath = Config.getSubjectDataDir() + File.separator + subjectName
				+ ObjectIO.DAT_FILE_EXTENSION;
		SubjectDataInfo subjectData = (SubjectDataInfo) ObjectIO.readObject(new File(subjectDataPath));
		subjectDataMap.put(subjectName, subjectData);
		logger.info(subjectData.printCount());
		return subjectData;
	}

	public static void readSubjectDataFromFile(boolean loadFromAllInOneFile) {
		logger.info("read subject data from file....");
		if (loadFromAllInOneFile) {
			// read subject data from the all-in-one file.
			String allSubjectDataMapPath = Config.getSubjectDataDir() + File.separator + "subjectDataMap"
					+ ObjectIO.DAT_FILE_EXTENSION;
			subjectDataMap = (HashMap<String, SubjectDataInfo>) ObjectIO
					.readObject(new File(allSubjectDataMapPath));
			logger.info("loaded subject data map from:" + allSubjectDataMapPath);
			// for (SubjectDataInfo subjectData : subjectDataMap.values()) {
			// logger.info(subjectData.printCount());
			// }
		}
		else {
			// read subject data from separated files
			String[] subjectTags = Config.getSubjectTags().split(";");
			for (String tag : subjectTags) {
				String subjectDataPath = Config.getSubjectDataDir() + File.separator + tag
						+ ObjectIO.DAT_FILE_EXTENSION;
				SubjectDataInfo subjectData = (SubjectDataInfo) ObjectIO
						.readObject(new File(subjectDataPath));
				subjectDataMap.put(tag, subjectData);
				// logger.info(subjectData.printCount());
			}
		}
		logger.info("finished read subject data from file....");
		printStatisticsOfSubjects();
	}

	public static void writeTrainingSetToAnnotationFiles() {
		for (String subjectName : highestVotedTrainingSetMap.keySet()) {
			highestVotedTrainingSetMap.get(subjectName).writeAnnotationTextToFile("highestVoted");
		}
		for (String subjectName : randomTrainingSetMap.keySet()) {
			randomTrainingSetMap.get(subjectName).writeAnnotationTextToFile("random");
		}
		logger.info("[Finished]output the mannually annotation files");

	}

	public static void writeSubjectCorpusDataToFile() {
		String corpusPath = Config.getCorpusDir() + File.separator + "subjectCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		writeCorpusMapToFile(subjectCorpusMap, corpusPath);
	}

	public static void readSubjectCorpusDataFromFile() {
		String corpusPath = Config.getCorpusDir() + File.separator + "subjectCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		subjectCorpusMap = readCorpusMapFromFile(corpusPath);
	}

	public static void writeCorpusMapToFile(HashMap<String, Corpus> corpus, String corpusPath) {
		ObjectIO.writeObject(corpus, corpusPath);
		logger.info("corpus written to:" + corpusPath);
	}

	public static HashMap<String, Corpus> readCorpusMapFromFile(String corpusPath) {
		HashMap<String, Corpus> corpus = (HashMap<String, Corpus>) ObjectIO.readObject(new File(corpusPath));
		logger.info("corpus loaded from: " + corpusPath);
		return corpus;
	}

	public static void writeTrainigCorpusDataToFile() {
		String combinedPath = Config.getTrainingCorpusDir() + File.separator + "trainingCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(trainingCorpusMap, combinedPath);
		logger.info("combined training corpus written to:" + combinedPath);
	}

	public static void readTrainigCorpusDataFromFile() {
		String combinedPath = Config.getTrainingCorpusDir() + File.separator + "trainingCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		trainingCorpusMap = (HashMap<String, Corpus>) ObjectIO.readObject(new File(combinedPath));
		System.out.println(trainingCorpusMap);
		logger.info("combined training corpus loaded from: " + combinedPath);
	}

	public static void writeAnnotatedCorpusDataToFile() {
		String annotatedPath = Config.getTrainingCorpusDir() + File.separator + "annotatedCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(annotatedCorpusMap, annotatedPath);
		logger.info("annotated corpus written to:" + annotatedPath);
	}

	public static void readAnnotatedCorpusDataFromFile() {
		String annotatedPath = Config.getTrainingCorpusDir() + File.separator + "annotatedCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		annotatedCorpusMap = (HashMap<String, Corpus>) ObjectIO.readObject(new File(annotatedPath));
		logger.info("annotated corpus loaded from: " + annotatedPath);
	}

	public static void writeSeparatedTrainigCorpusDataToFile() {
		String randomPath = Config.getTrainingCorpusDir() + File.separator + "randomTrainingCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(randomTrainingCorpusMap, randomPath);
		logger.info("random map written to:" + randomPath);

		String votePath = Config.getTrainingCorpusDir() + File.separator + "highVoteTrainingCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(highVoteTrainingCorpusMap, votePath);
		logger.info("vote map written to:" + votePath);
	}

	public static void readSeparatedTrainigCorpusDataFromFile() {
		String randomPath = Config.getTrainingCorpusDir() + File.separator + "randomTrainingCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		randomTrainingCorpusMap = (HashMap<String, Corpus>) ObjectIO.readObject(new File(randomPath));
		logger.info("random map loaded from: " + randomPath);

		String votePath = Config.getTrainingCorpusDir() + File.separator + "highVoteTrainingCorpusMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		highVoteTrainingCorpusMap = (HashMap<String, Corpus>) ObjectIO.readObject(new File(votePath));
		logger.info("vote map loaded from: " + votePath);

	}

	public static void writeTrainingSetToFile() {
		String randomPath = Config.getTrainingSetDir() + File.separator + "randomTrainingSetMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(randomTrainingSetMap, randomPath);
		logger.info("random map written to:" + randomPath);

		String votePath = Config.getTrainingSetDir() + File.separator + "highestVotedTrainingSetMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(highestVotedTrainingSetMap, votePath);
		logger.info("vote map written to:" + votePath);
	}

	public static void readTrainingSetFromFile() {
		String randomPath = Config.getTrainingSetDir() + File.separator + "randomTrainingSetMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		randomTrainingSetMap = (HashMap<String, TrainingSet>) ObjectIO.readObject(new File(randomPath));
		logger.info("random map loaded from: " + randomPath);

		String votePath = Config.getTrainingSetDir() + File.separator + "highestVotedTrainingSetMap"
				+ ObjectIO.DAT_FILE_EXTENSION;
		highestVotedTrainingSetMap = (HashMap<String, TrainingSet>) ObjectIO.readObject(new File(votePath));
		logger.info("vote map loaded from: " + votePath);
	}

	public static String printStatisticsOfSubjects() {
		StringBuilder sb = new StringBuilder("\n");
		// head line
		sb.append("Subject\t");
		sb.append("Thread\t");
		sb.append("Post\t");
		sb.append("Comment\t");
		sb.append("\t");
		sb.append("Content\t");
		sb.append("Paragraph\t");
		sb.append("Sentence\t");
		sb.append("\t");
		sb.append("Phrase\t");
		sb.append("\t");
		sb.append("Task\t");
		sb.append("\n");

		for (String subjectName : subjectDataMap.keySet()) {
			SubjectDataInfo subjectData = subjectDataMap.get(subjectName);
			sb.append(subjectName + "\t");
			sb.append(subjectData.getThreadMap().size() + "\t");
			sb.append(subjectData.getPostMap().size() + "\t");
			sb.append(subjectData.getCommentMap().size() + "\t");
			sb.append("\t");
			sb.append(subjectData.getContentMap().size() + "\t");
			sb.append(subjectData.getParagraphMap().size() + "\t");
			sb.append(subjectData.getSentenceMap().size() + "\t");
			sb.append("\t");
			sb.append(subjectData.getPhraseMap().size() + "\t");
			sb.append("\t");
			sb.append(subjectData.getTaskMap().size());
			sb.append("\n");
		}
		System.out.println(sb);
		return sb.toString();
	}

	public static void buildCorpora() {
		loadSubjectsTagMap();
		readUserDataFromFile();
		readSubjectDataFromFile(true);

		readTrainingSetFromFile();
		for (String subjectName : subjectTagIdMap.keySet()) {
			Corpus subjectCorpus = ModelBuilder.buildCorpusForSubject(subjectDataMap.get(subjectName));
			subjectCorpusMap.put(subjectName, subjectCorpus);

			TrainingSet hvts = highestVotedTrainingSetMap.get(subjectName);
			Corpus hvcorpus = ModelBuilder.extractTrainingCorpus(subjectCorpus, hvts);
			highVoteTrainingCorpusMap.put(subjectName, hvcorpus);

			TrainingSet rndts = randomTrainingSetMap.get(subjectName);
			Corpus rndcorpus = ModelBuilder.extractTrainingCorpus(subjectCorpus, rndts);
			randomTrainingCorpusMap.put(subjectName, rndcorpus);

			Corpus combinedCorpus = ModelBuilder.combineCorpus(rndcorpus, hvcorpus);
			trainingCorpusMap.put(subjectName, combinedCorpus);
			logger.info("combined map for: " + subjectName);
		}
		writeSubjectCorpusDataToFile();
		writeTrainigCorpusDataToFile();
	}

	@Deprecated
	public static void readTrainingSetsAndCombineCorpus() {
		loadSubjectsTagMap();
		readTrainingSetFromFile();
		readSubjectDataFromFile(true);
		for (String subjectName : subjectTagIdMap.keySet()) {
			TrainingSet hvts = highestVotedTrainingSetMap.get(subjectName);
			Corpus hvcorpus = ModelBuilder.buildCorpusForTrainingSet(subjectDataMap.get(subjectName), hvts);
			highVoteTrainingCorpusMap.put(subjectName, hvcorpus);

			TrainingSet rndts = randomTrainingSetMap.get(subjectName);
			Corpus rndcorpus = ModelBuilder.buildCorpusForTrainingSet(subjectDataMap.get(subjectName), rndts);
			randomTrainingCorpusMap.put(subjectName, rndcorpus);

			Corpus combinedCorpus = ModelBuilder.combineCorpus(rndcorpus, hvcorpus);
			trainingCorpusMap.put(subjectName, combinedCorpus);
			logger.info("combined map for: " + subjectName);
		}
		writeTrainigCorpusDataToFile();
	}

	public static void writeBackPhrasesAndTasks() {
		PhraseDAO pd = new PhraseDAO(DBConnPool.getConnection());
		TaskDAO td = new TaskDAO(DBConnPool.getConnection());
		for (SubjectDataInfo subject : subjectDataMap.values()) {
			logger.info("=====================" + subject.getName());

			for (PhraseInfo phrase : subject.phraseMap.values()) {
				if (phrase.getProofs() == null || phrase.getProofs().size() <= 0) {
					System.err.println("[Err]No Proofs");
					phrase.setProofs(Proof.extractProofs(phrase.getProofString()));
				}
				pd.addPhrase(phrase);
			}
			logger.info(subject.getName() + "\tphrases");
			// for (TaskInfo task : subject.taskMap.values()) {
			// td.addTask(task);
			// }
			// logger.info(subject.getName() + "\ttasks");

		}
	}

	// some threads do not have specified tags, remove then from the tables.
	// this job has been done.
	// no use method any more
	public static void cascadelyRemoveIrrelatedThreadsAndBelongingData() {
		Connection conn = DBConnPool.getConnection();
		try {
			MyThreadDAO threadDAO = new MyThreadDAO(conn);
			MyPostDAO postDAO = new MyPostDAO(conn);
			MyCommentDAO commentDAO = new MyCommentDAO(conn);
			ContentDAO contentDAO = new ContentDAO(conn);
			ParagraphDAO paragraphDAO = new ParagraphDAO(conn);
			SentenceDAO sentenceDAO = new SentenceDAO(conn);
			PhraseDAO phraseDAO = new PhraseDAO(conn);

			List<ThreadInfo> threads = threadDAO.getAll();
			int threadCount = threads.size();
			int deleteCount = 0;
			logger.info("Loaded " + threadCount + " threads.");
			long t0 = System.currentTimeMillis(), t1;
			for (int i = 0; i < threads.size(); i++) {
				if (i % 100 == 99) {
					t1 = System.currentTimeMillis();
					float avrg = (t1 - t0) / (float) (i + 1);
					float remaining = (threadCount - i - 1) * avrg / 1000;
					logger.info("posts handled:" + (i + 1) + " deleted:" + deleteCount + " avrg_time:" + avrg
							+ "ms. remaining:" + remaining + "s.");
				}

				ThreadInfo thread = threads.get(i);
				int tagId = thread.getLibraryTagId();

				// libraryTagId is null, cascadely deletion
				// if tagId>0, continue to next record.
				if (tagId > 0)
					continue;

				threadDAO.deleteById(thread.getId());
				deleteCount++;

				List<ContentInfo> contents = new ArrayList<>();

				List<ContentInfo> threadTitleContents = contentDAO
						.getBySourceTypeAndId(ContentInfo.SOURCE_TYPE_THREAD_TITLE, thread.getId());
				contents.addAll(threadTitleContents);

				List<PostInfo> posts = postDAO.getByThreadId(thread.getId());
				for (PostInfo post : posts) {
					postDAO.deleteById(post.getId());

					List<ContentInfo> postBodyContents = contentDAO
							.getBySourceTypeAndId(ContentInfo.SOURCE_TYPE_POST_BODY, post.getId());
					contents.addAll(postBodyContents);

					List<CommentInfo> comments = commentDAO.getByPostId(post.getId());
					for (CommentInfo comment : comments) {
						commentDAO.deleteById(comment.getId());

						List<ContentInfo> commentContents = contentDAO
								.getBySourceTypeAndId(ContentInfo.SOURCE_TYPE_COMMENT_TEXT, comment.getId());
						contents.addAll(commentContents);
					}
				}

				for (ContentInfo content : contents) {
					contentDAO.deleteById(content.getId());

					List<ParagraphInfo> paragraphs = paragraphDAO.getByParentId(content.getId());
					for (ParagraphInfo paragraph : paragraphs) {
						paragraphDAO.deleteById(paragraph.getId());

						List<SentenceInfo> sentences = sentenceDAO.getByParentId(paragraph.getId());
						for (SentenceInfo sentence : sentences) {
							sentenceDAO.deleteById(sentence.getId());

							List<PhraseInfo> phrases = phraseDAO.getByParentId(sentence.getId());
							for (PhraseInfo phrase : phrases) {
								phraseDAO.deleteById(phrase.getId());
							}
						}
					}
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
		}
	}

	public static SubjectDataInfo retrieveSubjectData(String name, int tagId) {
		SubjectDataInfo subjectData = new SubjectDataInfo(name);
		Connection conn = DBConnPool.getConnection();
		try {

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
		}
		return subjectData;

	}

}
