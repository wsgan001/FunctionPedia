package cn.edu.pku.sei.tsr.dragon.test;

import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class Test {

	public static void main(String[] args) {

		String string = "I haven't played around with natural language parse trees, he would not give it to me.";
		// "He wants to put that into this box, I hope I can move these from there to his room.";
		// ", he'll be a dancer, and he wouldn't said that his father might be a farmer, his family will never be Chinese.";

		SentenceInfo sentence = new SentenceInfo(string);
		SentenceParser.parseGrammaticalTree(sentence);
		sentence.getGrammaticalTree().pennPrint();
		PhraseExtractor.extractVerbPhrases(sentence);

		Filter<Tree> filter = new Filter<Tree>() {
			@Override
			public boolean accept(Tree t) {
				String pattern = "VP < (/VB.*/ < have|has|had)";// "VP < ( NP !< /NN.*/ !< PRP  [ <: DT | [!<< /NN.*/ << ( NP <: DT )] ] ) < ( PP < ( NP < /NN.*/ ) )";

				TregexPattern tregex = TregexPattern.compile(pattern);
				TregexMatcher matcher = tregex.matcher(t);
				return !matcher.matches();
			}
		};
		for (PhraseInfo phrase : sentence.getPhrases()) {
			Tree tree = phrase.getStemmedTree();

			System.out.println("-=============");
			tree.pennPrint();
			Tree newTree = tree.prune(filter);
			System.out.println("*******");
			if (newTree != null)
				newTree.pennPrint();
		}
	}
}
