package cn.edu.pku.sei.tsr.dragon.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordCoreSplit;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.StanfordParser;
import edu.stanford.nlp.trees.Tree;

public class SentenceParser {

	public static final Logger logger = Logger.getLogger(SentenceParser.class);

	public static void separateParagraphToSentences(ParagraphInfo paraInfo) {
		if (paraInfo instanceof ParagraphInfo) {
			ParagraphInfo textPara = paraInfo;

			List<SentenceInfo> sentences = new ArrayList<SentenceInfo>();

			String str = textPara.getContent();
			List<String> sentList = StanfordCoreSplit.splitSentences(str);
			for (String sentence : sentList) {
				SentenceInfo sentInfo = new SentenceInfo(sentence);
				// 句子的语法解析迁移到切分句子的工作完成之后进行 2015-07-27
				// parseGrammaticalTree(sentInfo);
				sentences.add(sentInfo);
				sentInfo.setParent(paraInfo);
			}
			textPara.setSentences(sentences);
		}
	}

	public static void parseGrammaticalTree(SentenceInfo sentence) {
		String s = sentence.getContent();
		int tot = 0; // 特殊（code-like）符号计数
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '.')
				tot++;
			if (s.charAt(i) == '}')
				tot++;
			if (s.charAt(i) == '{')
				tot++;
			if (s.charAt(i) == '(')
				tot++;
			if (s.charAt(i) == ')')
				tot++;
			if (s.charAt(i) == ';')
				tot++;
		}
		if (tot * 12 > s.length())
			return; // 如果特殊符号占句中字符的比例超过1/12，则视作无效

		String str = sentence.getContent();
		int i;
		for (i = str.length() - 1; i >= 0; i--) {
			char ch = str.charAt(i);
			if (Character.isLetter(ch) || Character.isDigit(ch))
				break;
		}
		str = str.substring(0, i + 1) + "."; // 句末加一个点
		sentence.setContent(str);

		// System.out.println(str);
		// long t1 = System.currentTimeMillis();

		Tree tree = StanfordParser.parseTree(str);
		// pool parser 无用!
		// Tree tree = StanfordParserPool.parseGrammaticalTree(str);

		// long t2 = System.currentTimeMillis();
		// System.err.println("parseTree "+(t2 - t1) + "ms");

	}

	@Deprecated
	public static List<String> getSentencesFromString_old(String str) {
		List<String> ans = new ArrayList<String>();
		String[] p = str.split("\\. |\\? |! ");
		for (String s : p) {
			ans.add(s);
		}
		return ans;
	}

	// @Deprecated
	// public static List<String> getSentencesFromString(String str) {
	// long t1 = System.currentTimeMillis();
	// List<String> sentences = StanfordCoreSplit.splitSentences(str);
	// long t2 = System.currentTimeMillis();
	// // System.out.println((t2 - t1) + "ms");
	// return sentences;
	// }

	// public static List<String> getSentenceList(OldThreadInfo tInfo) {
	// List<ParagraphInfo> lpf = new ArrayList<ParagraphInfo>();
	// List<String> ans = null;
	// lpf.addAll(tInfo.getQuestion().getContent().getParagraphs());
	// lpf.addAll(tInfo.getAcceptedAnswer().getContent().getParagraphs());
	// for (PostInfo pf : tInfo.getAnswerList())
	// lpf.addAll(pf.getContent().getParagraphs());
	// for (ParagraphInfo pgf : lpf) {
	// if (pgf instanceof ParagraphInfo) {
	// String str = ((ParagraphInfo) pgf).getContent();
	// ans = getSentencesFromString(str);
	// }
	// }
	// return ans;
	// }

	public static void main(String args[]) throws FileNotFoundException {
		new StanfordCoreSplit();
		// 这个空构造可删么?
		new StanfordParser(".");
		Scanner sc = new Scanner(new File("a.txt"));
		String sent = "";
		while (sc.hasNext()) {
			sent = sent + sc.nextLine();
		}
		// String sent = "";
		System.out.println("\n[Text]:");
		System.out.println(sent);
		ContentInfo content = new ContentInfo(sent);
		ContentParser.parseContent(content);
	}

	//
	// public static void separateParagraphToSentencesInThread(OldThreadInfo thread) {
	// separateParagraphToSentences(thread.getTitleAsParagraph());
	// separatePostToSentences(thread.getQuestion());
	// separatePostToSentences(thread.getAcceptedAnswer());
	// for (PostInfo postInfo : thread.getAnswerList()) {
	// separatePostToSentences(postInfo);
	// }
	// }
	//
	// public static void separatePostToSentences(PostInfo postInfo) {
	// if (postInfo == null)
	// return;
	// if (postInfo.getContent().getParagraphList() != null) {
	// for (ParagraphInfo para : postInfo.getContent().getParagraphList()) {
	// separateParagraphToSentences(para);
	// }
	// }
	// if (postInfo.getComments() != null) {
	// for (CommentInfo commentInfo : postInfo.getComments()) {
	// separateParagraphToSentences(commentInfo.getContent());
	// }
	// }
	//
	// }

}
