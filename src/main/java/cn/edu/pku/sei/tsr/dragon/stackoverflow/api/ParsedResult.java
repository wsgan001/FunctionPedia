package cn.edu.pku.sei.tsr.dragon.stackoverflow.api;

import java.util.List;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;

public class ParsedResult {
	public int questionID;
	public ContentInfo questionContent;
	public ContentInfo answerContent;
	public List<String> codesInAnswer;

	public ParsedResult(int questionID, ContentInfo questionContent, ContentInfo answerContent, List<String> codesInAnswer) {
		this.questionID = questionID;
		this.questionContent = questionContent;
		this.answerContent = answerContent;
		this.codesInAnswer = codesInAnswer;
	}
}
