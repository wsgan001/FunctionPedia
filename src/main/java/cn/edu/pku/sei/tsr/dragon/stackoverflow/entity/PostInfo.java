package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;

/**
 * 
 * Combine all types of posts (questions and answers) as a unified type.
 * Separate info fields that should be parts of thread instead, i.e. title,
 * thread-q/a relationships, view count, fav count. Only reserve content and
 * user infos.
 * 
 * @author ZHUZixiao
 *
 */
public class PostInfo implements Serializable {
	private static final long	serialVersionUID		= -3117443449746459274L;
	public static final String	TABLE_NAME				= "myposts";

	public static final int		UNKNOWN_TYPE			= -1;
	public static final int		DEFAULT_TYPE			= 0;
	public static final int		QUESTION_TYPE			= 1;
	public static final int		ANSWER_TYPE				= 2;
	public static final int		ACCEPTED_ANSWER_TYPE	= 3;

	private int					id;
	private int					postType;

	private int					threadId;										// parent
	private int[]				commentsId;

	private String				body;
	private int					score;

	// Both may be empty, unregistered owner user or no last edit
	private int					ownerUserId;
	private int					lastEditorUserId;

	public PostInfo() {
		super();
	}

	public boolean isQuestion() {
		return postType == QUESTION_TYPE;
	}

	public boolean isAnswer() {
		return postType == ANSWER_TYPE;
	}

	public boolean isAcceptedAnswer() {
		return postType == ACCEPTED_ANSWER_TYPE;
	}

	@Override
	public String toString() {
		return body;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPostType() {
		return postType;
	}

	public void setPostType(int postTypeId) {
		this.postType = postTypeId;
	}

	public int[] getCommentsId() {
		return commentsId;
	}

	public void setCommentsId(int[] commentsId) {
		this.commentsId = commentsId;
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
}
