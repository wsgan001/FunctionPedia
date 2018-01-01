package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;

public class SOTag implements Serializable {
	private static final long	serialVersionUID	= -5887313623408658805L;
	public static final String	TABLE_NAME			= "sotags";

	private int					id;
	private String				tagName;
	private int					count;
	private int					excerptPostId;
	private int					wikiPostId;

	public SOTag() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getExcerptPostId() {
		return excerptPostId;
	}
	public void setExcerptPostId(int excerptPostId) {
		this.excerptPostId = excerptPostId;
	}
	public int getWikiPostId() {
		return wikiPostId;
	}
	public void setWikiPostId(int wikiPostId) {
		this.wikiPostId = wikiPostId;
	}

}
