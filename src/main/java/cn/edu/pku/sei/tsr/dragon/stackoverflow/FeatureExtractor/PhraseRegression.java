package cn.edu.pku.sei.tsr.dragon.stackoverflow.FeatureExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.DecisionStump;
import weka.core.Instances;

/**
 * Created by maxkibble on 16/4/24.
 */
public class PhraseRegression {
	public static void main(String args[]) throws Exception {
		// load data
		String path = "/home/maxkibble/Document/brand_new_data";
		Instances data = new Instances(new BufferedReader(new FileReader(path + "/train_regression.arff")));
		data.setClassIndex(data.numAttributes() - 1);

		File fileDir = new File(path + "/arffFile/poiData/");
		File[] files = fileDir.listFiles();
		for (File file : files) {
			String filename = file.getName();
			Instances testingData = new Instances(
					new BufferedReader(new FileReader(path + "/arffFile/poiData/" + filename)));
			testingData.setClassIndex(testingData.numAttributes() - 1);

			int testingSize = testingData.numInstances();
			// build model
			Classifier[] models = {
					// new J48(),
					// new NaiveBayes(),
					// new PART(),
					// new LibSVM(),
					// new RandomForest(),
					// new DecisionTable(),
					new DecisionStump(), new LinearRegression(), new MultilayerPerceptron(),
					// new Logistic()
					// new BayesianLogisticRegression()
					// new SimpleLogistic()
			};
			for (int i = 0; i < models.length; i++) {
				String output = "";
				PrintStream out = new PrintStream(path + "/regressionResult/poiData/"
						+ models[i].getClass().getSimpleName() + "/" + filename + ".txt");
				models[i].buildClassifier(data);
				double maxValue = -10;
				int maxIdx = 0;
				output += models[i].getClass().getSimpleName() + ":\n";
				for (int j = 0; j < testingSize; j++) {
					double predictedValue = models[i].classifyInstance(testingData.instance(j));
					if (predictedValue > maxValue) {
						maxValue = predictedValue;
						maxIdx = j;
					}
					output += predictedValue + "\n";
				}
				out.println(output);
			}
		}
	}
}