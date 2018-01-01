package cn.edu.pku.sei.tsr.dragon.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;

public class HtmlParser {
	private static final Logger logger = Logger.getLogger(HtmlParser.class);

	public static ContentInfo parseHTMLContent(ContentInfo content) {
		String contentText = content.getContent();
		if (contentText == null)
			return content;

		Document htmlRoot = Jsoup.parse(content.getContent(), "UTF-8");

		List<ParagraphInfo> paragraphList = parseHTMLNodeToParagraphs(htmlRoot);
		if (paragraphList != null && paragraphList.size() > 0)
			content.getParagraphList().addAll(paragraphList);
		else {
			ParagraphInfo paragraphInfo = new ParagraphInfo(content.getContent());
			content.getParagraphList().add(paragraphInfo);
		}

		content.getParagraphList().forEach(paragraph -> {
			paragraph.setParent(content);
		});
		return content;
	}

	public static List<String> parseHTMLContent(String text) {
		Document htmlRoot = Jsoup.parse(text, "UTF-8");

//		System.out.println(text);
//		System.out.println(htmlRoot);
		List<String> strs = parseHTMLNodeToStrings(htmlRoot);
//		System.out.println(strs);
		return strs;
	}

	private static List<String> parseHTMLNodeToStrings(Node node) {
		List<String> strings = new ArrayList<>();
		List<Node> childNodes = node.childNodes();
		for (Node childNode : childNodes) {
			if (childNode.nodeName().equals("p") || childNode.nodeName().equals("li")) {
				strings.add(getNodeText(childNode));
			}
			else if (childNode.nodeName().equals("pre")) {
				for (Node grandChildNode : childNode.childNodes()) {
					if (grandChildNode.nodeName().equals("code")) {
						// <pre>...<code></code>...</pre>
//						System.err.println(grandChildNode.toString());
						strings.add(grandChildNode.toString());
					}
				}
			}
			else {
				strings.addAll(parseHTMLNodeToStrings(childNode));
			}
		}
		// content.setParagraphs(paragraphs);
		return strings;
	}

	private static List<ParagraphInfo> parseHTMLNodeToParagraphs(Node node) {
		List<ParagraphInfo> paragraphList = new ArrayList<>();
		List<Node> childNodes = node.childNodes();
		for (Node childNode : childNodes) {
			if (childNode.nodeName().equals("p") || childNode.nodeName().equals("li")) {
				ParagraphInfo para = new ParagraphInfo(getNodeText(childNode));
				paragraphList.add(para);
			}
			else if (childNode.nodeName().equals("pre")) {
				for (Node grandChildNode : childNode.childNodes()) {
					if (grandChildNode.nodeName().equals("code")) {
						// <pre>...<code></code>...</pre>
						ParagraphInfo codeFragment = new ParagraphInfo(StringEscapeUtils.unescapeHtml4(((Element) grandChildNode).text()));
						codeFragment.setCodeFragment(true);
						// System.out.println("-----------\n" + codeFragment);
						paragraphList.add(codeFragment);
					}
				}
			}
			else {
				paragraphList.addAll(parseHTMLNodeToParagraphs(childNode));
			}
		}
		// content.setParagraphs(paragraphs);
		return paragraphList;
	}

	private static String getNodeText(Node node) {
		String nodeText = "";
		if (node.nodeName().equals("code")) {
			nodeText = node.outerHtml(); // 保留code标签，如果是pre-code怎么办？
		}
		else if (node.nodeName().equals("#text")) {
			nodeText = node.toString();
		}
		else {
			for (Node childNode : node.childNodes()) {
				nodeText += getNodeText(childNode);
			}
		}
		return nodeText;
	}

	public static void main(String args[]) throws FileNotFoundException {
		String test = "<p>asd KJDS DEJui \r\n zhangsan(cxdf)\r lisi.wangwu ZdkjwLLsdjWs sjd zhaoLiu <code> sd </code> asdf::</p><li><pre>shjdwk</pre></li> <li> <code>sdsd</code></li><pre>dd <code> cc </code></pre>";
		String tst2 = "How to build ahjuhd e dwdjk in Eclipse?";
		Scanner sc = new Scanner(new File("testdata/test.html"));
		String sent = "";
		while (sc.hasNext()) {
			sent = sent + sc.nextLine() + "\n";
		}
		// new StanfordCoreSplit();
		// new StanfordParser(".");
		long t1 = System.currentTimeMillis();
		System.out.println(test);
		ContentInfo content = new ContentInfo(test);
		// HtmlParser.parseHTMLContent(content);
		// System.out.println(content);
		// for (ParagraphInfo p : content.getParagraphList()) {
		// System.out.println("======");
		// System.out.println(p.getContent());
		// }
		System.out.println();
		for (ParagraphInfo p : ContentParser
				.separateParagraphByLineBreaker(new ParagraphInfo(test))) {
			System.out.println("======");
			System.out.println(p.getContent());
		}
		// System.out.println(content.getParagraphList());
		// PostInfo postInfo = new PostInfo();
		// postInfo.setContent(h.getContent());
		// System.out.println(h.getContent().getParagraphs().get(0)).);
		// SentenceParser.separatePostToSentences(postInfo);
		long t3 = System.currentTimeMillis();
		System.out.println(t3 - t1);
	}

	@Deprecated
	// <pre>...<code></code>...</pre>
	private ParagraphInfo findPreCode(Node node) {
		if (node.nodeName().equals("code") && node.parentNode().nodeName().equals("pre")) {
			ParagraphInfo codeFragment = new ParagraphInfo(node.parentNode().outerHtml());
			codeFragment.setCodeFragment(true);
			return codeFragment;
			// logger.info("[precode-html]" + cParaInfo.getHtmlContent());
		}
		// for (Node childNode : node.childNodes()) {
		// findPreCode(childNode);
		// }
		return null;
	}

	// private static String getText_old(Node node) {
	// String nodeText = "";
	// if (node.nodeName().equals("code") &&
	// node.parentNode().nodeName().equals("pre")) {
	// }
	// else if (node.nodeName().equals("code")) {
	// CodeLikeTermInfo codeFragment = new CodeLikeTermInfo(node.outerHtml());
	// codeFragmentList.add(codeFragment);
	// nodeText = "CODE" + (codeFragmentList.size() - 1); //
	// 如果是列表第k+1个加入的，编号为CODEk
	// }
	// else if (node.nodeName().equals("#text")) {
	// String[] splittedString = node.toString().split(" ");
	// for (String str : splittedString) {
	// if (isBracket(str) || isaCall(str) || isCamelCase(str)) {
	// CodeLikeTermInfo codeFragment = new CodeLikeTermInfo(str);
	// codeFragmentList.add(codeFragment);
	// nodeText = nodeText + "CODE" + (codeFragmentList.size() - 1) + " ";
	// }
	// else
	// nodeText = nodeText + str + " ";
	// }
	// nodeText.trim();
	// }
	// else {
	// for (Node childNode : node.childNodes()) {
	// nodeText += getNodeText(childNode);
	// }
	// }
	// return nodeText;
	// }
}
