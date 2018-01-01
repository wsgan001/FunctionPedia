package cn.edu.pku.sei.tsr.dragon.content.entity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;

public class VerbalPhraseInfo extends PhraseInfo {

	private static final long serialVersionUID = -8143900492313845641L;

	private VerbalPhraseStructureInfo structure;

	public VerbalPhraseStructureInfo getStructure() {
		return structure;
	}

	public void setStructure(VerbalPhraseStructureInfo structure) {
		this.structure = structure;
	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws Exception {
		ois.defaultReadObject();
	}
}
