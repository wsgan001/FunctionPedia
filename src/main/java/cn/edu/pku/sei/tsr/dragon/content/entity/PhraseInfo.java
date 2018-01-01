package cn.edu.pku.sei.tsr.dragon.content.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.outdated.TrunkInfo;
import cn.edu.pku.sei.tsr.dragon.utils.TreeUtils;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.Stemmer;
import edu.stanford.nlp.trees.Tree;

public class PhraseInfo implements UUIDInterface,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8940129084178474863L;

	private String	uuid;
	private String	content;
	//private Tree	stemmedTree;	// stemmed tree
	private Tree	tree;			// unstemmed tree

	private transient SentenceInfo	parent;
	private List<Proof>		proofs;
	@Deprecated
	private transient TrunkInfo		trunk;

	public static final String	LEVEL_0	= "level_0";
	public static final String	LEVEL_1	= "level_1";
	public static final String	LEVEL_2	= "level_2";
	public static final String	LEVEL_3	= "level_3";
	public static final String	LEVEL_4	= "level_4";
	public static final String	LEVEL_5	= "level_5";

	public PhraseInfo() {
		uuid = java.util.UUID.randomUUID().toString();
		proofs = new ArrayList<Proof>();
	}

	public PhraseInfo(Tree tree) {
		this();
		this.tree = tree;
		this.content = TreeUtils.interpretTreeToString(tree);
	}
	
	public Tree getStemmedTree() {
		Tree stemmedTree = this.tree.deepCopy();
		Stemmer.stemTree(stemmedTree);
		return stemmedTree;
	}

	public String getStemmedContent() {
		return TreeUtils.interpretTreeToString(getStemmedTree());
	}

	public boolean isVP() {
		return "VP".equals(tree.label().toString());
	}

	public boolean isNP() {
		return "NP".equals(tree.label().toString());
	}

	public boolean hasProof(ProofType type) {
		if (type == null)
			return false;
		for (Proof proof : proofs) {
			if (type.equals(proof.getType()))
				return true;
		}
		return false;
	}

	public String getProofString() {
		StringBuilder sb = new StringBuilder();
		int scores = 0;
		for (int i = 0; i < proofs.size(); i++) {
			Proof proof = proofs.get(i);
			sb.append(proof.toString());
			// sb.append("[ProofType] " + proof.getType().getName());
			if (i < proofs.size() - 1)
				sb.append(System.getProperty("line.separator"));
			scores += proof.getScore();
		}
		sb.insert(0, "[ProofScore] " + scores + System.getProperty("line.separator"));
		return sb.toString();
	}

	public int getProofTotalScore() {
		int scores = 0;
		for (Proof proof : proofs) {
			scores += proof.getScore();
		}
		return scores;
	}

	public void addProof(Proof proof) {
		if (proofs == null)
			proofs = new ArrayList<Proof>();
		proofs.add(proof);
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getContent() {
		if (content == null && tree != null)
			content = TreeUtils.interpretTreeToString(tree);
		return content;
	}

	public void setContent(String phrase) {
		this.content = phrase;
	}


	public List<Proof> getProofs() {
		return proofs;
	}

	public void setProofs(List<Proof> proofs) {
		this.proofs = proofs;
	}

	public TrunkInfo getTrunk() {
		return trunk;
	}

	public void setTrunk(TrunkInfo trunk) {
		this.trunk = trunk;
	}

	public Tree getTree() {
		return tree;
	}

	public void setTree(Tree originalTree) {
		this.tree = originalTree;
	}

	@Override
	public String toString() {
		return getContent();
	}

	public SentenceInfo getParent() {
		return parent;
	}

	public void setParent(SentenceInfo parent) {
		this.parent = parent;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
	}	
}
