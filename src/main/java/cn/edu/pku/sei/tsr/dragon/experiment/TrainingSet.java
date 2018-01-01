package cn.edu.pku.sei.tsr.dragon.experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import cn.edu.pku.sei.tsr.dragon.experiment.entity.SubjectDataInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;

public class TrainingSet implements Serializable {
	private static final long	serialVersionUID	= 6067728468351902768L;

	public static final int		SIZE				= 150;
	public static final String	TEXT_SEPARATOR		= "\t";
	public static final String	HTTPLINK_PREFIX		= "http://stackoverflow.com/questions/";

	private String				subjectName;
	private List<ThreadInfo>	selectedThreads		= new ArrayList<>();
	private String				annotationFilePath;

	public TrainingSet(String name) {
		this.subjectName = name;
	}

	public static void main(String[] args) {
		SubjectDataHandler.readSubjectDataFromFile(APILibrary.WEKA);
		SubjectDataHandler.readTrainingSetFromFile();
		TrainingSet ts = SubjectDataHandler.randomTrainingSetMap.get(APILibrary.WEKA);
		// System.out.println(ts.getSubjectName());
		// System.out.println(ts.getAnnotationFilePath());

		List<Document> documents = ts.readAnnotatedDataFromFile(Config.getMannuallyAnnotationDir()
				+ File.separator + APILibrary.WEKA + File.separator + "random-annotated.xls");

		for (Document document : documents) {
			System.out.println("==============");
			System.out.println("[DOC]" + document.getThreadId());
			for (TaskVector taskVector : document.getTaskVectors()) {
				System.out.println("[task]" + taskVector.getAnnotatedScore() + "\t"
						+ taskVector.getTaskInfo().toPlainText());
			}
		}

	}

