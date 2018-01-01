package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public class PostInfo implements UUIDInterface, Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID		= -3117443449746459274L;
	public static final int		DEFAULT_TYPE			= 0;
	public static final int		QUESTION_TYPE			= 1;
	public static final int		ACCEPTED_ANSWER_TYPE	= 2;
	public static final int		ANSWER_TYPE				= 3;

	private String				uuid;
	private int					id;
	private UserInfo			ownerUser;
	private Date				creationDate;
	private int					score;
	private List<VoteInfo>		votes;
	private UserInfo			lastEditorUser;
	private Date				lastEditDate;

	private ContentInfo			content;
	private List<CommentInfo>	comments;
	private int					postType				= DEFAULT_TYPE;

	private ThreadInfo			parent;

	public PostInfo() {
		uuid = java.util.UUID.randomUUID().toString();
		votes = new ArrayList<>();
		comments = new ArrayList<>();
	}

	public boolean isQuestion() {
		return postType == QUESTION_TYPE;
	}

	public boolean isAcceptedAnswer() {
		return postType == ACCEPTED_ANSWER_TYPE;
	}

	public boolean isAnswer() {
		return postType == ANSWER_TYPE;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserInfo getOwnerUser() {
		return ownerUser;
	}

	public void setOwnerUser(UserInfo ownerUser) {
		this.ownerUser = ownerUser;
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

	public int getPostType() {
		return postType;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

	public List<VoteInfo> getVotes() {
		return votes;
	}

	public void setVotes(List<VoteInfo> votes) {
		this.votes = votes;
	}

	public UserInfo getLastEditorUser() {
		return lastEditorUser;
	}

	public void setLastEditorUser(UserInfo lastEditorUser) {
		this.lastEditorUser = lastEditorUser;
	}

	public Date getLastEditDate() {
		return lastEditDate;
	}

	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	public List<CommentInfo> getComments() {
		return comments;
	}

	public void setComments(List<CommentInfo> comments) {
		this.comments = comments;
	}

	public ContentInfo getContent() {
		return content;
	}

	public void setContent(ContentInfo content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return content.toString();
	}

	public ThreadInfo getParent() {
		return parent;
	}

	public void setParent(ThreadInfo parent) {
		this.parent = parent;
	}
}
