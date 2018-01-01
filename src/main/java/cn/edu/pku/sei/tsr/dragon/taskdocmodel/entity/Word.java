package cn.edu.pku.sei.tsr.dragon.taskdocmodel.entity;

import java.io.Serializable;

public class Word implements Serializable {
	private static final long	serialVersionUID	= 4947647985915562828L;

	public String				word;
	public String				tag;
	public float				weight;

	public Word(String word, String tag) {
		this.word = word;
		this.tag = tag;
	}

	public boolean equals(Word w) {
		return word.equals(w.word) && tag.equals(w.tag);
	}

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
}