package cn.edu.pku.sei.tsr.dragon.nlp.parser.filters;

import org.apache.commons.lang3.ArrayUtils;

import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;

public class FilterDeterminer {
	public static boolean isValuable(String word) {
		return ArrayUtils.contains(Rules.valuable_determiners, word);
	}

}
