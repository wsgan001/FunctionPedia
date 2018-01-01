package cn.edu.pku.sei.tsr.dragon.feature;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.ConjunctionInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.NounInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.dictionary.VerbInfo;

public class NounLayer {
	int count = 0;
	boolean filtered = false;
	NounInfo noun;
	Map<TriPair<NounInfo, ConjunctionInfo, VerbInfo>, SecondLayer> map = new HashMap<>();

	public NounLayer(NounInfo noun) {
		this.noun = noun;
	}

	public void addVPS(VerbalPhraseStructureInfo vps, int count) {
		this.count += count;
		TriPair<NounInfo, ConjunctionInfo, VerbInfo> pair = new TriPair<>(
			vps.getSubNP().getKeyNoun(),
			vps.getParticle(),
			vps.getVerb()
		);
		SecondLayer layer = map.get(pair);
		if (layer == null) {
			layer = new SecondLayer(pair);
			map.put(pair, layer);
		}
		layer.addVPS(vps, count);
	}
	
	public int getCount() {
		return count;
	}

	public Collection<SecondLayer> getSecondLayouts(){
		return map.values();
	}

	public void print(PrintStream ps) {
		ps.println(count + "\t" + noun);
		List<Map.Entry<TriPair<NounInfo, ConjunctionInfo, VerbInfo>, SecondLayer>> entryList = new ArrayList<>(map.entrySet());
		Collections.sort(entryList, (x, y) -> {return y.getValue().getCount() - x.getValue().getCount();});
//		int size = entryList.size();
//		entryList.stream().limit((size + 1) / 2).forEach(x -> {
//			x.getValue().print(ps);
//		});
		entryList.forEach(x -> {
			x.getValue().print(ps);
		});
	}
}
