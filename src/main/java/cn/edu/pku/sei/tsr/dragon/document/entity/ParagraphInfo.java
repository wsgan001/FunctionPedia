package cn.edu.pku.sei.tsr.dragon.document.entity;

import java.io.Serializable;

/**
 * @author ZHUZixiao
 *
 */
public class ParagraphInfo implements Serializable {
	private static final long	serialVersionUID	= -7878118050893574574L;
	public static final String	TABLE_NAME			= "paragraphs";
	public static final int		PARAGRAPH_TYPE_TEXT	= 1;
	public static final int		PARAGRAPH_TYPE_CODE	= 2;

	private int					id;
	private int					paragraphType		= PARAGRAPH_TYPE_TEXT;	// text
																			// default
	private String				text;

	/**
	 * The parent ({@code}ContentInfo) of the paragraph
	 */
	private int					parentId			= -1;

	/**
	 * The index of this paragraph in its parent's children array
	 */
	private int					indexAsChild		= -1;

	/**
	 * A textual paragraphInfo consists of several consecutive sentences. Should
	 * be empty if type == PARA_TYPE_CODE
	 */
	private int[]				sentencesId;

	// /** The siblings ({@code}ParagraohInfo) of the paragraph **/
	// private int previousId;
	// private int nextId;
	//
	// /** A paragraphInfo consists of several consecutive sentences **/
	// private int firstSentenceId;
	// private int lastSentenceId;
	// private int sentencesCount;

	public ParagraphInfo() {
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	// public int getPreviousId() {
	// return previousId;
	// }
	//
	// public void setPreviousId(int previousId) {
	// this.previousId = previousId;
	// }
	//
	// public int getNextId() {
	// return nextId;
	// }
	//
	// public void setNextId(int nextId) {
	// this.nextId = nextId;
	// }
	//
	// public int getFirstSentenceId() {
	// return firstSentenceId;
	// }
	//
	// public void setFirstSentenceId(int firstSentenceId) {
	// this.firstSentenceId = firstSentenceId;
	// }
	//
	// public int getLastSentenceId() {
	// return lastSentenceId;
	// }
	//
	// public void setLastSentenceId(int lastSentenceId) {
	// this.lastSentenceId = lastSentenceId;
	// }
	//
	// public int getSentencesCount() {
	// return sentencesCount;
	// }
	//
	// public void setSentencesCount(int sentencesCount) {
	// this.sentencesCount = sentencesCount;
	// }

	public int getIndexAsChild() {
		return indexAsChild;
	}

	public void setIndexAsChild(int indexAsChild) {
		this.indexAsChild = indexAsChild;
	}

	public int[] getSentencesId() {
		return sentencesId;
	}

	public void setSentencesId(int[] sentencesId) {
		this.sentencesId = sentencesId;
	}

	public int getParagraphType() {
		return paragraphType;
	}

	public void setParagraphType(int paragraphType) {
		this.paragraphType = paragraphType;
	}
}
