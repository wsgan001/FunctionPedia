package cn.edu.pku.sei.tsr.dragon.nlp.parser.filters;

import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.ProofType;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;

public class FilterPhrase {
	public static boolean filter(PhraseInfo phrase) {
		String text = phrase.getText();
		for (String stop_phrase : Rules.stop_phrases) {
			if (text.contains(stop_phrase)) {
				Proof proof = new Proof(ProofType.FAIL_STOP_PHRASES);
				proof.setEvidence(stop_phrase);
				phrase.addProof(proof);
				return false;
			}
		}
		for (String stop_phrase : Rules.qa_phrases) {
			if (text.contains(stop_phrase)) {
				Proof proof = new Proof(ProofType.FAIL_STOP_PHRASES);
				proof.setEvidence(stop_phrase);
				phrase.addProof(proof);
				return false;
			}
		}

		return true;
	}
}
