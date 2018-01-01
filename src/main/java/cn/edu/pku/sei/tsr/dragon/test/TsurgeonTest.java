package cn.edu.pku.sei.tsr.dragon.test;

import java.util.regex.Pattern;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class TsurgeonTest {
	public static void main(String[] args0) {
		Tree t = Tree
				.valueOf("(VP (VBG anti-aliasing) (ADVP (RB clearly)) (PP (IN on) (NP (DT the) (JJ glyph) (NNS sizes))))");
		// (ROOT (S (NP (NP (NNP Bank)) (PP (IN of) (NP (NNP America)))) (VP
		// (VBD called)) (. .)))");
		// TregexPattern pat =
		// TregexPattern.compile("VP < /VB.*/=a < (PP <IN=c < (NP <- /NN.*/=b ))");
		// TsurgeonPattern surgery =
		// Tsurgeon.parseOperation("[excise x x] [excise y y]");
		// Tsurgeon.processPattern(pat, surgery, t).pennPrint();
		t.pennPrint();

		Tree[] children = t.children();
		for (int i = 0; i < t.children().length; i++) {
			TregexPattern pat = TregexPattern.compile("/VB.*/");
			TregexMatcher matcher = pat.matcher(children[i]);
			if (matcher.matches()) {
				Pattern vb_ptn = Pattern.compile("[VB.*]");
				vb_ptn.matcher(children[i].label().toString());
				if (matcher.matches()) {
					System.out.println(children[i]);
				}
				continue;
			}
			else {
				pat = TregexPattern.compile("PP");
				matcher = pat.matcher(children[i]);
				if (!matcher.matches()) {
					System.err.println("hello");
					t.removeChild(i).pennPrint();
					t.pennPrint();
				}
				else {

				}

			}

		}

	}
}
