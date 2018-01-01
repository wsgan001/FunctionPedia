package cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.SubjectDataHandler;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Corpus;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Document;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Instance;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.Task;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskShortVector;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TaskVector;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.TrainingSet;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity.VectorFeatureSelector;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class Scorer implements Serializable {
	public static final Logger	logger				= Logger.getLogger(Scorer.class);

	private static final long	serialVersionUID	= -431581942425881324L;

	private Classifier			classifier;
	private static boolean				isNumericClass;
	private int					scoreRange;
	private String				dataSetName;

	public static void main(String[] args) {
		score(true); //useShortVector
		ObjectIO.writeObject(SubjectDataHandler.subjectCorpusMap,
				Config.getSearchDir() + File.separator + "corpusMap.dat");
	}

	public static boolean isNumericClass(Classifier classifier) {
		String classifierName = classifier.getClass().getSimpleName();
		switch (classifierName) {
		case "LinearRegression":
		case "DecisionStump":
		case "MultilayerPerceptron":
			return true;
		default:
			break;
		}
		return false;
	}

	public static void score(Classifier classifier, Corpus corpus, boolean useShortVector) {
		logger.info("prepare all data for scoring...");
		boolean isNumericClass = isNumericClass(classifier);
		TrainingSet dataset = new TrainingSet("scoredata");

		for (Document document : corpus.getDocuments()) {
			List<TaskVector> taskVectors = document.getTaskVectors();
			for (int i = 0; i < taskVectors.size(); i++) {
				TaskVector taskVector = taskVectors.get(i);

				Instance instance = new Instance();
				instance.setSubjectName(corpus.getSubjectName());
				instance.setTaskVector(taskVector);
				instance.setThreadId(document.getThreadId());
				instance.setTaskVectorIndexInDocument(i);
				// instance.setData(taskVector.getInstanceData());
				
				if(useShortVector)
					instance.setTaskShortVector(new TaskShortVector(taskVector));

				dataset.addTestingInstance(instance);
			}
		}

		logger.info("data ready, output to file...");

		String dataFilePath = Config.getScorersDir() + File.separator + "dataToScore.dat";
		ObjectIO.writeString(dataset.getTestingInstancesFileContent(isNumericClass, VectorFeatureSelector.SCORE_RANGE_1,useShortVector),
				dataFilePath);

		logger.info("start to score data....");
		int dataCount = dataset.getTestingInstances().size();
		try {
			Instances inputData = new Instances(new BufferedReader(new FileReader(dataFilePath)));
			inputData.setClassIndex(inputData.numAttributes() - 1);
			for (int i = 0; i < dataCount; i++) {
				double score = classifier.classifyInstance(inputData.instance(i));
				score = Math.min(score, 1); // no more than 1
				score = Math.max(score, 0); // no less than 0
				Instance myInstance = dataset.getTestingInstances().get(i);
				if(useShortVector)
					myInstance.getTaskShortVector().setScore((float) score);
				else
					myInstance.getTaskVector().setScore((float) score);
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("finished data scoring. output data to files.");
		// for (String subjectName :
		// SubjectDataHandler.subjectCorpusMap.keySet()) {
		// Corpus corpus = SubjectDataHandler.subjectCorpusMap.get(subjectName);
		String subjetDataDirPath = Config.getScorerSubjectDataDir() + File.separator + corpus.getSubjectName()
				+ File.separator;
		for (Document document : corpus.getDocuments()) {
			String docFilePath = subjetDataDirPath + document.getThreadId() + ObjectIO.DAT_FILE_EXTENSION;
			StringBuilder docContent = new StringBuilder();
			for (TaskVector taskVector : document.getTaskVectors()) {
				docContent.append(taskVector.getTaskInfo().getText() + "\n");
				docContent.append(taskVector.getScore() + "\n");
			}
			ObjectIO.writeString(docContent.toString(), docFilePath);
		}
		// }
		logger.info("writing finished.");

	}

	public static void score(boolean useShortVector) {
		logger.info("load subjects corpus data...");
		SubjectDataHandler.readSubjectCorpusDataFromFile();
		String classifierPath = Config.getScorersDir() + File.separator + "classifier.file";

		logger.info("load classifier...");
		Classifier classifier = (Classifier) ObjectIO.readObject(classifierPath);

		logger.info("prepare all data for scoring...");
		TrainingSet dataset = new TrainingSet("subject real data");

		for (String subjectName : SubjectDataHandler.subjectCorpusMap.keySet()) {
			Corpus corpus = SubjectDataHandler.subjectCorpusMap.get(subjectName);

			for (Document document : corpus.getDocuments()) {
				List<TaskVector> taskVectors = document.getTaskVectors();
				for (int i = 0; i < taskVectors.size(); i++) {
					TaskVector taskVector = taskVectors.get(i);

					Instance instance = new Instance();
					instance.setSubjectName(subjectName);
					instance.setTaskVector(taskVector);
					instance.setThreadId(document.getThreadId());
					instance.setTaskVectorIndexInDocument(i);
					// instance.setData(taskVector.getInstanceData());
					
					if(useShortVector)
						instance.setTaskShortVector(new TaskShortVector(taskVector));

					dataset.addTestingInstance(instance);
				}
			}
		}

		logger.info("data ready, output to file...");

		String dataFilePath = Config.getScorersDir() + File.separator + "allsubjectdata.dat";
		ObjectIO.writeString(dataset.getTestingInstancesFileContent(isNumericClass, VectorFeatureSelector.SCORE_RANGE_1,useShortVector),
				dataFilePath);

		logger.info("start to score data....");
		int dataCount = dataset.getTestingInstances().size();
		try {
			Instances inputData = new Instances(new BufferedReader(new FileReader(dataFilePath)));
			inputData.setClassIndex(inputData.numAttributes() - 1);
			for (int i = 0; i < dataCount; i++) {
				double score = classifier.classifyInstance(inputData.instance(i));
				// System.err.print(score);
				// double exp = Math.exp(score);
				// score = exp / (1 + exp);
				// System.err.println("\t" + exp + "\t" + score);
				// score = score > 0 ? 1 : 0;
				score = Math.min(score, 1); // no more than 1
				score = Math.max(score, 0); // no less than 0
				Instance myInstance = dataset.getTestingInstances().get(i);
				if(useShortVector)
					myInstance.getTaskShortVector().setScore((float) score);
				else
					myInstance.getTaskVector().setScore((float) score);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		logger.info("finished data scoring. output data to files.");
		for (String subjectName : SubjectDataHandler.subjectCorpusMap.keySet()) {
			Corpus corpus = SubjectDataHandler.subjectCorpusMap.get(subjectName);
			String subjetDataDirPath = Config.getScorerSubjectDataDir() + File.separator
					+ corpus.getSubjectName() + File.separator;
			for (Document document : corpus.getDocuments()) {
				String docFilePath = subjetDataDirPath + document.getThreadId() + ObjectIO.DAT_FILE_EXTENSION;
				StringBuilder docContent = new StringBuilder();
				for (TaskVector taskVector : document.getTaskVectors()) {
					docContent.append(taskVector.getTaskInfo().getText() + "\n");
					docContent.append(taskVector.getScore() + "\n");
				}
				ObjectIO.writeString(docContent.toString(), docFilePath);
			}
		}
		logger.info("writing finished.");
	}
	
//	public String getClassifierIdString()
//	{
//		StringBuilder sb=new StringBuilder();
//		
//	}

	public String getClassifierName() {
		return classifier.getClass().getSimpleName();
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public boolean isNumericClass() {
		return isNumericClass;
	}

	public void setNumericClass(boolean isNumericClass) {
		this.isNumericClass = isNumericClass;
	}

	public int getScoreRange() {
		return scoreRange;
	}

	public void setScoreRange(int scoreRange) {
		this.scoreRange = scoreRange;
	}

	public String getDataSetName() {
		return dataSetName;
	}

	public void setDataSetName(String dataSetName) {
		this.dataSetName = dataSetName;
	}

}
