package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public class CommentInfo implements UUIDInterface, Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8521796860666484829L;
	private String				uuid;
	private int				id;
	private ContentInfo			content;
	private UserInfo			ownerUser;
	private Date				creationDate;
	private int					score;
	private List<VoteInfo>		votes;
	private String				userDisplayName;

	private PostInfo parent;

	public CommentInfo() {
		super();
		uuid = java.util.UUID.randomUUID().toString();
		votes = new ArrayList<>();
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	@Override
	public String getUuid() {
		if (uuid == null || "".equals(uuid))
			uuid = java.util.UUID.randomUUID().toString();

		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public List<VoteInfo> getVotes() {
		return votes;
	}

	public void setVotes(List<VoteInfo> votes) {
		this.votes = votes;
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

	public PostInfo getParent() {
		return parent;
	}

	public void setParent(PostInfo parent) {
		this.parent = parent;
	}
}
