package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOVote implements Serializable {
	private static final long	serialVersionUID	= 3755022374264487704L;
	public static final String	TABLE_NAME			= "sovotes";

	public static final int		ACCEPTED_VOTE		= 1;
	public static final int		UP_VOTE				= 2;
	public static final int		DOWN_VOTE			= 3;
	// userid will be populated
	public static final int		FAVORITE			= 4;

	private int					id;
	private int					postId;
	private int					voteTypeId;
	private int					userId;
	private Date				creationDate;
	private int					bountyAmount;

	public SOVote() {
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
	public int getVoteTypeId() {
		return voteTypeId;
	}
	public void setVoteTypeId(int voteTypeId) {
		this.voteTypeId = voteTypeId;
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
	public int getBountyAmount() {
		return bountyAmount;
	}
	public void setBountyAmount(int bountyAmount) {
		this.bountyAmount = bountyAmount;
	}

}
