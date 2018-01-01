package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;
import java.util.Date;

public class VoteInfo implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5847373647451719773L;
	public static final int		UP_VOTE				= 1;
	public static final int		DOWN_VOTE			= 2;
	public static final int		DEFAULT				= 0;

	private int	id;
	private int		voteType;
	private Date	creationDate;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getVoteType() {
		return voteType;
	}
	public void setVoteType(int voteType) {
		this.voteType = voteType;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
