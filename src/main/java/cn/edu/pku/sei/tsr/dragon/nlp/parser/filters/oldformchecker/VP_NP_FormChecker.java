package cn.edu.pku.sei.tsr.dragon.nlp.parser.filters.oldformchecker;

import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class VP_NP_FormChecker {

	public static boolean check(PhraseInfo phrase) {
		if (phrase == null || phrase.getStemmedTree() == null)
			return false;

		// String filterPattern =
		// "__ < ( NP </NN.*/ | !< /NN.*/ < (NP < /NN.*/) ) !< PP";
		String filterPattern = "__ < " + Rules.NP_NN_PATTERN + " !< PP";

		TregexPattern tregexPattern = TregexPattern.compile(filterPattern.toString());
		Tree phraseTree = phrase.getStemmedTree();
		TregexMatcher matcher = tregexPattern.matcher(phraseTree);

		// 可以匹配到代词形式
		if (matcher.matches()) {
			Proof proof = new Proof();
			proof.setType(ProofType.PASS_VP_NP_FORM);
			phrase.getProofs().add(proof);
			return true;
		}

		return false;
	}
}
