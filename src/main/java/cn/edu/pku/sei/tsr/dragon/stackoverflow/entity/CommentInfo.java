package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;

public class CommentInfo implements Serializable {
	private static final long	serialVersionUID	= -8521796860666484829L;
	public static final String	TABLE_NAME			= "mycomments";

	private int					id;
	private int					postId;
	private int					score;
	private String				text;
	private int					userId;

	public CommentInfo() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
