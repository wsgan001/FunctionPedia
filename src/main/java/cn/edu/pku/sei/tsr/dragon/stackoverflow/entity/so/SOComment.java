package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOComment implements Serializable {
	private static final long	serialVersionUID	= -5451295273647315933L;
	public static final String	TABLE_NAME			= "socomments";

	private int					id;
	private int					postId;
	private Date				creationDate;
	private int					score;
	private String				text;
	private int					userId;
	private String				userDisplayName;

	public SOComment() {
		// TODO Auto-generated constructor stub
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
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
	public String getUserDisplayName() {
		return userDisplayName;
	}
	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}
}
