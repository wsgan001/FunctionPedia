package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.Date;

public class Question implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5358332591479961490L;
	private int		id;
	private int		postTypeId;
	private int		acceptedAnswerId	= -1;
	private Date	creationDate;
	private int		score;
	private int		viewCount;
	private String	body;
	private String	title;
	private int		ownerUserId;
	private int		lastEditorUserId;
	private Date	lastEditDate;
	private Date	lastActivityDate;
	private String	tags;
	private int		answerCount;
	private int		commentCount;
	private int		favoriteCount;
	
	public Question() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPostTypeId() {
		return postTypeId;
	}
	public void setPostTypeId(int postTypeId) {
		this.postTypeId = postTypeId;
	}
	public int getAcceptedAnswerId() {
		return acceptedAnswerId;
	}
	public void setAcceptedAnswerId(int acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
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
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getOwnerUserId() {
		return ownerUserId;
	}
	public void setOwnerUserId(int ownerUserId) {
		this.ownerUserId = ownerUserId;
	}
	public int getLastEditorUserId() {
		return lastEditorUserId;
	}
	public void setLastEditorUserId(int lastEditorUserId) {
		this.lastEditorUserId = lastEditorUserId;
	}
	public Date getLastEditDate() {
		return lastEditDate;
	}
	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}
	public Date getLastActivityDate() {
		return lastActivityDate;
	}
	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public int getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

}
