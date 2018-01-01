package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.Date;

public class Answer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8212462043811097470L;
	
	private int	id;
	private int	postTypeId;
	private int	parentId;
	private Date	creationDate;
	private int		score;
	private String	body;
	private int	ownerUserId;
	private int	lastEditorUserId;
	private Date	lastEditDate;
	private Date	lastActivityDate;
	private int		commentCount;
	private int		favoriteCount;
	
	public Answer() {
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
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
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
}
