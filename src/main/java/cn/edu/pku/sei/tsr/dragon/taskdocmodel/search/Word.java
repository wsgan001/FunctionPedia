package cn.edu.pku.sei.tsr.dragon.taskdocmodel.search;

public class Word{
	String word;
	String tag;
	double weight;
	public Word(String word, String tag){
		this.word = word;
		this.tag = tag;
	}
	public boolean equals(Word w){
		return word.equals(w.word) && tag.equals(w.tag);
	}
}