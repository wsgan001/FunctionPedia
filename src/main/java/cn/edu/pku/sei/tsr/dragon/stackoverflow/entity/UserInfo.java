package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;

public class UserInfo implements Serializable {
	private static final long	serialVersionUID		= 70208325752480435L;
	public static final String	TABLE_NAME				= "myusers";

	private int					id;
	private String				displayName;
	private int					reputation;
	private int					badgesCountFirstClass	= 0;
	private int					badgesCountSecondClass	= 0;
	private int					badgesCountThirdClass	= 0;
	private int					askedQuestionsCount		= 0;
	private int					answersCount			= 0;
	private int					acceptedAnswersCount	= 0;

	// his/her profile receives
	private int					views;
	// he/she contributes
	private int					upVotes;
	private int					downVotes;

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

	public int getBadgesCountFirstClass() {
		return badgesCountFirstClass;
	}
	public void setBadgesCountFirstClass(int badgesCountFirstClass) {
		this.badgesCountFirstClass = badgesCountFirstClass;
	}
	public int getBadgesCountSecondClass() {
		return badgesCountSecondClass;
	}
	public void setBadgesCountSecondClass(int badgesCountSecondClass) {
		this.badgesCountSecondClass = badgesCountSecondClass;
	}
	public int getBadgesCountThirdClass() {
		return badgesCountThirdClass;
	}
	public void setBadgesCountThirdClass(int badgesCountThirdClass) {
		this.badgesCountThirdClass = badgesCountThirdClass;
	}

	public int getAskedQuestionsCount() {
		return askedQuestionsCount;
	}

	public void setAskedQuestionsCount(int askedQuestionsCount) {
		this.askedQuestionsCount = askedQuestionsCount;
	}

	public int getAnswersCount() {
		return answersCount;
	}

	public void setAnswersCount(int answersCount) {
		this.answersCount = answersCount;
	}

	public int getAcceptedAnswersCount() {
		return acceptedAnswersCount;
	}

	public void setAcceptedAnswersCount(int acceptedAnswersCount) {
		this.acceptedAnswersCount = acceptedAnswersCount;
	}
}
