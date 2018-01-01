package cn.edu.pku.sei.tsr.dragon.content.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public class ParagraphInfo implements UUIDInterface, Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7229861851634640475L;
	private String				uuid;
	private String				content;
	@Deprecated
	private transient ContentInfo			parent;
	private String				parentUuid;

	private List<SentenceInfo>	sentences;
	private transient List<CodeLikeTermInfo>	codeLikeTerms;	
	private boolean				isCodeFragment	= false;

	public ParagraphInfo(String content) {
		super();
		setUuid(UUID.randomUUID().toString());
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<SentenceInfo> getSentences() {
		return sentences;
	}

	public void setSentences(List<SentenceInfo> sentences) {
		this.sentences = sentences;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public ContentInfo getParent() {
		return parent;
	}

	public void setParent(ContentInfo parent) {
		this.parent = parent;
	}

	public boolean isCodeFragment() {
		return isCodeFragment;
	}

	public void setCodeFragment(boolean isCodeFragment) {
		this.isCodeFragment = isCodeFragment;
	}

	@Override
	public String toString() {
		return content;
	}

	public String getParentUuid() {
		return parentUuid;
	}

	public void setParentUuid(String parentUuid) {
		this.parentUuid = parentUuid;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		for (SentenceInfo sentence : this.getSentences()){
			sentence.setParent(this);
		}
	}

	public List<CodeLikeTermInfo> getCodeLikeTerms() {
		return codeLikeTerms;
	}

	public void setCodeLikeTerms(List<CodeLikeTermInfo> codeLikeTerms) {
		this.codeLikeTerms = codeLikeTerms;
	}	
}
