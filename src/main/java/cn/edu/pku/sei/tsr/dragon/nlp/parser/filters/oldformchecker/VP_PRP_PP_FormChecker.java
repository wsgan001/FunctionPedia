package cn.edu.pku.sei.tsr.dragon.nlp.parser.filters.oldformchecker;

import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class VP_PRP_PP_FormChecker {


	public static boolean check(PhraseInfo phrase) {
		if (phrase == null || phrase.getStemmedTree() == null)
			return false;

		// __ < ( NP <: ( PRP < it|them ) ) < ( PP < ( NP < /NN.*/ ) )
		String filterPattern = "__ < ( NP <: ( PRP < " + Rules.ruleWordsConjuctionForTregex(Rules.VALID_PRONOUNS)
				+ " ) ) < " + Rules.PP_PATTERN;

		TregexPattern tregexPattern = TregexPattern.compile(filterPattern.toString());
		Tree phraseTree = phrase.getStemmedTree();
		TregexMatcher matcher = tregexPattern.matcher(phraseTree);

		// 可以匹配到代词形式
		if (matcher.matches()) {
			Proof proof = new Proof();
			proof.setType(ProofType.PASS_VP_PRP_PP_FORM);
			phrase.getProofs().add(proof);
			return true;
		}

		return false;
	}
}
