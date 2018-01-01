package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;

public class ThreadInfo implements Serializable {
	private static final long	serialVersionUID	= -5504339945194025946L;
	public static final String	TABLE_NAME			= "mythreads";

	private int					id;
	private String				title;

	private int					questionId;
	private int					acceptedAnswerId;
	private int[]				answersId;

	private int					viewCount			= 0;
	private int					favoriteCount		= 0;
	private int					vote				= 0;

	private int					libraryTagId;

	public ThreadInfo() {
	}

	public int findPost(int postId) {
		if (questionId == postId)
			return PostInfo.QUESTION_TYPE;
		else if (acceptedAnswerId == postId)
			return PostInfo.ACCEPTED_ANSWER_TYPE;
		else {
			for (int i = 0; i < answersId.length; i++) {
				if (answersId[i] == postId)
					return PostInfo.ANSWER_TYPE;
			}
		}
		return PostInfo.UNKNOWN_TYPE;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	public int getAcceptedAnswerId() {
		return acceptedAnswerId;
	}

	public void setAcceptedAnswerId(int acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
	}

	public int[] getAnswersId() {
		return answersId;
	}

	public void setAnswersId(int[] answersId) {
		this.answersId = answersId;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public int getLibraryTagId() {
		return libraryTagId;
	}

	public void setLibraryTagId(int libraryTagId) {
		this.libraryTagId = libraryTagId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

}
