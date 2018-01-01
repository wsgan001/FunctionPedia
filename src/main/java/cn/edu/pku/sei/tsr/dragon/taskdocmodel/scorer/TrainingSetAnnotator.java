package cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.experiment.SubjectDataHandler;
import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.ModelBuilder;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Corpus;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.VectorFeatureSelector;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class TrainingSetAnnotator {
	public static final Logger	logger						= Logger.getLogger(TrainingSetAnnotator.class);

	public static final String	OUTPUTFILE_CELL_SEPARATOR	= "\t";
	public static final String	OUTPUTFILE_HTTPLINK_PREFIX	= "http://stackoverflow.com/questions/";

	public static void main(String[] args) {
		SubjectDataHandler.loadSubjectsTagMap();
		SubjectDataHandler.readTrainigCorpusDataFromFile();

		SubjectDataHandler.annotatedCorpusMap = readAnnotatedDataFromAllSubjects();
		for (String subj : SubjectDataHandler.annotatedCorpusMap.keySet()) {
			System.out.println();
			logger.info("===============Subject:" + subj + "================");
			System.out.println();
			Corpus c = SubjectDataHandler.annotatedCorpusMap.get(subj);
			if (c == null)
				continue;
			logger.info("annotated file count: " + c.getDocuments().size());
			for (Integer id : c.getThreadIdToDocumentIndexMap().keySet()) {
				Document doc = c.getDocumentByThreadId(id);
				System.out.println("--------------Document:" + id);
				System.out.println("Is annotated? " + doc.isAnnotated());
				for (TaskVector vector : doc.getTaskVectors()) {
					System.out.println("*************");
					System.out.println("[vector]" + vector.getTaskInfo().toPlainText());
					System.out.println("[vector score]" + vector.getAnnotatedScore());
					// System.out.println(vector.toString());
				}
			}
		}
		SubjectDataHandler.writeAnnotatedCorpusDataToFile();
	}

	public static HashMap<String, Corpus> readAnnotatedDataFromAllSubjects() {
		// need: trainingcorpus; subject data; subjecttags

		HashMap<String, Corpus> annotatedCorpusMap = new HashMap<>();
		for (String subjectName : APILibrary.getSubjectNames()) {
			System.out.println(SubjectDataHandler.trainingCorpusMap);
			Corpus trainingCorpus = SubjectDataHandler.trainingCorpusMap.get(subjectName);

			String highVoteTSPath = Config.getMannuallyAnnotationDir() + File.separator + subjectName
					+ File.separator + "highestVoted-annotated.xls";
			String randomPath = Config.getMannuallyAnnotationDir() + File.separator + subjectName
					+ File.separator + "random-annotated.xls";

			Corpus annotateRandomCorpus = readAnnotatedDataFromFile(trainingCorpus, randomPath);
			Corpus annotateHighVoteCorpus = readAnnotatedDataFromFile(trainingCorpus, highVoteTSPath);

			Corpus annotatedCorpus = null;

			if (annotateRandomCorpus != null && annotateHighVoteCorpus != null)
				annotatedCorpus = ModelBuilder.combineCorpus(annotateRandomCorpus, annotateHighVoteCorpus);
			else if (annotateRandomCorpus != null)
				annotatedCorpus = annotateRandomCorpus;
			else if (annotateHighVoteCorpus != null)
				annotatedCorpus = annotateHighVoteCorpus;
			else
				annotatedCorpus = null;

			annotatedCorpusMap.put(subjectName, annotatedCorpus);
		}
		return annotatedCorpusMap;

	}

	public static Corpus readAnnotatedDataFromFile(Corpus tsCorpus, String filePath) {
		// SubjectDataInfo subject =
		// SubjectDataHandler.subjectDataMap.get(tsCorpus.getSubjectName());
		Corpus annotatedCorpus = new Corpus(tsCorpus.getSubjectName());
		annotatedCorpus.setType(Corpus.TYPE_ANNOTATED_TRAINING_SET);

		Connection conn = DBConnPool.getConnection();
		MyPostDAO postDAO = new MyPostDAO(conn);

		try {
			File file = new File(filePath);
			if (!file.exists()) {
				logger.info("[File not existed]" + filePath);
				return null;
			}
			else
				logger.info("read annotated data from file: " + filePath);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			if (line == null) {
				// first line: head line
				reader.close();
				return null;
			}

			Document currDocument = null;
			TaskVector currTaskVector = null;

			int docNo = 0, taskNo = 0, annotatedScore = 0, threadId = 0;
			String currTaskText = null;

			int lineIndexOfDocument = -1;
			boolean invalidDocument = false, allZeroScore = true;

			while ((line = reader.readLine()) != null) {
				try {
					if (StringUtils.isBlank(line)) {
						// space line means a new thread.

						// add valid and annotated document into corpus
						if (!allZeroScore && !invalidDocument) {
							currDocument.setAnnotated(true);
							annotatedCorpus.addDocumentWithReplacement(currDocument);
							// tsCorpus.removeDocument(currDocument);
						}

						currDocument = null;
						currTaskVector = null;
						lineIndexOfDocument = -1;
						invalidDocument = false;
						allZeroScore = true;
						continue;
					}

					lineIndexOfDocument++;
					String[] words = line.split(OUTPUTFILE_CELL_SEPARATOR);

					if (lineIndexOfDocument == 0) {
						if (words.length < 5 || StringUtils.isBlank(words[3])) {
							invalidDocument = true;
							continue;
						}
						else {// no actual use
							docNo = Integer.valueOf(words[0]).intValue();
						}
					}
					else if (lineIndexOfDocument == 1) {
						int questionId = Integer.valueOf(words[1]).intValue();
						threadId = postDAO.getThreadIdById(questionId);
						// subject.getPostMap().get(questionId).getThreadId();

						currDocument = tsCorpus.getDocumentByThreadId(threadId);
						if (currDocument == null) {
							invalidDocument = true;
							logger.error("[ERROR] Can not find the document! File thread ID [" + threadId
									+ "] isn't in the training set!");
						}

						if (invalidDocument)
							continue;

						// currTask is the first task in the first line
						currTaskVector = currDocument.getTaskVectors().get(0);

						if (!currTaskText.equals(currTaskVector.getTaskInfo().toPlainText())) {
							logger.error("[ERROR] Task text: " + currTaskText
									+ " \tTask does not match in order!");
							Integer actualTaskIndex = currDocument.getTaskTextToVectorIndexMap()
									.get(currTaskText);
							if (actualTaskIndex == null || actualTaskIndex < 0) {
								System.err.println("[ERROR] Task isn't in the training set!");
								continue;
							}
							currTaskVector = currDocument.getTaskVectors().get(actualTaskIndex);
						}
						currTaskVector.setAnnotatedScore(VectorFeatureSelector.normalizeAnnotatedScore(annotatedScore,
								VectorFeatureSelector.SCORE_RANGE_ANNOTATED, TaskVector.currScoreRange));

						if (words.length < 5 || StringUtils.isBlank(words[4])) {
							// only 1 task in this thread, empty 2nd line
							continue;
						}
					}

					if (invalidDocument)
						continue;

					annotatedScore = Integer.valueOf(words[2]).intValue();
					if (annotatedScore < 0) {
						// only in first line
						invalidDocument = true;
						continue;
					}
					if (annotatedScore > 0)
						allZeroScore = false;

					taskNo = Integer.valueOf(words[3]).intValue();
					currTaskText = words[4];

					// the main goal of the while block is to set annotated
					// score for each task.
					if (lineIndexOfDocument == 0)// currDocument == null) {
						// logger.error("strange!!!!");
						continue;

					if (currDocument.getTaskVectors() == null
							|| taskNo > currDocument.getTaskVectors().size())
						logger.error("[ERROR] Task text: " + currTaskText + " \tTaskNo out of bounds!"
								+ taskNo + "(taskNo) vectors:" + currDocument.getTaskVectors());
					else
						currTaskVector = currDocument.getTaskVectors().get(taskNo - 1);

					if (currTaskVector == null
							|| !currTaskText.equals(currTaskVector.getTaskInfo().toPlainText())) {
						logger.error(
								"[ERROR] Task text: " + currTaskText + " \tTask does not match in order!");
						Integer actualTaskIndex = currDocument.getTaskTextToVectorIndexMap()
								.get(currTaskText);
						if (actualTaskIndex == null || actualTaskIndex < 0) {
							logger.error("[ERROR] Task isn't in the training set!");
							continue;
						}
						currTaskVector = currDocument.getTaskVectors().get(actualTaskIndex);
					}

					currTaskVector.setAnnotatedScore(VectorFeatureSelector.normalizeAnnotatedScore(annotatedScore,
							VectorFeatureSelector.SCORE_RANGE_ANNOTATED, TaskVector.currScoreRange));
				}
				catch (Exception e) {
					logger.error(
							"[Exception] ThreadId:" + threadId + "\tDocNo:" + docNo + "\tTaskNo:" + taskNo);
					e.printStackTrace();
				}
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			DBConnPool.closeConnection(conn);
		}
		logger.info(annotatedCorpus.getSubjectName() + " Annotated corpus size:"
				+ annotatedCorpus.getDocuments().size());
		return annotatedCorpus;
	}

	public static File writeAnnotationTextToFile(Corpus corpus, String filename) {
		FileWriter fw;
		try {
			String annotationFilePath = Config.getTrainingSetDir() + File.separator + corpus.getSubjectName()
					+ File.separator + filename + ".xls";
			File file = new File(annotationFilePath);

			File parentDir = file.getParentFile();
			if (!parentDir.exists())
				parentDir.mkdirs();

			fw = new FileWriter(file);
			fw.write(getAnnotationText(corpus));
			fw.flush();
			fw.close();
			return file;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getAnnotationText(Corpus corpus) {
		StringBuilder sb = new StringBuilder();

		// head line
		sb.append("No." + OUTPUTFILE_CELL_SEPARATOR);
		sb.append("Thread" + OUTPUTFILE_CELL_SEPARATOR);
		sb.append("Score" + OUTPUTFILE_CELL_SEPARATOR);
		sb.append("TaskNo." + OUTPUTFILE_CELL_SEPARATOR);
		sb.append("Task" + OUTPUTFILE_CELL_SEPARATOR);
		sb.append("\n");

		SubjectDataInfo subject = SubjectDataHandler.subjectDataMap.get(corpus.getSubjectName());
		int threadNo = 1;
		for (Integer threadId : corpus.getThreadIdToDocumentIndexMap().keySet()) {
			ThreadInfo threadInfo = subject.threadMap.get(threadId);
			sb.append(getAnnotationTextForThread(corpus, threadInfo, threadNo));
			sb.append("\n"); // space line
			threadNo++;
		}
		return sb.toString();
	}

	public static String getAnnotationTextForThread(Corpus corpus, ThreadInfo thread, int threadNo) {
		StringBuilder sb = new StringBuilder();

		SubjectDataInfo subjectData = SubjectDataHandler.subjectDataMap.get(corpus.getSubjectName());
		Set<Integer> tasksIdSet = subjectData.threadToTasksMap.get(thread.getId());
		int[] tasksId = Utils.convertIntegerSetToIntArray(tasksIdSet);

		// first line
		sb.append(threadNo + OUTPUTFILE_CELL_SEPARATOR);
		String httpLink = OUTPUTFILE_HTTPLINK_PREFIX + thread.getQuestionId();
		String excelHyperlink = "=HYPERLINK(\"" + httpLink + "\",\"" + thread.getTitle() + "\")";
		sb.append(excelHyperlink + OUTPUTFILE_CELL_SEPARATOR);
		sb.append(/* reserved for score */0 + OUTPUTFILE_CELL_SEPARATOR);
		if (tasksId.length >= 1) {
			sb.append("1" + OUTPUTFILE_CELL_SEPARATOR);
			String taskText = subjectData.getTaskMap().get(tasksId[0]).getText();
			sb.append(TaskInfo.parseTaskStringToPlainText(taskText));
		}
		sb.append("\n");

		// second line
		sb.append(/* below thread no. */ OUTPUTFILE_CELL_SEPARATOR);
		sb.append(thread.getQuestionId() + OUTPUTFILE_CELL_SEPARATOR);
		sb.append(/* reserved for score */0 + OUTPUTFILE_CELL_SEPARATOR);
		if (tasksId.length >= 2) {
			sb.append("2" + OUTPUTFILE_CELL_SEPARATOR);
			String taskText = subjectData.getTaskMap().get(tasksId[1]).getText();
			sb.append(TaskInfo.parseTaskStringToPlainText(taskText));
		}
		sb.append("\n");

		// 3rd line to the end
		for (int i = 2; i < tasksId.length; i++) {
			sb.append(/* below thread no. */ OUTPUTFILE_CELL_SEPARATOR);
			sb.append(/* below thread info */ OUTPUTFILE_CELL_SEPARATOR);
			sb.append(/* reserved for score */0 + OUTPUTFILE_CELL_SEPARATOR);
			sb.append((i + 1) + OUTPUTFILE_CELL_SEPARATOR);
			String taskText = subjectData.getTaskMap().get(tasksId[i]).getText();
			sb.append(TaskInfo.parseTaskStringToPlainText(taskText));
			sb.append("\n");
		}

		return sb.toString();
	}
}
