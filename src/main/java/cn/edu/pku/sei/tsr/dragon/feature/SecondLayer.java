package cn.edu.pku.sei.tsr.dragon.feature;


import java.io.PrintStream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.ConjunctionInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.NounInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.VerbInfo;

public class SecondLayer {
	private int count = 0;
	private boolean filtered = false;
	private TriPair<NounInfo, ConjunctionInfo, VerbInfo> pair;
	private Multiset<VerbalPhraseStructureInfo> vpsSet = HashMultiset.create();

	public SecondLayer(TriPair<NounInfo, ConjunctionInfo, VerbInfo> pair) {
		this.pair = pair;
	}

	public void addVPS(VerbalPhraseStructureInfo vps, int count) {
		this.count += count;
		vpsSet.add(vps, count);
	}

	public int getCount() {
		return count;
	}

	public void filter() {
		filtered = true;
	}

	public void print(PrintStream ps) {
		if (filtered) return;
		ps.println("\t\t" + count + "\t" + pair);
		ImmutableSet<Multiset.Entry<VerbalPhraseStructureInfo>> set = (ImmutableSet<Entry<VerbalPhraseStructureInfo>>) Multisets.copyHighestCountFirst(vpsSet).entrySet();
//		int size = set.size();
//		set.stream().limit((size + 1) / 2).forEach(x -> {
//			ps.println("\t\t\t\t" + x.getCount() + "\t" + x.getElement().toNLText());
//		});
		set.stream().forEach(x -> {
			ps.println("\t\t\t\t" + x.getCount() + "\t" + x.getElement().toNLText() + "\t" + x.getElement().getUuid());
		});
	}
}
