package cn.edu.pku.sei.tsr.dragon.test;

import java.util.List;

import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import edu.stanford.nlp.trees.Tree;

public class TreeTest {

	public static void main(String[] args) {
		String string = // "He got a score of 3.7 in the exams.";
		//"He wants to give us an idea of how things are scored.";
		// "I could never be playing around with natural language parse trees, he wants to handle empty cells in excel sheets.";
		// "He wants to put that into this box, I hope I can move these from there to his room.";
		// ", he'll be a dancer, and he wouldn't said that his father might be a farmer, his family will never be Chinese.";
				//"extract verb phases";
				"to formula string";

		SentenceInfo sentence = new SentenceInfo(string);
		SentenceParser.parseGrammaticalTree(sentence);
		sentence.getGrammaticalTree().pennPrint();
		PhraseExtractor.extractVerbPhrases(sentence);
		Tree copyTree = sentence.getGrammaticalTree().deepCopy();
		System.out.println(copyTree.equals(sentence.getGrammaticalTree()));

		List<PhraseInfo> phrases = sentence.getPhrases();
		for (int i = phrases.size() - 1; i >= 0; i--) {
			PhraseInfo phrase = phrases.get(i);
			Tree tree = phrase.getStemmedTree();
			System.out.println("===1====");
			tree.pennPrint();
			System.out.println("===2====");
			Tree parent = tree.parent(sentence.getGrammaticalTree());// TreeUtils.getParent(tree,
																		// copyTree);
			if (parent == null)
				continue;
			parent.pennPrint();
			int index = parent.objectIndexOf(tree);// TreeUtils.indexOf(tree,
													// parent);
			System.out.println(index);
			parent.removeChild(index);
			parent.pennPrint();
			System.out.println("===3====");
			tree.pennPrint();
			System.out.println("===4====");
		}

	}
}
