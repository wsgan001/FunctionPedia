package cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
import edu.stanford.nlp.patterns.GetPatternsFromDataMultiClass.Flags;
import edu.stanford.nlp.trees.DependencyScoring.Score;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesianLogisticRegression;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class ScorerTrainer {
	public static final Logger					logger				= Logger.getLogger(ScorerTrainer.class);

	public static int							K_FOLD				= 10;
	public static List<TrainingSet>				trainingSets		= new ArrayList<>();
	public static List<VectorFeatureSelector>	featureSelectors	= new ArrayList<>();
	public static int[]							scoreRanges			= new int[] {
			VectorFeatureSelector.SCORE_RANGE_1, VectorFeatureSelector.SCORE_RANGE_2,
			VectorFeatureSelector.SCORE_RANGE_3, VectorFeatureSelector.SCORE_RANGE_5 };
	public static int							currDataRange		= VectorFeatureSelector.SCORE_RANGE_1;

	public static void main(String[] args) {
		SubjectDataHandler.readAnnotatedCorpusDataFromFile();
		initializeTrainingSets();
		initializeFeatureSelectors();
		prepareTrainingData();
		// prepareShortTrainingData();
		outputTrainingFiles(true); // parameter: useShortVector
		writeTrainingSetsListToFile();

		train();
	}

	public static void train() {
		loadTrainingSetsListFromFile();

		for (int t = 0; t < trainingSets.size(); t++) {
			TrainingSet trainingSet = trainingSets.get(t);
			if (trainingSet.getName().contains("[K-Fold]"))
				continue;
			logger.info("Training: " + trainingSet.getName());

			try {
				// build classifiers
				Classifier[] classifiers = { // classifiers
						// new J48(), // k-nearest neighbor
						// new NaiveBayes(), // naive bayes
						// new SMO(), // supporting vector machine
						// new PART(), // ??
						// new LibSVM(), // ??
						// new RandomForest(), // randome forest
						// new DecisionTable(), // ?
						// new DecisionStump(), // decision stump (num)
						// new LinearRegression(), // linear regression (num)

						// new BayesianLogisticRegression(), // (binary)
						// new SimpleLogistic(), // simple logistic regression
						new Logistic(), // logistic regression
						// new MultilayerPerceptron()// Multilayer Perceptron
						// (num)
				};

				for (int i = 0; i < classifiers.length; i++) {
					try {
						String classifierId = "[" + classifiers[i].getClass().getSimpleName() + "]";
						
						if(classifierId.contains("Logistic"))
							classifiers[i].setOptions(new String[]{"-D"});

						boolean isNumericClass = Scorer.isNumericClass(classifiers[i]);
						int scoreRange = TaskShortVector.SCORE_RANGE_2;

						for (VectorFeatureSelector selector : featureSelectors) {
							if (isNumericClass != selector.isNumericClass || scoreRange != selector.range)
								continue;

							logger.info("start training: " + classifierId + " training data set: "
									+ trainingSet.getName() + " feature selector: " + selector.name);

							String trainingFilePath = trainingSet.getTrainingFilePath(selector);
							String testingFilePath = trainingSet.getTestingFilePath(selector);
							if (classifierId.equals("[BayesianLogisticRegression]")) {
								// 二分类？
							}
							logger.info("read training data from file: " + trainingFilePath);

							Instances trainingData = new Instances(
									new BufferedReader(new FileReader(trainingFilePath)));
							trainingData.setClassIndex(trainingData.numAttributes() - 1);

							Instances testingData = new Instances(
									new BufferedReader(new FileReader(testingFilePath)));
							testingData.setClassIndex(trainingData.numAttributes() - 1);

							int testingSize = testingData.numInstances();
							
							Evaluation evaluation;
							if (trainingSet.getName().contains("full")||!trainingSet.getName().contains("Leave-One-Out")) {
								// K_FOLD cross validation
								logger.info("training with cross validation... " + trainingData.numInstances() + " instances... "
										+ classifierId);
								Classifier classifierCopy = Classifier.makeCopy(classifiers[i]);
								evaluation = new Evaluation(trainingData);
								evaluation.crossValidateModel(classifierCopy, trainingData, K_FOLD,
										new Random(System.currentTimeMillis()));

								classifiers[i].buildClassifier(trainingData);
							}
							else {
								logger.info("training... " + trainingData.numInstances() + " instances... "
										+ classifierId);
								classifiers[i].buildClassifier(trainingData);

								evaluation = new Evaluation(trainingData);
								evaluation.evaluateModel(classifiers[i], testingData);
							}

							System.out
									.println(
											// evaluation.toClassDetailsString());
											evaluation.toSummaryString(
													"Evaluation for classifier:" + classifierId
															+ " on the training set:" + trainingSet.getName(),
													true));
							if (!isNumericClass) {
								System.out.println(evaluation.toClassDetailsString());
								System.out.println(evaluation.toMatrixString());
							}
							else
								System.err.println(evaluation.correlationCoefficient());

							if (classifiers[i].getClass().getSimpleName().contains("Logistic")) {
								double[][] coef = ((Logistic) classifiers[i]).coefficients();
								for (int j = 0; j < coef.length; j++) {
									for (int k = 0; k < coef[j].length; k++) {
										System.out.print(coef[j][k] + "\t");
									}
									System.out.println();
								}
							}

							String outputPath = trainingSet.getTrainSetDirPath() + classifierId
									+ File.separator;

							String resultsFileName = classifierId + "." + trainingSet.getName() + "."
									+ (isNumericClass ? "numeric" : "nominal") + "." + scoreRange
									+ ".results";
							FileWriter fw = new FileWriter(ObjectIO.getFile(outputPath + resultsFileName));
							double sqrtsum = 0;
							double diffsum = 0;
							int safeCount = 0;
							double[] predictedValues = new double[testingSize];
							for (int testInstanceIndex = 0; testInstanceIndex < testingSize; testInstanceIndex++) {
								double predictedScore = classifiers[i]
										.classifyInstance(testingData.instance(testInstanceIndex));

								Instance testInstance = trainingSet.getTestingInstances()
										.get(testInstanceIndex);
								float annotatedScore = testInstance.getTaskVector().getAnnotatedScore();

								if (classifierId.equals("[BayesianLogisticRegression]")) {
									double[] distribution = classifiers[i]
											.distributionForInstance(testingData.instance(testInstanceIndex));
									// System.err.println(Arrays.toString(distribution));
									double avrg = 0, sum = 0;
									for (int j = 0; j < distribution.length; j++) {
										avrg += j * distribution[j];
										sum += distribution[j];
									}
									// System.err.println(avrg + "\t" +
									// predictedScore + "\t" + sum);

									diffsum += Math.abs(avrg - annotatedScore);
								}
								else if (!isNumericClass) {
									// 為什麼會有這個？
									// predictedScore /= 10;
									double[] distribution = classifiers[i]
											.distributionForInstance(testingData.instance(testInstanceIndex));
									// System.out.println(Arrays.toString(distribution));
									double avrg = 0, sum = 0;
									for (int j = 0; j < distribution.length; j++) {
										avrg += j * distribution[j] / 10;
										sum += distribution[j];
									}
									// System.err.println(avrg + "\t" +
									// predictedScore + "\t" + sum);

									diffsum += Math.abs(avrg - annotatedScore);
									predictedScore = distribution[1];
								}

								predictedValues[testInstanceIndex] = predictedScore;
								fw.write(predictedScore + "\t" + annotatedScore + "\n");
								if (judgeScorePrecision(annotatedScore, (float) predictedScore,
										TaskShortVector.currScoreRange))
									safeCount++;

								sqrtsum += Math.abs(predictedScore - annotatedScore);
								// System.out
								// .println(VectorFeatureSelector.normalizeAnnotatedScore(annotatedScore,
								// VectorFeatureSelector.SCORE_RANGE_1,
								// VectorFeatureSelector.SCORE_RANGE_1) + "\t"
								// + String.format("%.4f", predictedScore) +
								// "\t"
								// +
								// testInstance.getTaskVector().getTaskInfo().getText()
								// + "\t"
								// + testInstance.getThreadId());
							}
							if (testingSize > 0) {
								logger.info((float) safeCount / testingSize);
								logger.info((float) sqrtsum / testingSize);
								logger.info((float) diffsum / testingSize);
							}
							fw.flush();
							fw.close();

							String classifierFileName = classifierId + "." + trainingSet.getName() + "."
									+ (isNumericClass ? "numeric" : "nominal") + "." + scoreRange + "."
									+ selector.name + ".classifier";
							ObjectIO.writeObject(classifiers[i], outputPath + classifierFileName);
							logger.info("finished training, output to: " + outputPath);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						System.err.println("continueing....");
					}
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void initializeTrainingSets() {
		String[] subjectNames = Config.getTrainingSubjectTags().split(";");
		int subjectCount = subjectNames.length;

		/**
		 * 0 to (n-1): one subject only, 70% for training and 30% for testing.
		 * 
		 * n to (2n-1): leave-one-out, each time leave a subject data out for
		 * testing.
		 * 
		 * 2n to (2n + K_FOLD - 1): 10-fold cross validation, full data.
		 * 
		 * 2n + K_FOLD: full data as training set, train a scorer for final
		 * usage.
		 **/
		int trainSetCount = subjectCount * 2 + K_FOLD + 1;
		trainingSets = new ArrayList<>(trainSetCount);

		/** initialize empty training sets **/
		for (int i = 0; i < subjectCount; i++) {
			String subjectName = subjectNames[i];

			int singleSubjIndex = getSingleSubjectIndex(i);
			TrainingSet singleSubjSet = new TrainingSet("[" + subjectName + "]");
			singleSubjSet.setIndex(singleSubjIndex);
			trainingSets.add(singleSubjIndex, singleSubjSet);
		}

		for (int i = 0; i < subjectCount; i++) {
			String subjectName = subjectNames[i];

			int leaveOutIndex = getLeaveSubjectOutIndex(i, subjectCount);
			TrainingSet leaveOutSet = new TrainingSet(
					TrainingSet.LEAVE_ONE_OUT_FILE_PREFIX + "[" + subjectName + "]");
			leaveOutSet.setIndex(leaveOutIndex);
			trainingSets.add(leaveOutIndex, leaveOutSet);
		}

		for (int i = 0; i < K_FOLD; i++) {
			int kFoldIndex = getKFoldIndex(i, subjectCount);
			TrainingSet kFoldSet = new TrainingSet(TrainingSet.K_FOLD_FILE_PREFIX + (i + 1));
			kFoldSet.setIndex(kFoldIndex);
			trainingSets.add(kFoldIndex, kFoldSet);
		}

		int fullSetIndex = getAllDataIndex(K_FOLD, subjectCount);
		TrainingSet fullSet = new TrainingSet("[full]");
		fullSet.setIndex(fullSetIndex);
		trainingSets.add(fullSetIndex, fullSet);
	}

	public static void prepareTrainingData() {
		String[] subjectNames = Config.getTrainingSubjectTags().split(";");
		int subjectCount = subjectNames.length;
		Random rand = new Random(1);
		/** populate training & testing data into training sets **/
		int globalDocIndex = -1;
		for (int i = 0; i < subjectCount; i++) {
			Corpus corpus = SubjectDataHandler.annotatedCorpusMap.get(subjectNames[i]);
			if (corpus == null)
				continue;

			// for each instance
			for (int j = 0; j < corpus.getDocuments().size(); j++) {
				Document doc = corpus.getDocuments().get(j);
				globalDocIndex++;
				List<TaskVector> taskVectors = doc.getTaskVectors();

				boolean repeat = false;
				for (int tvi = 0; tvi < taskVectors.size(); tvi++) {
					TaskVector taskVector = taskVectors.get(tvi);
					float score = VectorFeatureSelector.normalizeAnnotatedScore(
							taskVector.getAnnotatedScore(), currDataRange,
							VectorFeatureSelector.SCORE_RANGE_1);
					// filter out those data without manual annotation
					if (score <= Integer.MIN_VALUE)
						continue;
					if (score <= 0) {
						if (rand.nextFloat() > 0.3f)
							continue;
					}
					else if (score == 0.5) {
						if (rand.nextFloat() > 0.8f)
							continue;
					}
					else if (score >= 0.8) {
						if (!repeat) {
							tvi--;// repeat current instance
							repeat = true;
						}
						else
							repeat = false;
					}
					
//					score = VectorFeatureSelector.normalizeAnnotatedScore(
//							taskVector.getAnnotatedScore(), currDataRange,
//							VectorFeatureSelector.SCORE_RANGE_2);
//					System.err.println(taskVector.getAnnotatedScore()+"\t"+score);

					Instance currInstance = new Instance();
					// currInstance.setData(taskVector.getInstanceData());
					currInstance.setSubjectName(corpus.getSubjectName());
					currInstance.setThreadId(doc.getThreadId());
					currInstance.setTaskVectorIndexInDocument(tvi);
					currInstance.setTaskVector(taskVector);

					// Reducing numbers of task features, use short vector for
					// training
					// TaskShortVector shortVector = new
					// TaskShortVector(taskVector);
					// currInstance.setTaskShortVector(shortVector);

					// change to k-fold @170829
					// For single subject train & test, 30% for testing 70% for
					// training
//					if (j % 10 == 2 || j % 10 == 5 || j % 10 == 8) {
//						trainingSets.get(getSingleSubjectIndex(i)).addTestingInstance(currInstance);
//					}
//					else {
//						trainingSets.get(getSingleSubjectIndex(i)).addTrainingInstance(currInstance);
//					}
					trainingSets.get(getSingleSubjectIndex(i)).addTrainingInstance(currInstance);
					trainingSets.get(getSingleSubjectIndex(i)).addTestingInstance(currInstance);

					// For leave-one(subject)-out train & test
					for (int lo = 0; lo < subjectCount; lo++) {
						if (lo == i) {
							// when lo==i, it is time to leave this subject out
							// for testing.
							trainingSets.get(getLeaveSubjectOutIndex(lo, subjectCount))
									.addTestingInstance(currInstance);
						}
						else {
							trainingSets.get(getLeaveSubjectOutIndex(lo, subjectCount))
									.addTrainingInstance(currInstance);
						}
					}

					// all data, k-fold
					trainingSets.get(getAllDataIndex(K_FOLD, subjectCount)).addTrainingInstance(currInstance);
					trainingSets.get(getAllDataIndex(K_FOLD, subjectCount)).addTestingInstance(currInstance);
					// actually no need for test

					// k-fold
					for (int k = 0; k < K_FOLD; k++) {
						// for No.k set in k-fold validation, when doc
						// global-index == k (mod K_FOLD), leave the instance
						// for testing
						if (globalDocIndex % K_FOLD == k) {
							trainingSets.get(getKFoldIndex(k, subjectCount)).addTestingInstance(currInstance);
						}
						else {
							trainingSets.get(getKFoldIndex(k, subjectCount))
									.addTrainingInstance(currInstance);
						}
					}
				}
			}
		}

		logger.info("finished preparing data");
	}
	//

	public static void outputTrainingFiles(boolean useShortVector) {
		/** output training files and training sets objects **/
		for (int i = 0; i < trainingSets.size(); i++) {
			TrainingSet trainingSet = trainingSets.get(i);

			for (VectorFeatureSelector selector : featureSelectors) {
				ObjectIO.writeString(trainingSet.getTrainingInstancesFileContent(selector),
						trainingSet.getTrainingFilePath(selector));
				ObjectIO.writeString(trainingSet.getTestingInstancesFileContent(selector),
						trainingSet.getTestingFilePath(selector));
			}

			logger.info("written train & test data for training: " + trainingSet.getName());
		}
	}

	public static void writeTrainingSetsListToFile() {
		ObjectIO.writeObject(trainingSets,
				Config.getScorerTrainingDir() + File.separator + "trainingSetList.dat");
		logger.info("written training set list to file...");
	}

	public static void loadTrainingSetsListFromFile() {
		trainingSets = (List<TrainingSet>) ObjectIO
				.readObject(new File(Config.getScorerTrainingDir() + File.separator + "trainingSetList.dat"));
		logger.info("loaded training sets from file.");
	}

	public static int getSingleSubjectIndex(int i) {
		return i;
	}

	public static int getLeaveSubjectOutIndex(int i, int subjectCount) {
		return i + subjectCount;
	}

	public static int getKFoldIndex(int i, int subjectCount) {
		return subjectCount * 2 + i;
	}
	public static int getAllDataIndex(int k_fold, int subjectCount) {
		return subjectCount * 2 + k_fold;
	}

	public static boolean judgeScorePrecision(float annotatedScore, float predictedScore, int range) {
		switch (range) {
		case VectorFeatureSelector.SCORE_RANGE_1:
			if (annotatedScore >= 1) {
				if (predictedScore > 0.8)
					return true;
			}
			else if (annotatedScore >= 0.8) {
				if (predictedScore >= 0.6 && predictedScore <= 0.8)
					return true;
			}
			else if (annotatedScore >= 0.5) {
				if (predictedScore < 0.6 && predictedScore >= 0.3)
					return true;
			}
			else if (annotatedScore < 0.5) {
				if (predictedScore < 0.3)
					return true;
			}
			return false;
		case VectorFeatureSelector.SCORE_RANGE_3:
			if (annotatedScore >= 3) {
				if (predictedScore > 2.25)
					return true;
			}
			else if (annotatedScore >= 2) {
				if (predictedScore >= 1.5 && predictedScore <= 2.25)
					return true;
			}
			else if (annotatedScore >= 1) {
				if (predictedScore < 1.5 && predictedScore >= 0.75)
					return true;
			}
			else if (annotatedScore < 1) {
				if (predictedScore < 0.75)
					return true;
			}
			return false;
		case VectorFeatureSelector.SCORE_RANGE_5:
			if (annotatedScore >= 5) {
				if (predictedScore > 3)
					return true;
			}
			else if (annotatedScore >= 3) {
				if (predictedScore >= 1 && predictedScore <= 3)
					return true;
			}
			else if (annotatedScore >= 0) {
				if (predictedScore < 1 && predictedScore >= -2)
					return true;
			}
			else if (annotatedScore < 0) {
				if (predictedScore < -2)
					return true;
			}
			return false;
		default:
			return annotatedScore == predictedScore;
		}
	}

	public static List<VectorFeatureSelector> initializeFeatureSelectors() {
		if (featureSelectors == null)
			featureSelectors = new ArrayList<>();

		// VectorFeatureSelector fullSelector =
		// VectorFeatureSelector.getFullFeatureSelector();
		VectorFeatureSelector shortSelector = VectorFeatureSelector.getShortestFeatureSelector();
		VectorFeatureSelector mediumSelector = VectorFeatureSelector.getMediumFeatureSelector();
		VectorFeatureSelector longSelector = VectorFeatureSelector.getLongFeatureSelector();
		VectorFeatureSelector tfBaseSelector = VectorFeatureSelector.getTFBaselineFeatureSelector();
		VectorFeatureSelector tfSelector = VectorFeatureSelector.getTermFrequencySelector();
		VectorFeatureSelector pagerankSelector = VectorFeatureSelector.getPageRankSelector();
		VectorFeatureSelector phraseSyntaxSelector = VectorFeatureSelector.getPhraseSyntaxSelector();
		VectorFeatureSelector positionSelector = VectorFeatureSelector.getPositionSelector();
		VectorFeatureSelector contextSelector = VectorFeatureSelector.getContextSelector();
		VectorFeatureSelector contextTFSelector = VectorFeatureSelector.getContextTFSelector();	
		VectorFeatureSelector soMetaSelector=VectorFeatureSelector.getStackOverflowMetaSelector();
		

		for (int range : scoreRanges) {
			VectorFeatureSelector selector;

			// selector = fullSelector.clone();
			// selector.range = range;
			// selector.isNumericClass = true;
			// featureSelectors.add(selector);
			//
			// selector = fullSelector.clone();
			// selector.range = range;
			// selector.isNumericClass = false;
			// featureSelectors.add(selector);

			selector = shortSelector.clone();
			selector.range = range;
			selector.isNumericClass = true;
			featureSelectors.add(selector);

			selector = shortSelector.clone();
			selector.range = range;
			selector.isNumericClass = false;
			featureSelectors.add(selector);

			selector = mediumSelector.clone();
			selector.range = range;
			selector.isNumericClass = true;
			featureSelectors.add(selector);

			selector = mediumSelector.clone();
			selector.range = range;
			selector.isNumericClass = false;
			featureSelectors.add(selector);

			selector = longSelector.clone();
			selector.range = range;
			selector.isNumericClass = true;
			featureSelectors.add(selector);

			selector = longSelector.clone();
			selector.range = range;
			selector.isNumericClass = false;
			featureSelectors.add(selector);

			selector = tfBaseSelector.clone();
			selector.range = range;
			selector.isNumericClass = true;
			featureSelectors.add(selector);

			selector = tfBaseSelector.clone();
			selector.range = range;
			selector.isNumericClass = false;
			featureSelectors.add(selector);
		}

		return featureSelectors;
	}

}
