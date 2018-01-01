package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOPostHistory implements Serializable {
	private static final long	serialVersionUID	= 7916981733116863576L;
	public static final String	TABLE_NAME			= "soposthistory";

	private int					id;
	private int					postHistoryTypeId;
	private int					postId;
	private String				revisionGUID;
	private Date				creationDate;
	private int					userId;
	private String				userDisplayName;
	private String				comment;
	private String				text;

	public SOPostHistory() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPostHistoryTypeId() {
		return postHistoryTypeId;
	}
	public void setPostHistoryTypeId(int postHistoryTypeId) {
		this.postHistoryTypeId = postHistoryTypeId;
	}
	public int getPostId() {
		return postId;
	}
	public void setPostId(int postId) {
		this.postId = postId;
	}
	public String getRevisionGUID() {
		return revisionGUID;
	}
	public void setRevisionGUID(String revisionGUID) {
		this.revisionGUID = revisionGUID;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

}
