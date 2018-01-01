package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOPost implements Serializable {
	private static final long	serialVersionUID	= -997911788109225116L;
	public static final String	TABLE_NAME			= "soposts";

	public static final int		POST_TYPE_QUESTION	= 1;
	public static final int		POST_TYPE_ANSWER	= 2;

	private int					id;
	private int					postTypeId;
	private int					acceptedAnswerId;
	private int					parentId;
	private Date				creationDate;
	private Date				deletionDate;
	private int					score;
	private int					viewCount;
	private String				body;
	private int					ownerUserId;
	private String				ownerDisplayName;
	private int					lastEditorUserId;
	private String				lastEditorDisplayName;
	private Date				lastEditDate;
	private Date				lastActivityDate;
	private String				title;
	private String				tags;
	private int					answerCount;
	private int					commentCount;
	private int					favoriteCount;
	private Date				closedDate;
	private Date				communityOwnedDate;

	public SOPost() {
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
	public int getAcceptedAnswerId() {
		return acceptedAnswerId;
	}
	public void setAcceptedAnswerId(int acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
	}
	public Date getDeletionDate() {
		return deletionDate;
	}
	public void setDeletionDate(Date deletionDate) {
		this.deletionDate = deletionDate;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}
	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}
	public String getLastEditorDisplayName() {
		return lastEditorDisplayName;
	}
	public void setLastEditorDisplayName(String lastEditorDisplayName) {
		this.lastEditorDisplayName = lastEditorDisplayName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public Date getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}
	public Date getCommunityOwnedDate() {
		return communityOwnedDate;
	}
	public void setCommunityOwnedDate(Date communityOwnedDate) {
		this.communityOwnedDate = communityOwnedDate;
	}
}
