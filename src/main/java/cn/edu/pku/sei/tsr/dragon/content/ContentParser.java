package cn.edu.pku.sei.tsr.dragon.content;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.CodeLikeTermInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;

public class ContentParser {
	public static final Logger logger = Logger.getLogger(ContentParser.class);

	public static ContentInfo parseContent(ContentInfo content) {
		long t0 = System.currentTimeMillis();
		if (content.isHTMLContent())
			HtmlParser.parseHTMLContent(content);
		else
			content.getParagraphList().add(new ParagraphInfo(content.getContent()));

		long t1 = System.currentTimeMillis();
		List<ParagraphInfo> paraList = content.getParagraphList();
		if (paraList != null && paraList.size() > 0) {
			for (int i = 0; i < paraList.size(); i++) {
				long t2 = System.currentTimeMillis();
				ParagraphInfo paragraph = paraList.get(i);
				if (paragraph.isCodeFragment())
					continue;

				List<ParagraphInfo> subParas = separateParagraphByLineBreaker(paragraph);
				if (subParas.size() > 1) {
//					System.err.println("=========");
//					System.err.println(subParas);
					paraList.remove(i);
					paraList.addAll(i, subParas);
					i--;// 重新处理当前段落
					continue;
				}

				//Paragraphs that contain only code elements will produce null sentenceList
				ContentParser.replaceCodeLikeTerms(paragraph);
				SentenceParser.separateParagraphToSentences(paragraph);
				int last = 0;
				for (int id = 0; id < paragraph.getCodeLikeTerms().size(); id++){
					for (int k = last; k < paragraph.getSentences().size(); k++){
						SentenceInfo sent = paragraph.getSentences().get(k);
						String strtmp = sent.getContent();
						if (strtmp.contains("CODE"+id)){
							strtmp = strtmp.replace("CODE"+id, paragraph.getCodeLikeTerms().get(id).toString());
							sent.setContent(strtmp);
							last = k;
							break;
						}
					}
				}
				long t3 = System.currentTimeMillis();
			}
		}
		long t_end = System.currentTimeMillis();
		// logger.info("[parse html: " + (t1 - t0) + "ms][others: " + (t2 - t1) + "ms]");
		return content;
	}

	public static List<ParagraphInfo> separateParagraphByLineBreaker(ParagraphInfo paragraphInfo) {
		List<ParagraphInfo> paragraphList = new ArrayList<>();

		if (paragraphInfo == null || paragraphInfo.getContent() == null)
			return paragraphList;
		String text = paragraphInfo.getContent();
		String[] splittedParas = text.split("\\r?\\n");
		for (String para : splittedParas) {
			para = para.trim();
			// System.err.println("=======");
			// System.err.println(para);
			if (!para.equals("")) {
				ParagraphInfo paragraph = new ParagraphInfo(para);
				paragraphList.add(paragraph);
			}
		}
		return paragraphList;
	}

