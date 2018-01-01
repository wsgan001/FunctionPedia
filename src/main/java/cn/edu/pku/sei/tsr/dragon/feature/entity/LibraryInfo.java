package cn.edu.pku.sei.tsr.dragon.feature.entity;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;

public class LibraryInfo {
	private String				name;
	private List<ThreadInfo>	threadList;
	private List<ContentInfo>	contentList;
	private List<PhraseInfo>	phraseList;

	public LibraryInfo(String libraryName) {
		this.name = libraryName;
		threadList = new ArrayList<>();
		setContentList(new ArrayList<>());
		phraseList = new ArrayList<>();
	}

	public List<ThreadInfo> getThreadList() {
		return threadList;
	}

	public void setThreadList(List<ThreadInfo> threadList) {
		this.threadList = threadList;
	}

	public List<PhraseInfo> getPhraseList() {
		return phraseList;
	}

	public void setPhraseList(List<PhraseInfo> phraseList) {
		this.phraseList = phraseList;
	}

	public String getName() {
		return name;
	}

	public void setName(String libraryName) {
		this.name = libraryName;
	}

	public List<ContentInfo> getContentList() {
		return contentList;
	}

	public void setContentList(List<ContentInfo> contentList) {
		this.contentList = contentList;
	}

}
