package cn.edu.pku.sei.tsr.dragon.document.entity;

import java.io.Serializable;

public class SentenceInfo implements Serializable {
	private static final long	serialVersionUID	= 1896002261136446138L;
	public static final String	TABLE_NAME			= "sentences";

	private int					id;
	private String				text;

	/**
	 * The parent ({@code}ParagrahInfo) of the sentence. Data type of the source
	 * paragraph must be a text paragraph
	 */
	private int					parentId			= -1;

	/**
	 * The index of this sentence in its parent's children array
	 */
	private int					indexAsChild		= -1;

	/**
	 * A sentenceInfo consists of several phrases.
	 */
	private int[]				phrasesId;

	private String				treeString;
	private String				codeTermString;

	public SentenceInfo() {
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

	public int getIndexAsChild() {
		return indexAsChild;
	}

	public void setIndexAsChild(int indexAsChild) {
		this.indexAsChild = indexAsChild;
	}

	public int[] getPhrasesId() {
		return phrasesId;
	}

	public void setPhrasesId(int[] phrasesId) {
		this.phrasesId = phrasesId;
	}

	public String getTreeString() {
		return treeString;
	}

	public void setTreeString(String treeString) {
		this.treeString = treeString;
	}

	public String getCodeTermString() {
		return codeTermString;
	}

	public void setCodeTermString(String codeTermString) {
		this.codeTermString = codeTermString;
	}
}
