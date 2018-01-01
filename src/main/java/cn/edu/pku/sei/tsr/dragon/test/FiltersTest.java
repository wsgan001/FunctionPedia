package cn.edu.pku.sei.tsr.dragon.test;

import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;

public class FiltersTest {

	public static void main(String[] args) {
		String string = "I'm trying to create a new cell whom content is the result of a formula as if the user had copied/pasted the formula (what i call the \"relative\" way, as opposite to \"absolute\").";
		// "NoSuchMethod exception while creating and formatting .doc file using Apache poi-3.8 beta hwpf";
		// "He got a score of 3.7 in the exams.";
		// "He wants to give us an idea of how things are scored.";
		// "I could never be playing around with natural language parse trees, he wants to handle empty cells in excel sheets.";
		// "He wants to put that into this box, I hope I can move these from there to his room.";
		// ", he'll be a dancer, and he wouldn't said that his father might be a farmer, his family will never be Chinese.";

		SentenceInfo sentence = new SentenceInfo(string);
		SentenceParser.parseGrammaticalTree(sentence);
		sentence.getGrammaticalTree().pennPrint();
		PhraseExtractor.extractVerbPhrases(sentence);

		for (PhraseInfo phrase : sentence.getPhrases()) {
			System.out.println("==================");
			phrase.getStemmedTree().pennPrint();
			long t1 = System.currentTimeMillis();
			PhraseFilter.filter(phrase, sentence);
			long t2 = System.currentTimeMillis();
			// System.out.println("TIME: " + (t2 - t1) + "ms");

			// for (Proof p : phrase.getProofs()) {
			// System.out.println(p.getWeight());
			// System.out.println(p.getProofType());
			// }

			System.out.println(phrase.getProofString());
		}
	}

}
