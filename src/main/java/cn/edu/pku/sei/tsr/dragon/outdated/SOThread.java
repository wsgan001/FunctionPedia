package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SOThread implements Serializable {
	private static final long	serialVersionUID	= 8337281814521165499L;
	public static final String	TABLE_NAME			= "thread";

	private int					id;
	private int					questionId;
	private int					acceptedAnswerId;
	private List<Integer>		answersIdList;
	private String				answersId;

	public SOThread() {
		answersIdList = new ArrayList<>();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public List<Integer> getAnswersIdList() {
		return answersIdList;
	}
	public void setAnswersIdList(List<Integer> answersIdList) {
		this.answersIdList = answersIdList;
	}
	public String getAnswersId() {
		return answersId;
	}
	public void setAnswersId(String answersId) {
		this.answersId = answersId;
	}
}
