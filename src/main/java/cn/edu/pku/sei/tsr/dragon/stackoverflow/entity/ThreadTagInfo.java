package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;

public class ThreadTagInfo implements Serializable {
	private static final long	serialVersionUID	= 4938923470361379757L;
	public static final String	TABLE_NAME			= "mythreadtags";

	private int					threadId;
	private int					tagId;

	public ThreadTagInfo() {
	}

	public ThreadTagInfo(int _threadId, int _tagId) {
		threadId = _threadId;
		tagId = _tagId;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

}
