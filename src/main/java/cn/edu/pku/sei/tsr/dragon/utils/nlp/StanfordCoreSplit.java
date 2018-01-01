package cn.edu.pku.sei.tsr.dragon.utils.nlp;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;

public class StanfordCoreSplit {
	private static Properties props = new Properties();
	static {
		props.put("annotators", "tokenize, ssplit");
	}
	private static final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	public static List<String> splitSentences(String str) {
		List<String> ret = new ArrayList<String>();
		Annotation annotation = new Annotation(str);
		// long t1 = System.currentTimeMillis();
		pipeline.annotate(annotation);

		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		for (CoreMap sent : sentences) {
			ret.add(sent.toString());
		}
		// long t2 = System.currentTimeMillis();
		// if (t2 - t1 > 10)
		// System.err.println("SentSplit:" + (t2 - t1) + "ms");
		return ret;
		/*
		 * for (CoreMap sent : sentences) { ArrayCoreMap sentence =
		 * (ArrayCoreMap) sent; Tree tree =
		 * sentence.get(TreeCoreAnnotations.TreeAnnotation.class); List<Tree>
		 * words = tree.getLeaves(); String s = ""; for (Tree word : words) { s
		 * = s + word + " "; } s = s.replace("-RRB-", ")"); s =
		 * s.replace("-LRB-", "("); ret.add(s); } return ret;
		 */
	}

	public static ArrayList<String> splitSentenceWithPunctuation(String text) {
		PTBTokenizer ptbt = new PTBTokenizer(new StringReader(text), new CoreLabelTokenFactory(), "");
		ArrayList<String> result = new ArrayList<String>();
		for (CoreLabel label; ptbt.hasNext();) {
			label = (CoreLabel) ptbt.next();
			result.add(label.toString());
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		String s = "Write a program that call the java.lang.getDate(). get() method";
		// "Kosgi Santosh sent some email , e.g. goods and flies to Stanford
		// University. He didn't
		// get a reply etc. gd or sd.";
		for (String ss : StanfordCoreSplit.splitSentences(s)) {
			System.out.println(ss);
		}

	}

}
