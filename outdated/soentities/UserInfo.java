package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;
import java.util.Date;

public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 70208325752480435L;
	private int				id;
	private int					reputation;
	private String				displayName;
	private int					views;
	private int					upVotes;
	private int					downVotes;
	private String				websiteUrl;
	private String				location;
	private String				abouMe;
	private Date				creationDate;
	private Date				lastAccessDate;
	private int					age;

	public UserInfo() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getReputation() {
		return reputation;
	}
	public void setReputation(int reputation) {
		this.reputation = reputation;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public int getUpVotes() {
		return upVotes;
	}
	public void setUpVotes(int upVotes) {
		this.upVotes = upVotes;
	}
	public int getDownVotes() {
		return downVotes;
	}
	public void setDownVotes(int downVotes) {
		this.downVotes = downVotes;
	}
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAbouMe() {
		return abouMe;
	}
	public void setAbouMe(String abouMe) {
		this.abouMe = abouMe;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}

}
