package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long	serialVersionUID	= 2518936519718024847L;

	private Document			document;
	private float				rankScore;

	public Result() {
		super();
	}

	public int getThreadId() {
		return document.getThreadId();
	}

	public float getRankScore() {
		return rankScore;
	}
	public void setRankScore(float rankScore) {
		this.rankScore = rankScore;
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}

}