	public static ParagraphInfo replaceCodeLikeTerms(ParagraphInfo paragraph) {
		String text = paragraph.getContent();
		List<CodeLikeTermInfo> codeLikeTerms = new ArrayList<>();

		// TODO:处理点连接的调用链
		// java.security.cert.CertificateException:

		// 处理<code>结点内容,如果是列表第k+1个加入的，编号为CODEk
		String codeTagStart = "<code>";
		String codeTagEnd = "</code>";
		try {
			while (text.contains(codeTagStart) && text.contains(codeTagEnd)) {
				int startIndex = text.indexOf(codeTagStart);
				int endIndex = text.indexOf(codeTagEnd, startIndex);
				if (startIndex < 0 || endIndex < 0 || startIndex >= endIndex)
					break;
				String placeholder = "CODE" + codeLikeTerms.size();
				String codeLikeTerm = text.substring(startIndex, endIndex + codeTagEnd.length());
				CodeLikeTermInfo codeLikeTermInfo = new CodeLikeTermInfo(codeLikeTerm);
				text = text.substring(0, startIndex) + " " + placeholder + " "
						+ text.substring(endIndex + codeTagEnd.length());
				codeLikeTerms.add(codeLikeTermInfo);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(text);

		}
		text = text.replace(". ", " . ");
		text = text.replace("? ", " ? ");
		text = text.replace("! ", " ! ");
		text = text.replace(";", " ; ");
		String[] splittedString = text.split(" ");
		String result = "";
		for (String str : splittedString) {
			if (isBracket(str) || isaCall(str) || isCamelCase(str)) {
				CodeLikeTermInfo codeFragment = new CodeLikeTermInfo(str);
				codeLikeTerms.add(codeFragment);
				result += "CODE" + (codeLikeTerms.size() - 1) + " ";
			}
			else
				result += str + " ";
		}
		result = result.trim();
		// if (codeLikeTerms.size() > 0) {
		// System.err.println("====================================");
		// System.err.println("result:" + result);
		// System.err.println("clt:" + codeLikeTerms);
		// }
		paragraph.setContent(result);
		paragraph.setCodeLikeTerms(codeLikeTerms);

		return paragraph;
	}	
	
	public static SentenceInfo replaceCodeLikeTerms(SentenceInfo sentence) {
		String text = sentence.getContent();
		List<CodeLikeTermInfo> codeLikeTerms = new ArrayList<>();

		// TODO:处理点连接的调用链
		// java.security.cert.CertificateException:

		// 处理<code>结点内容,如果是列表第k+1个加入的，编号为CODEk
		String codeTagStart = "<code>";
		String codeTagEnd = "</code>";
		try {
			while (text.contains(codeTagStart) && text.contains(codeTagEnd)) {
				int startIndex = text.indexOf(codeTagStart);
				int endIndex = text.indexOf(codeTagEnd, startIndex);
				if (startIndex < 0 || endIndex < 0 || startIndex >= endIndex)
					break;
				String placeholder = "CODE" + codeLikeTerms.size();
				String codeLikeTerm = text.substring(startIndex + codeTagStart.length(), endIndex);
				CodeLikeTermInfo codeLikeTermInfo = new CodeLikeTermInfo(codeLikeTerm);
				codeLikeTermInfo.setParent(sentence);
				text = text.substring(0, startIndex) + " " + placeholder + " "
						+ text.substring(endIndex + codeTagEnd.length());
				codeLikeTerms.add(codeLikeTermInfo);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(text);

		}
		text = text.replace(". ", " . ");
		text = text.replace("? ", " ? ");
		text = text.replace("! ", " ! ");
		text = text.replace(";", " ; ");
		if (text.endsWith(".")){
			text = text.substring(0,text.length()-1)+" .";
		}
		String[] splittedString = text.split(" ");
		String result = "";
		for (String str : splittedString) {
			if (isBracket(str) || isaCall(str) || isCamelCase(str)) {
				CodeLikeTermInfo codeFragment = new CodeLikeTermInfo(str);
				codeFragment.setParent(sentence);
				codeLikeTerms.add(codeFragment);
				result += "CODE" + (codeLikeTerms.size() - 1) + " ";
			}
			else
				result += str + " ";
		}
		result = result.trim();
		// if (codeLikeTerms.size() > 0) {
		// System.err.println("====================================");
		// System.err.println("result:" + result);
		// System.err.println("clt:" + codeLikeTerms);
		// }
		sentence.setContent(result);
		sentence.setCodeLikeTerms(codeLikeTerms);

		return sentence;
	}
	
	public static SentenceInfo replaceCodeTags(SentenceInfo sentence) {
		String text = sentence.getContent();
		List<CodeLikeTermInfo> codeLikeTerms = new ArrayList<>();

		// TODO:处理点连接的调用链
		// java.security.cert.CertificateException:

		// 处理<code>结点内容,如果是列表第k+1个加入的，编号为CODEk
		String codeTagStart = "<code>";
		String codeTagEnd = "</code>";
		try {
			
				text = text.replaceAll(codeTagStart, "");
				text = text.replaceAll(codeTagEnd, "");
			
			sentence.setContent(text);
			System.err.println(text);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println(text);

		}
		
		return sentence;
	}

	
	private static boolean isaCall(String str) {
		if (str.length() == 0)
			return false;
		if (str.length() < 5)
			return false;
		String patternStr = "[a-zA-Z()]+\\.[a-zA-Z0-9.()]+";
		boolean result = Pattern.matches(patternStr, str);
		return result;
	}

	private static boolean isBracket(String str) {
		if (str.length() == 0)
			return false;
		if (str.length() < 5)
			return false;
		String patternStr = "[a-zA-Z.]+\\([a-zA-Z0-9.()]*\\)";
		boolean result = Pattern.matches(patternStr, str);
		return result;
	}

	private static boolean isCamelCase(String str) {
		if (str.length() == 0)
			return false;
		/*
		 * for (int i = 0; i < str.length(); i++) { Character c = str.charAt(i); if
		 * (!Character.isLetter(c)) return false; } Character ch = str.charAt(0); if
		 * (Character.isUpperCase(ch)) return false; for (int i = 0; i < str.length(); i++) {
		 * Character c = str.charAt(i); if (Character.isUpperCase(c)) return true; }
		 */

		boolean reg1 = Pattern.matches("([A-Z][a-z]*)+", str);// CamelCase
		boolean reg2 = Pattern.matches("[a-z]+([A-Z][a-z]+)+", str); // camelCase
		boolean reg3 = Pattern.matches("[A-Z]+", str); // CAMELCASE, HTTP
		boolean reg4 = Pattern.matches("[A-Z][a-z]+", str); // Singleword

		return (!reg3 && !reg4) && (reg1 || reg2);
	}

	public static void main(String[] args) throws FileNotFoundException {
		String tst1 = "<p> ask KJDS DEJui  zhangsan() lisi.wangwu ZdkjwLLsdjWs sjd zhaoLiu <code> sd </code> asdf::</p><li><pre>shjdwk</pre></li> <li> <code>sdsd</code></li><pre>dd <code> cc </code></pre>";
		String tst2 = "How to build ahjuhd e dWdjk in Eclipse?";
		Scanner sc = new Scanner(new File("testdata/test.html"));
		String sent = "";
		while (sc.hasNext()) {
			sent = sent + sc.nextLine() + "\n";
		}
		String test = tst1;
		System.err.println("test:" + test);
		ContentInfo content = new ContentInfo(test);
		content.setHTMLContent(true);
		ContentParser.parseContent(content);
		// SentenceInfo sentence = new SentenceInfo(test);
		// replaceCodeLikeTerms(sentence);
		// System.err.println("sentence:" + sentence);
		// System.err.println();
		// System.err.println("codeliketerms:" + sentence.getCodeLikeTerms());
		// System.err.println();
	}
}
