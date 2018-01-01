package cn.edu.pku.sei.tsr.dragon.email.entity;

import java.io.Serializable;

public class SessionContent implements Serializable{

	private static final long serialVersionUID = 2024086366540003222L;

	private String	sessionID;

	private String	participants;

	private String	subject;

	private String	content;

	private String	msgList;

	private int		projectID;

	public String getMsgList() {
		return msgList;
	}

	public void setMsgList(String msgList) {
		this.msgList = msgList;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getParticipants() {
		return participants;
	}

	public void setParticipants(String participants) {
		this.participants = participants;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String toString() {
		String ret = "";
		ret += "------------------------------------\n";
		ret += "sessionID :" + this.sessionID + "\n";
		ret += "subject :" + this.subject + "\n";
		ret += "participants :" + this.participants + "\n";
		ret += "content length :" + this.content.length() + "\n";
		ret += "------------------------------------\n";
		return ret;
	}
}
