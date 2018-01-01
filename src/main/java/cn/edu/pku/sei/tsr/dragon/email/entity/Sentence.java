package cn.edu.pku.sei.tsr.dragon.email.entity;

import java.io.Serializable;

public class Sentence implements Serializable{

	private static final long serialVersionUID = 3937107643827123569L;
	private String	sentence;

	public Sentence() {
		sentence = "";
	}

	public Sentence(String s) {
		this.sentence = s;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public String toString() {
		return sentence;
	}

}
