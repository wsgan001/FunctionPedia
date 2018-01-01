package cn.edu.pku.sei.tsr.dragon.nlp.entity;

import java.util.List;

public class NounKGramInfo {
	private List<String> nouns;

	public NounKGramInfo(List<String> nouns) {
		this.nouns = nouns;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getClass() != obj.getClass()) return false;
		NounKGramInfo other = (NounKGramInfo) obj;
		if (nouns.size() != other.nouns.size()) return false;
		for (int i = 0; i < nouns.size(); ++i) {
			if (!nouns.get(i).equals(other.nouns.get(i))) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int code = 0;
		for (String noun : nouns) code += noun.hashCode();
		return code;
	}

	@Override
	public String toString() {
		return nouns.toString();
	}
}
