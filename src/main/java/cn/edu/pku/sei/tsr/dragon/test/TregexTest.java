package cn.edu.pku.sei.tsr.dragon.test;

import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class TregexTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String string = "Recently I stumbled upon some projects using and creating such editors:";
		// "He wants to put the cute boy into this box, I hope I can move these boxes from there to his small movie room.";

		// "I could never be playing around with natural language parse trees, he hasn't given it to me.";
		Tree tree = StanfordParser.parseTree(string);

		// tree = Tree.valueOf("	 (S	" + "	 (NP (PRP I))	" + "	 (VP (VBP hope)	"
		// + "	 (SBAR	" + "	 (S	" + "	 (NP (PRP I))	"
		// + "	 (VP (MD can)	" + "	 (VP (VB move)	"
		// + "	 (NP	" + "	 (NP (DT that))	"
		// // + "	 (PP (IN from)	" + "	 (NP (RB there))))	"
		// + "	 (PP (TO to)	" + "	 (NP (DT this)))))))))	"
		// + "	 (, ,)	");

		tree.pennPrint();
		String pattern = "";
		// "(__ <<box),,(__ << (cute .. boy))";
		// " VP , (__ << (to , (__ << wants))) ";
		// "NP <+(nsubj) /NN.*/";
		// "(VP < (/VB.*/=verb ) ) : /VB.*/ !< has";//" (VP < (/VB.*/=verb !<has|be) ) ";//" /VB.*/ >>VP  !< be"
		// ;//"VP < ( NP !< /NN.*/ !< PRP  [ <: DT | [!<< /NN.*/ << ( NP <: DT )] ] ) < ( PP < ( NP < /NN.*/ ) )";

		TregexPattern tregex = TregexPattern.compile(pattern);
		TregexMatcher matcher = tregex.matcher(tree);

		while (matcher.findNextMatchingNode()) {
			Tree a = matcher.getMatch();

			System.out.println("###　MATCH ###");
			a.pennPrint();
		}

	}

}
