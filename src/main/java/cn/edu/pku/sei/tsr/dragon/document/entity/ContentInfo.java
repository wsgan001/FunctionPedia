package cn.edu.pku.sei.tsr.dragon.document.entity;

import java.io.Serializable;

public class ContentInfo implements Serializable {
	private static final long	serialVersionUID			= -5844839732601712371L;
	public static final String	TABLE_NAME					= "contents";
	public static final int		SOURCE_TYPE_INVALID			= -1;
	public static final int		SOURCE_TYPE_THREAD_TITLE	= 1;
	public static final int		SOURCE_TYPE_POST_BODY		= 2;
	public static final int		SOURCE_TYPE_COMMENT_TEXT	= 3;

	private int					id;
	private String				text;

	/** Source of the contentInfo, id indicates the table record **/
	private int					sourceType					= SOURCE_TYPE_INVALID;
	private int					sourceId					= -1;

	/** A contentInfo consists of several consecutive paragraphs **/
	private int[]				paragraphsId;

	// private int firstParagraphId;
	// private int lastParagraphId;
	// private int paragraphsCount;

	public ContentInfo() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int[] getParagraphsId() {
		return paragraphsId;
	}

	public void setParagraphsId(int[] paragraphsId) {
		this.paragraphsId = paragraphsId;
	}

	// public int getFirstParagraphId() {
	// return firstParagraphId;
	// }
	//
	// public void setFirstParagraphId(int firstParagraphId) {
	// this.firstParagraphId = firstParagraphId;
	// }
	//
	// public int getLastParagraphId() {
	// return lastParagraphId;
	// }
	//
	// public void setLastParagraphId(int lastParagraphId) {
	// this.lastParagraphId = lastParagraphId;
	// }
	//
	// public int getParagraphsCount() {
	// return paragraphsCount;
	// }
	//
	// public void setParagraphCount(int paragraphsCount) {
	// this.paragraphsCount = paragraphsCount;
	// }

}
