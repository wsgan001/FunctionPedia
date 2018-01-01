package cn.edu.pku.sei.tsr.dragon.test;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.utils.Config;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class hehe {
	public static void main(String[] args) throws FileNotFoundException {
		LexicalizedParser lp = LexicalizedParser.loadModel(Config.getLexicalModelFile());
		demoAPI(lp);

	}
	public static void demoAPI(LexicalizedParser lp) throws FileNotFoundException {
		// Scanner sc = new Scanner(new File("testdata\\a.txt"));
		// String sent = "";
		// while (sc.hasNext()) {
		// sent = sent + sc.nextLine();
		// }
		String string = "He wants to put them into that room, I hope I can move that from there to this, he'll be a dancer, and he wouldn't said that his father might be a farmer, his family will never be Chinese.";
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(
				new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords = tokenizerFactory.getTokenizer(new StringReader(string))
				.tokenize();
		Tree parse = lp.apply(rawWords);

		parse.pennPrint();
		// System.out.println(parse.label());

		String pattern = "VP < ( NP < ( PRP < it|them ) ) < ( PP < ( NP < /NN.*/ ) )";
		// "VP<(/VB.*/ < be|am|is|are|was|were|been|being|'s|'m|'re)";
		// "VP < /VB.*/ <(PP<NP) | <NP";
		// //"VP < /VB.*/  !<< been|'ve|am|is|are|be|want|need|problem";//"PP <<NNS | <NP  $PRT";//"@NP";//
		// "VP < /VB.*/";

		TregexPattern semgrex = TregexPattern.compile(pattern);
		TregexMatcher matcher = semgrex.matcher(parse);

		HashSet<Tree> ts = new HashSet<Tree>();
		// This will produce two results on the given tree: "likes" is an
		// ancestor of both "dog" and "my" via the nsubj relation
		while (matcher.findNextMatchingNode()) {
			// matcher.getNode(pattern).pennPrint();

			Tree a = matcher.getMatch();
			ts.add(a);

			System.out.println("###　MATCH ###");
			a.pennPrint();
		}

		for (Tree tree : ts) {
			// System.out.println("###　SET ###");
			// tree.pennPrint();
		}

		// List<Tree> cs = parse.postOrderNodeList();
		// for (int i = 0; i < cs.size(); i++) {
		// if (cs.get(i).label().toString().equalsIgnoreCase("VP")) {
		// List<Tree> c = cs.get(i).getLeaves();
		// for (int j = 0; j < c.size(); j++)
		// System.out.print("" + c.get(j) + " ");
		// System.out.println();
		// }
		// }

	}
}
