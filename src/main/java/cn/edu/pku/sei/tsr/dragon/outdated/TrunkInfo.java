package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;

import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;

public class TrunkInfo implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8873902791141746898L;

	private PhraseInfo			kernal;
	private PhraseInfo			trunk;
	private PhraseInfo			skeleton;

	public PhraseInfo getKernal() {
		return kernal;
	}
	public void setKernal(PhraseInfo kernal) {
		this.kernal = kernal;
	}
	public PhraseInfo getTrunk() {
		return trunk;
	}
	public void setTrunk(PhraseInfo trunk) {
		this.trunk = trunk;
	}
	public PhraseInfo getSkeleton() {
		return skeleton;
	}
	public void setSkeleton(PhraseInfo skeleton) {
		this.skeleton = skeleton;
	}
}
