package cn.edu.pku.sei.tsr.dragon.feature;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.graphofnodeinfo.GraphParser;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.NounPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PrepPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.AdjectiveInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.ConjunctionInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.NounInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.VerbInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.WordInfo;
import cn.edu.pku.sei.tsr.dragon.test.GraphTest;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class PhraseStructureAnalyzer {

	public static boolean valiadateStructure(VerbalPhraseStructureInfo vps) {
		if (vps == null)
			return false;
		if (vps.getVerb() == null)
			return false; // verb必须在
		if (vps.getSubNP() == null && vps.getSubPPList().size() == 0)
			return false; // NP和PP不能同时不存在

		// 但凡存在的NP或者PP就必须有效
		if (!valiadateStructure(vps.getSubNP()))
			return false;
		for (PrepPhraseStructureInfo subPP : vps.getSubPPList()) {
			if (!valiadateStructure(subPP))
				return false;
		}
		return true;
	}

	public static boolean valiadateStructure(NounPhraseStructureInfo nps) {
		if (nps == null)
			return false;

		List<WordInfo> wordChain = nps.getWordChain();
		if (wordChain == null || wordChain.size() < 1)
			return false;

		// BUG：NP1 and/or NP2的形式？
		// state in wordChain: [DT.3.DT].2.[ADJ.2.ADJ].1.[NN.1.NN].0.
		// state0：只接受名词
		// state1：接受名词，状态不变；接受形容词或者其他词，状态跳转
		// state2：接受形容词，状态不变；接受冠词，状态跳转；不能接受名词
		// state3：只能接受冠词
		// state 1-2均可以接受conj
		int state = 0;
		for (int i = wordChain.size() - 1; i >= 0; i--) {
			WordInfo word = wordChain.get(i);
			if (state == 0) {
				if (word instanceof NounInfo)
					state = 1;
				else
					return false;
			} else if (state == 1) {
				if (word instanceof NounInfo || word instanceof ConjunctionInfo)
					state = 1;// 不变
				else if (word instanceof AdjectiveInfo)
					state = 2;
				else if (word instanceof WordInfo)
					state = 3;
				else
					return false;
			} else if (state == 2) {
				if (word instanceof ConjunctionInfo)
					state = 1;
				if (word instanceof AdjectiveInfo)
					state = 2;
				else if (word instanceof WordInfo)
					state = 3;
				else
					return false;
			} else if (state == 3) {
				if (word instanceof WordInfo)
					state = 3;
				else
					return false;
			}
		}

		if (nps.getSubPP() != null && !valiadateStructure(nps.getSubPP()))
			return false; // 如果有PP就要合格

		return true;
	}

	public static boolean valiadateStructure(PrepPhraseStructureInfo pps) {
		if (pps == null)
			return false;

		if (pps.getConjunction() == null)
			return false; // 必须有介词
		if (pps.getSubNP() == null || !valiadateStructure(pps.getSubNP()))
			return false; // 必须有NP且合法
		if (pps.getSubPP() != null && !valiadateStructure(pps.getSubPP()))
			return false; // 如果有PP，必须合法

		return true;
	}

	// 判断一个vps是否是另一个vps的子集, 已确保verb相同, vps == other时返回false
	private static boolean subVPSInAList(VerbalPhraseStructureInfo vps,
	                                     VerbalPhraseStructureInfo other) {
		if (vps == other)
			return false;
		if (other.getParticle() != null) {
			if (vps.getParticle() == null)
				return false;
			if (!other.getParticle().equals(vps.getParticle()))
				return false;
		}
		if (!other.getSubNP().getWordChain().containsAll(vps.getSubNP().getWordChain()))
			return false;
		if (!other.getSubPPListAsWordList().containsAll(vps.getSubPPListAsWordList()))
			return false;
		return true;
	}

	public static void removeSmallVPS(List<Pair<VerbalPhraseStructureInfo, Integer>> graphList) {
		Map<Pair<VerbInfo, Integer>, List<VerbalPhraseStructureInfo>> map = new HashMap<>();
		List<Pair<VerbalPhraseStructureInfo, Integer>> deleteList = new ArrayList<>();
		graphList.forEach(x -> {
			VerbalPhraseStructureInfo vps = x.getLeft();
			int freq = x.getRight();
			Pair<VerbInfo, Integer> key = new ImmutablePair<>(vps.getVerb(), freq);
			List<VerbalPhraseStructureInfo> value;
			if (!map.containsKey(key)) {
				value = new ArrayList<>();
				map.put(key, value);
			} else {
				value = map.get(key);
			}
			value.add(x.getLeft());
		});
		graphList.forEach(x -> {
			// if (x.getLeft().toNLText().equals("achieve accuracy on data training")) {
			// int a = 1;
			// }
			List<VerbalPhraseStructureInfo> vpsList = map
				.get(new ImmutablePair<>(x.getLeft().getVerb(), x.getRight()));
			if (vpsList.stream().anyMatch(y -> subVPSInAList(x.getLeft(), y)))
				deleteList.add(x);
		});
		graphList.removeAll(deleteList);
	}

	public static void splitLayers(List<Pair<VerbalPhraseStructureInfo, Integer>> graphList, String libraryName) {
		Map<NounInfo, NounLayer> coreNounLayerMap = new HashMap<>();
		graphList.forEach(x -> {
			x.getLeft().getSubNP().getWordChain().stream().filter(y -> y instanceof NounInfo).forEach(y -> {
				NounInfo noun = (NounInfo) y;
				NounLayer layer = coreNounLayerMap.get(noun);
				if (layer == null) {
					layer = new NounLayer(noun);
					coreNounLayerMap.put(noun, layer);
				}
				layer.addVPS(x.getLeft(), x.getRight());
			});
		});

		List<SecondLayer> layers = new ArrayList<>();

		coreNounLayerMap.forEach((k, v) -> layers.addAll(v.getSecondLayouts()));
		layers.sort((x, y) -> x.getCount() - y.getCount());
		int threshold = layers.stream().collect(Collectors.summingInt(x -> x.getCount())) * 9 / 10;
		int count = 0;
//		for (SecondLayer layer : layers) {
//			if (count > threshold) layer.filter();
//			count += layer.getCount();
//		}

		try {
			PrintStream ps = new PrintStream(ObjectIO.getDataObjDirectory(
				ObjectIO.NEW_HIERARCHICAL_FEATURES + File.separator + libraryName + ObjectIO.DAT_FILE_EXTENSION));

			List<Map.Entry<NounInfo, NounLayer>> entryList = new ArrayList<>(coreNounLayerMap.entrySet());

			Collections.sort(entryList, (x, y) -> {
				return y.getValue().getCount() - x.getValue().getCount();
			});
			threshold = entryList.stream().collect(Collectors.summingInt(x -> x.getValue().getCount())) * 9 / 10;
			count = 0;
			for (Map.Entry<NounInfo, NounLayer> entry : entryList) {
				if (count > threshold) break;
				count += entry.getValue().getCount();
				entry.getValue().print(ps);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GraphInfo g1 = GraphTest.graph();
		System.out.println(g1);
		VerbalPhraseStructureInfo vp = GraphParser
			.parseTreeToVPStructure(GraphParser.parseGraphToTree(g1));
		System.out.println(vp);
		System.out.println(valiadateStructure(vp));
	}

}
