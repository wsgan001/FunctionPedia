package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;

public class FullTextDocument implements Serializable {
	private static final long	serialVersionUID	= -6390633463327501679L;

	private int					threadId;
	private String				text;

	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

}
