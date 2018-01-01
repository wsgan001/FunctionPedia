package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;

public class Instance implements Serializable {
	private static final long	serialVersionUID	= -8085954245080382216L;

	private String				subjectName;
	private int					threadId;
	private int					taskVectorIndexInDocument;
//	private String				data;
	private TaskVector			taskVector;
//	private TaskShortVector		taskShortVector;

	public int getTaskVectorIndexInDocument() {
		return taskVectorIndexInDocument;
	}
	public void setTaskVectorIndexInDocument(int taskVectorIndexInDocument) {
		this.taskVectorIndexInDocument = taskVectorIndexInDocument;
	}
//	public String getData() {
//		return data;
//	}
//	public void setData(String data) {
//		this.data = data;
//	}
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public TaskVector getTaskVector() {
		return taskVector;
	}
	public void setTaskVector(TaskVector taskVector) {
		this.taskVector = taskVector;
	}
//	public TaskShortVector getTaskShortVector() {
//		return taskShortVector;
//	}
//	public void setTaskShortVector(TaskShortVector taskShortVector) {
//		this.taskShortVector = taskShortVector;
//	}
}
