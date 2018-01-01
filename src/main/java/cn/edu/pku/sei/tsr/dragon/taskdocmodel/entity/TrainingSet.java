package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.taskdocmodel.scorer.FeatureExtractor;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class TrainingSet implements Serializable {
	private static final long	serialVersionUID				= -8644316993807418191L;

	public static final String	TRAINING_INSTANCES_FILE_PREFIX	= "training";
	public static final String	TESTING_INSTANCES_FILE_PREFIX	= "testing";
	public static final String	LEAVE_ONE_OUT_FILE_PREFIX		= "[Leave-One-Out]";
	public static final String	K_FOLD_FILE_PREFIX				= "[K-Fold]";

	private String				name;
	private int					index;
	private List<Instance>		trainingInstances				= new ArrayList<>();
	private List<Instance>		testingInstances				= new ArrayList<>();

	public TrainingSet(String name) {
		super();
		this.name = name;
	}

	public void addTrainingInstance(Instance instance) {
		if (trainingInstances == null)
			trainingInstances = new ArrayList<>();
		trainingInstances.add(instance);
	}

	public void addTestingInstance(Instance instance) {
		if (testingInstances == null)
			testingInstances = new ArrayList<>();
		testingInstances.add(instance);
	}

	public String getTrainSetDirPath() {
		return Config.getScorerTrainingDir() + File.separator + name + File.separator;
	}
	
	public String getTrainingFilePath(VectorFeatureSelector selector) {
		return getTrainSetDirPath() + getTrainingInstancesFileName(selector);
	}

	public String getTestingFilePath(VectorFeatureSelector selector) {
		return getTrainSetDirPath() + getTestingInstancesFileName(selector);
	}

	public String getTrainingFilePath(boolean isNumericClass, int range, boolean useShortVector) {
		return getTrainSetDirPath() + getTrainingInstancesFileName(isNumericClass, range, useShortVector);
	}

	public String getTestingFilePath(boolean isNumericClass, int range, boolean useShortVector) {
		return getTrainSetDirPath() + getTestingInstancesFileName(isNumericClass, range, useShortVector);
	}

	public String getTrainingInstancesFileName(VectorFeatureSelector selector) {
		return TRAINING_INSTANCES_FILE_PREFIX + name + (selector.isNumericClass ? "[numeric]" : "[nominal]")
				+ "[range-" + selector.range + "]" + "["+selector.name+"]" + ObjectIO.DAT_FILE_EXTENSION;
	}

	public String getTestingInstancesFileName(VectorFeatureSelector selector) {
		return TESTING_INSTANCES_FILE_PREFIX + name + (selector.isNumericClass ? "[numeric]" : "[nominal]")
				+ "[range-" + selector.range + "]" + "["+selector.name+"]" + ObjectIO.DAT_FILE_EXTENSION;
	}
	
	public String getTrainingInstancesFileName(boolean isNumericClass, int range, boolean useShortVector) {
		return TRAINING_INSTANCES_FILE_PREFIX + name + (isNumericClass ? "[numeric]" : "[nominal]")
				+ "[range-" + range + "]" + (useShortVector? "[ShortVector]" : "[LongVector]") + ObjectIO.DAT_FILE_EXTENSION;
	}

	public String getTestingInstancesFileName(boolean isNumericClass, int range, boolean useShortVector) {
		return TESTING_INSTANCES_FILE_PREFIX + name + (isNumericClass ? "[numeric]" : "[nominal]") + "[range-"
				+ range + "]" + (useShortVector? "[ShortVector]" : "[LongVector]") + ObjectIO.DAT_FILE_EXTENSION;
	}
	
	
	public String getTrainingInstancesFileContent(VectorFeatureSelector selector) {
		StringBuilder sb = new StringBuilder();
		sb.append(selector.getArffHead());
		
		for (Instance instance : trainingInstances) {
			sb.append(instance.getTaskVector().getInstanceData(selector));
		}
		return sb.toString();
	}
	
	public String getTestingInstancesFileContent(VectorFeatureSelector selector) {
		StringBuilder sb = new StringBuilder();
		sb.append(selector.getArffHead());
		
		for (Instance instance : testingInstances) {
			sb.append(instance.getTaskVector().getInstanceData(selector));
		}
		return sb.toString();
	}

//
//	public String getTrainingInstancesFileContent(boolean isNumericClass, int range, boolean useShortVector) {
//		StringBuilder sb = new StringBuilder();
//		if(useShortVector)
//			sb.append(TaskShortVector.getArffHead(isNumericClass, range));
//		else
//			sb.append(TaskVector.getArffHead(isNumericClass, range));
//		
//		for (Instance instance : trainingInstances) {
//			if(useShortVector)
//				sb.append(instance.getTaskShortVector().getInstanceData(range));
//			else 
//				sb.append(instance.getTaskVector().getInstanceData(range));
//		}
//		return sb.toString();
//	}
//
//	public String getTestingInstancesFileContent(boolean isNumericClass, int range, boolean useShortVector) {
//		StringBuilder sb = new StringBuilder();
//		if(useShortVector)
//			sb.append(TaskShortVector.getArffHead(isNumericClass, range));
//		else
//			sb.append(TaskVector.getArffHead(isNumericClass, range));
//		
//		for (Instance instance : testingInstances) {
//			if(useShortVector)
//				sb.append(instance.getTaskShortVector().getInstanceData(range));
//			else 
//				sb.append(instance.getTaskVector().getInstanceData(range));
//		}
//		return sb.toString();
//	}

	// public String getTrainingInstancesFileContent() {
	// StringBuilder sb = new StringBuilder();
	// sb.append(FeatureExtractor.getArffHead());
	// for (Instance instance : trainingInstances) {
	// sb.append(instance.getTaskVector().getInstanceData());
	// }
	// return sb.toString();
	// }
	//
	// public String getTestingInstancesFileContent() {
	// StringBuilder sb = new StringBuilder();
	// sb.append(FeatureExtractor.getArffHead());
	// for (Instance instance : testingInstances) {
	// sb.append(instance.getTaskVector().getInstanceData());
	// }
	// return sb.toString();
	// }

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public List<Instance> getTrainingInstances() {
		return trainingInstances;
	}
	public void setTrainingInstances(List<Instance> trainingInstances) {
		this.trainingInstances = trainingInstances;
	}
	public List<Instance> getTestingInstances() {
		return testingInstances;
	}
	public void setTestingInstances(List<Instance> testingInstances) {
		this.testingInstances = testingInstances;
	}

}