	public List<Document> readAnnotatedDataFromFile(String filename) {
		SubjectDataInfo subject = SubjectDataHandler.subjectDataMap.get(subjectName);
		List<Integer> threadIdList = new ArrayList<>();
		for (ThreadInfo thread : selectedThreads) {
			threadIdList.add(thread.getId());
		}

		List<Document> documents = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
			String line = reader.readLine();
			if (line == null) {
				// first line: head line
				return null;
			}

			Document document = new Document();
			int[] taskIds = null;
			int threadId = 0;
			TaskVector task = null;
			int lineIndexOfDocument = -1;
			boolean invalidDocument = false, allZeroScore = true;
			int docNo = 0, taskNo = 0, annotatedScore = 0;
			List<String> taskTexts = new ArrayList<>();

			while ((line = reader.readLine()) != null) {
				try {
					if (StringUtils.isBlank(line)) {
						// space line means a new thread.
						if (!allZeroScore && !invalidDocument)
							documents.add(document);

						document = new Document();
						taskIds = null;
						task = null;
						lineIndexOfDocument = -1;
						invalidDocument = false;
						taskTexts = new ArrayList<>();
						continue;
					}
					if (invalidDocument)
						continue;

					lineIndexOfDocument++;
					String[] words = line.split(TEXT_SEPARATOR);
					if (lineIndexOfDocument == 0) {
						if (words.length < 4 || StringUtils.isBlank(words[3])) {
							invalidDocument = true;
							continue;
						}
						else {
							docNo = Integer.valueOf(words[0]).intValue();
						}
					}
					else if (lineIndexOfDocument == 1) {
						int questionId = Integer.valueOf(words[1]).intValue();
						// System.out.println(questionId);
						threadId = subject.getPostMap().get(questionId).getThreadId();
						// System.err.println(threadId);
						if (threadId != selectedThreads.get(docNo - 1).getId()) {
							System.err.println(
									"[ERROR] File thread ID does not match the training set thread id!");
							if (!threadIdList.contains(threadId)) {
								System.err.println("[ERROR] File thread ID isn't in the training set!");
								// invalidDocument = true;
								// continue;
							}
						}
						document.setThreadId(threadId);

						taskIds = Utils.convertIntegerSetToIntArray(subject.threadToTasksMap.get(threadId));
						for (int i : taskIds) {
							taskTexts.add(subject.taskMap.get(i).toPlainText());
						}
						// make up for checking task 1
						TaskInfo firstTask = subject.taskMap.get(taskIds[0]);
						if (!task.getText().equals(firstTask.toPlainText())) {
							System.err.println("[ERROR] Task text: " + task.getText());
							System.err.println("[ERROR] Task does not match in order!");
							if (!taskTexts.contains(task.getText())) {
								System.err.println("[ERROR] Task isn't in the training set!");
								continue;
							}
						}

						if (words.length < 5 || StringUtils.isBlank(words[4])) {
							// only 1 task in this thread, empty 2nd line
							continue;
						}
					}

					annotatedScore = Integer.valueOf(words[2]).intValue();
					if (annotatedScore < 0) {
						// only in first line
						invalidDocument = true;
						continue;
					}
					if (annotatedScore > 0)
						allZeroScore = false;

					taskNo = Integer.valueOf(words[3]).intValue();
					String taskText = words[4];

					task = new TaskVector(taskText);
					// TODO: check task order(taskNo) (taskText) and get
					// taskInfo id
					task.setText(taskText);// TODO
					task.setAnnotatedScore(annotatedScore);

					if (taskIds != null) {
						// check task with mapped tasks related to the thread
						TaskInfo taskInMap = subject.taskMap.get(taskIds[taskNo - 1]);
						if (!task.getText().equals(taskInMap.toPlainText())) {
							System.err.println("[ERROR] Task text: " + task.getText());
							System.err.println("[ERROR] Task does not match in order!");
							// invalidDocument = true;
							if (!taskTexts.contains(task.getText())) {
								System.err.println("[ERROR] Task isn't in the training set!");
								continue;
							}
						}
					}

					document.getTaskVectors().add(task);
				}
				catch (Exception e) {
					System.err.println("[ERROR] ThreadId:" + threadId + "\tDocNo:" + docNo + "\tTaskNo:"
							+ taskNo + "\tTaskIds:" + Arrays.toString(taskIds));
					e.printStackTrace();
				}
			}

			if (document.getThreadId() > 0) {
				// make up for the last document
				documents.add(document);
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return documents;
	}

	public File writeAnnotationTextToFile(String filename) {
		FileWriter fw;
		try {
			annotationFilePath = Config.getTrainingSetDir() + File.separator + subjectName + File.separator
					+ filename + ".xls";
			File file = new File(annotationFilePath);

			File parentDir = file.getParentFile();
			if (!parentDir.exists())
				parentDir.mkdirs();

			fw = new FileWriter(file);
			fw.write(this.toAnnotationText());
			fw.flush();
			fw.close();
			return file;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String toAnnotationText() {
		StringBuilder sb = new StringBuilder();

		// head line
		sb.append("No." + TEXT_SEPARATOR);
		sb.append("Thread" + TEXT_SEPARATOR);
		sb.append("Score" + TEXT_SEPARATOR);
		sb.append("TaskNo." + TEXT_SEPARATOR);
		sb.append("Task" + TEXT_SEPARATOR);
		sb.append("\n");

		for (int i = 0; i < selectedThreads.size(); i++) {
			sb.append(getAnnotationTextForThread(selectedThreads.get(i), i + 1));
			sb.append("\n"); // space line
		}
		return sb.toString();
	}

	public String getAnnotationTextForThread(ThreadInfo thread, int threadNo) {
		StringBuilder sb = new StringBuilder();

		SubjectDataInfo subjectData = SubjectDataHandler.subjectDataMap.get(subjectName);
		Set<Integer> tasksIdSet = subjectData.threadToTasksMap.get(thread.getId());
		int[] tasksId = Utils.convertIntegerSetToIntArray(tasksIdSet);

		// first line
		sb.append(threadNo + TEXT_SEPARATOR);
		String httpLink = HTTPLINK_PREFIX + thread.getQuestionId();
		String excelHyperlink = "=HYPERLINK(\"" + httpLink + "\",\"" + thread.getTitle() + "\")";
		sb.append(excelHyperlink + TEXT_SEPARATOR);
		sb.append(/* reserved for score */0 + TEXT_SEPARATOR);
		if (tasksId.length >= 1) {
			sb.append("1" + TEXT_SEPARATOR);
			String taskText = subjectData.getTaskMap().get(tasksId[0]).getText();
			sb.append(TaskInfo.parseTaskStringToPlainText(taskText));
		}
		sb.append("\n");

		// second line
		sb.append(/* below thread no. */ TEXT_SEPARATOR);
		sb.append(thread.getQuestionId() + TEXT_SEPARATOR);
		sb.append(/* reserved for score */0 + TEXT_SEPARATOR);
		if (tasksId.length >= 2) {
			sb.append("2" + TEXT_SEPARATOR);
			String taskText = subjectData.getTaskMap().get(tasksId[1]).getText();
			sb.append(TaskInfo.parseTaskStringToPlainText(taskText));
		}
		sb.append("\n");

		// 3rd line to the end
		for (int i = 2; i < tasksId.length; i++) {
			sb.append(/* below thread no. */ TEXT_SEPARATOR);
			sb.append(/* below thread info */ TEXT_SEPARATOR);
			sb.append(/* reserved for score */0 + TEXT_SEPARATOR);
			sb.append((i + 1) + TEXT_SEPARATOR);
			String taskText = subjectData.getTaskMap().get(tasksId[i]).getText();
			sb.append(TaskInfo.parseTaskStringToPlainText(taskText));
			sb.append("\n");
		}

		return sb.toString();
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public List<ThreadInfo> getSelectedThreads() {
		return selectedThreads;
	}

	public void setSelectedThreads(List<ThreadInfo> selectedThreads) {
		this.selectedThreads = selectedThreads;
	}

	public String getAnnotationFilePath() {
		return annotationFilePath;
	}

	public void setAnnotationFilePath(String annotationFilePath) {
		this.annotationFilePath = annotationFilePath;
	}
}
