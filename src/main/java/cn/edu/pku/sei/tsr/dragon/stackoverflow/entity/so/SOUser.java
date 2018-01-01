package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOUser implements Serializable {
	private static final long	serialVersionUID	= 1758031820619894607L;
	public static final String	TABLE_NAME			= "sousers";

	private int					id;
	private int					reputation;
	private String				displayName;
	private Date				creationDate;
	private Date				lastAccessDate;
	private String				websiteUrl;
	private String				location;
	private String				abouMe;
	private int					views;
	private int					upVotes;
	private int					downVotes;
	private String				profileImageUrl;
	private String				emailHash;
	private int					age;
	private int					accountId;

	public SOUser() {
		// TODO Auto-generated constructor stub
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
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	public String getEmailHash() {
		return emailHash;
	}
	public void setEmailHash(String emailHash) {
		this.emailHash = emailHash;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

}
