package cn.edu.pku.sei.tsr.dragon.content.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaBaseInfo;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public class CodeLikeTermInfo implements UUIDInterface, Serializable {
	private static final long					serialVersionUID	= 2243814273130161693L;

	private String								uuid;
	private String								content;
	private String								relevantCodeElementFQN;
	private String								relevantCodeElementUUID;
	private transient JavaBaseInfo				relevantCodeElement	= null;
	private transient SentenceInfo				parent;
	private CodeLikeTermInfo						contextfather;
	private boolean								isLeaf				= false;
	private boolean								isType				= false;
	private boolean								isMethod			= false;
	private boolean								isParameterMethod	= false;
	private int									parameterNum		= 0;
	private List<CodeLikeTermInfo>					codeLikeTermList	= new LinkedList<CodeLikeTermInfo>();
	private transient Map<JavaBaseInfo, Double>	similarTypeCodeElementList;
	private transient Map<JavaBaseInfo, Double>	similarMethodCodeElementList;

	public CodeLikeTermInfo() {
		super();
	}

	public CodeLikeTermInfo(String content) {
		uuid = UUID.randomUUID().toString();
		this.content = content;
	}

	public String getRelevantCodeElementFQN() {
		return relevantCodeElementFQN;
	}
	public void setRelevantCodeElementFQN(String relevantCodeElementFQN) {
		this.relevantCodeElementFQN = relevantCodeElementFQN;
	}
	@Override
	public String getUuid() {
		return uuid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return content;
	}

	public List<CodeLikeTermInfo> getCodeLikeTermList() {
		return codeLikeTermList;
	}

	public void setCodeLikeTermList(List<CodeLikeTermInfo> codeLikeTermList) {
		this.codeLikeTermList = codeLikeTermList;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public boolean isType() {
		return isType;
	}

	public void setType(boolean isType) {
		this.isType = isType;
	}

	public boolean isMethod() {
		return isMethod;
	}

	public void setMethod(boolean isMethod) {
		this.isMethod = isMethod;
	}

	public boolean isParameterMethod() {
		return isParameterMethod;
	}

	public void setParameterMethod(boolean isParameterMethod) {
		this.isParameterMethod = isParameterMethod;
	}

	public Map<JavaBaseInfo, Double> getSimilarTypeCodeElementList() {
		return similarTypeCodeElementList;
	}

	public void setSimilarTypeCodeElementList(Map<JavaBaseInfo, Double> similarTypeCodeElementList) {
		this.similarTypeCodeElementList = similarTypeCodeElementList;
	}

	public Map<JavaBaseInfo, Double> getSimilarMethodCodeElementList() {
		return similarMethodCodeElementList;
	}

	public void setSimilarMethodCodeElementList(Map<JavaBaseInfo, Double> similarMethodCodeElementList) {
		this.similarMethodCodeElementList = similarMethodCodeElementList;
	}

	public int getParameterNum() {
		return parameterNum;
	}

	public void setParameterNum(int parameterNum) {
		this.parameterNum = parameterNum;
	}

	public SentenceInfo getParent() {
		return parent;
	}

	public void setParent(SentenceInfo parent) {
		this.parent = parent;
	}

	public CodeLikeTermInfo getContextfather() {
		return contextfather;
	}

	public void setContextfather(CodeLikeTermInfo contextfather) {
		this.contextfather = contextfather;
	}

	public JavaBaseInfo getRelevantCodeElement() {
		return relevantCodeElement;
	}

	public void setRelevantCodeElement(JavaBaseInfo relevantCodeElement) {
		this.relevantCodeElement = relevantCodeElement;
		if (relevantCodeElement != null) {
			this.relevantCodeElementFQN = relevantCodeElement.toString();
			this.relevantCodeElementUUID = relevantCodeElement.getUuid();
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
		for (CodeLikeTermInfo term : this.getCodeLikeTermList()) {
			term.setParent(this.parent);
		}
	}

}
