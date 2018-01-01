package cn.edu.pku.sei.tsr.dragon.statistics;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.NounKGramInfo;

public class VerbNounStatistics {
	public static ImmutableSet<Multiset.Entry<Pair<String, String>>> verbNounCount(List<VerbNounInfo> verbNounInfos) {
		Multiset<Pair<String, String>> result = HashMultiset.create();
		verbNounInfos.forEach(x -> result.add(new ImmutablePair<>(x.getVerb(), x.getNoun())));
		return (ImmutableSet<Entry<Pair<String, String>>>) Multisets.copyHighestCountFirst(result).entrySet();
	}

	public static Map<String, Multiset<String>> verbCountInNoun(List<VerbNounInfo> verbNounInfos) {
		Map<String, Multiset<String>> result = new TreeMap<>();
		verbNounInfos.forEach(verbNounInfo -> {
			String verb = verbNounInfo.getVerb();
			String noun = verbNounInfo.getNoun();
			Multiset<String> nounSet = result.get(verb);
			if (nounSet == null) {
				nounSet = HashMultiset.create();
				nounSet.add(noun);
				result.put(verb, nounSet);
			}
			nounSet.add(noun);
		});
		return result;
	}

	public static Map<String, Multiset<String>> nounCountInVerb(List<VerbNounInfo> verbNounInfos) {
		Map<String, Multiset<String>> result = new TreeMap<>();
		verbNounInfos.forEach(verbNounInfo -> {
			String verb = verbNounInfo.getVerb();
			String noun = verbNounInfo.getNoun();
			Multiset<String> verbSet = result.get(noun);
			if (verbSet == null) {
				verbSet = HashMultiset.create();
				verbSet.add(verb);
				result.put(noun, verbSet);
			}
			verbSet.add(verb);
		});
		return result;
	}

	public static ImmutableSet<Multiset.Entry<NounKGramInfo>> kgramCount(List<NounKGramInfo> nounKGramInfos) {
		Multiset<NounKGramInfo> result = HashMultiset.create();
		nounKGramInfos.forEach(result::add);
		return (ImmutableSet<Entry<NounKGramInfo>>) Multisets.copyHighestCountFirst(result).entrySet();
	}
}
