package cn.edu.pku.sei.tsr.dragon.stackoverflow.api;

public class SearchResult {
	public String title;
	public String link;
	public int questionID;

	public SearchResult(String title, String link, int questionID) {
		this.title = title;
		this.link = link;
		this.questionID = questionID;
	}
}
