package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Document implements Serializable {
	private static final long serialVersionUID = 290888110545850026L;

	private HashMap<String, Integer>	taskTextToVectorIndexMap	= new HashMap<>();
	private List<TaskVector>			taskVectors					= new ArrayList<>();
	private int							threadId;
	private boolean						isAnnotated					= false;

	private HashMap<String, Float> maxSimilarityCache = new HashMap<>();

	public Document() {
		super();
	}

	public int addTaskVector(TaskVector vector) {
		String text = vector.getTaskInfo().toPlainText();
		Integer index = taskTextToVectorIndexMap.get(text);
		if (index == null || index < 0) {
			taskVectors.add(vector);
			index = taskVectors.indexOf(vector);
			taskTextToVectorIndexMap.put(text, index);
		}
		return index.intValue();
	}

	public int removeTaskVector(TaskVector vector) {
		String text = vector.getTaskInfo().toPlainText();
		Integer index = taskTextToVectorIndexMap.get(text);
		if (index != null || index >= 0) {
			taskVectors.remove(vector);
			taskTextToVectorIndexMap.remove(text);
		}
		return index.intValue();
	}

	public List<TaskVector> getTaskVectors() {
		return taskVectors;
	}

	public void setTaskVectors(List<TaskVector> taskVectors) {
		this.taskVectors = taskVectors;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public HashMap<String, Integer> getTaskTextToVectorIndexMap() {
		return taskTextToVectorIndexMap;
	}

	public void setTaskTextToVectorIndexMap(HashMap<String, Integer> taskTextToVectorIndexMap) {
		this.taskTextToVectorIndexMap = taskTextToVectorIndexMap;
	}

	public boolean isAnnotated() {
		return isAnnotated;
	}

	public void setAnnotated(boolean isAnnotated) {
		this.isAnnotated = isAnnotated;
	}

	public HashMap<String, Float> getMaxSimilarityCache() {
		if (maxSimilarityCache == null) // later added attr, might be null for existed objects
			maxSimilarityCache = new HashMap<>();
		return maxSimilarityCache;
	}

	public void setMaxSimilarityCache(HashMap<String, Float> maxSimilarityCache) {
		this.maxSimilarityCache = maxSimilarityCache;
	}
}
