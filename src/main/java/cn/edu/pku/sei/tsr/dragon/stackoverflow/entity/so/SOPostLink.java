package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOPostLink implements Serializable {
	private static final long	serialVersionUID	= 210206501550652449L;
	public static final String	TABLE_NAME			= "sopostlinks";

	private int					id;
	private Date				creationDate;
	private int					postId;
	private int					relatedPostId;
	private int					linkTypeId;

	public SOPostLink() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getLinkTypeId() {
		return linkTypeId;
	}
	public void setLinkTypeId(int linkTypeId) {
		this.linkTypeId = linkTypeId;
	}
	public int getPostId() {
		return postId;
	}
	public void setPostId(int postId) {
		this.postId = postId;
	}
	public int getRelatedPostId() {
		return relatedPostId;
	}
	public void setRelatedPostId(int relatedPostId) {
		this.relatedPostId = relatedPostId;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
