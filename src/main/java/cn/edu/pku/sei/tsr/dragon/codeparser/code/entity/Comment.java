package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;

import java.io.Serializable;

public class Comment implements Serializable {
	private static final long	serialVersionUID	= -8024668410920547749L;
	private String				comment;

	public Comment(String comment) {
		this.comment = comment;
	}

	@Override
	public String toString() {
		return comment;
	}

}
