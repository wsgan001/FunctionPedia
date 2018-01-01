package cn.edu.pku.sei.tsr.dragon.nlp.parser.filters.oldformchecker;

import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class VP_DT_PP_FormChecker {
	public static boolean check(PhraseInfo phrase) {
		if (phrase == null || phrase.getStemmedTree() == null)
			return false;

		// "VP < ( NP !< /NN.*/ !< PRP  [ <: DT | [!<< /NN.*/ << ( NP <: DT )] ] ) < ( PP < ( NP < /NN.*/ ) )"

		String filterPattern = "__ < ( NP !</NN.*/ !<PRP  [ <: (DT <"
				+ Rules.ruleWordsConjuctionForTregex(Rules.DETERMINERS) + ") | !<</NN.*/ << ( NP <: (DT <"
				+ Rules.ruleWordsConjuctionForTregex(Rules.DETERMINERS) + " ))] ) < " + Rules.PP_PATTERN;
		// System.out.println(filterPattern);

		TregexPattern tregexPattern = TregexPattern.compile(filterPattern.toString());
		Tree phraseTree = phrase.getStemmedTree();
		TregexMatcher matcher = tregexPattern.matcher(phraseTree);

		// 可以匹配到冠词形式
		if (matcher.matches()) {
			Proof proof = new Proof();
			proof.setType(ProofType.PASS_VP_DT_PP_FORM);
			phrase.getProofs().add(proof);
			return true;
		}

		return false;
	}
}
