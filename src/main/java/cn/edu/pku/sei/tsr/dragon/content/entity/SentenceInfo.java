package cn.edu.pku.sei.tsr.dragon.content.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;
import edu.stanford.nlp.trees.Tree;

public class SentenceInfo implements UUIDInterface, Serializable {
	/**
	 * 
	 */
	private static final long		serialVersionUID	= -6695549730422579956L;
	private String					uuid;
	private String					content;
	private Tree					grammaticalTree;
	private List<CodeLikeTermInfo>	codeLikeTerms;
	private List<PhraseInfo>		phrases;
	public  boolean                 beforeCodeFragment;
	public  boolean                 afterCodeFragment;
	@Deprecated
	private transient ParagraphInfo			parent;
	private String					parentUuid;

	public SentenceInfo() {
		uuid = java.util.UUID.randomUUID().toString();
		phrases = new ArrayList<PhraseInfo>();
		setCodeLikeTerms(new ArrayList<>());
	}

	public SentenceInfo(String sentence) {
		this();
		setContent(sentence);
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String sentence) {
		this.content = sentence;
	}

	public List<PhraseInfo> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<PhraseInfo> phrases) {
		this.phrases = phrases;
	}

	public Tree getGrammaticalTree() {
		return grammaticalTree;
	}

	public void setGrammaticalTree(Tree grammaticalTree) {
		this.grammaticalTree = grammaticalTree;
	}

	@Override
	public String toString() {
		return content;
	}

	public ParagraphInfo getParent() {
		return parent;
	}

	public void setParent(ParagraphInfo parent) {
		this.parent = parent;
	}

	public List<CodeLikeTermInfo> getCodeLikeTerms() {
		return codeLikeTerms;
	}

	public void setCodeLikeTerms(List<CodeLikeTermInfo> codeLikeTerms) {
		this.codeLikeTerms = codeLikeTerms;
	}

	public String getParentUuid() {
		return parentUuid;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		for (PhraseInfo phrase : this.getPhrases()){
			phrase.setParent(this);
		}
		for (CodeLikeTermInfo term : this.getCodeLikeTerms()){
			term.setParent(this);
		}		
	}
}
