package cn.edu.pku.sei.tsr.dragon.feature;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.NounPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PrepPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.NounInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.WordInfo;

public class FeatureLinker {

	public static float calculateSimilarityToA(VerbalPhraseStructureInfo vpsa, VerbalPhraseStructureInfo vpsb) {
		if (vpsa == null || vpsb == null) return 0;
		if (!vpsa.getVerb().equals(vpsb.getVerb())) return 0;
		List<WordInfo> wordsA = getWords(vpsa);
		List<WordInfo> wordsB = getWords(vpsb);

		int count = 0;
		for (WordInfo a : wordsA) {
			if (wordsB.contains(a)) ++count;
		}
		return (float) count*count / (wordsA.size()*wordsB.size());
	}

	// 获取所有下属名词列表中的word
	public static List<NounInfo> getNouns(VerbalPhraseStructureInfo vps) {
		List<WordInfo> wordList = getWords(vps);
		return getNouns(wordList);
	}

	// 获取所有下属名词列表中的word
	public static List<NounInfo> getNouns(List<WordInfo> wordList) {
		List<NounInfo> nounList = new ArrayList<>();

		for (WordInfo word : wordList) {
			if (word instanceof NounInfo)
				nounList.add((NounInfo) word);
		}

		return nounList;
	}

	// 获取所有下属名词列表中的word
	public static List<WordInfo> getWords(VerbalPhraseStructureInfo vps) {
		List<WordInfo> wordList = new ArrayList<>();
		wordList.addAll(getWords(vps.getSubNP()));
		for (PrepPhraseStructureInfo pps : vps.getSubPPList()) {
			wordList.addAll(getWords(pps.getSubNP()));
		}
		return wordList;
	}

	// 获取所有下属名词列表中的word
	public static List<WordInfo> getWords(NounPhraseStructureInfo nps) {
		List<WordInfo> wordList = new ArrayList<>();
		if (nps != null) {
			if (nps.getWordChain() != null)
				wordList.addAll(nps.getWordChain());
			if (nps.getSubPP() != null)
				wordList.addAll(getWords(nps.getSubPP().getSubNP()));
		}
		return wordList;
	}

}
