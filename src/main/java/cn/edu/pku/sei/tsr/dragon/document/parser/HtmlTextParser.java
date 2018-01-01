package cn.edu.pku.sei.tsr.dragon.document.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

/**
 * @author ZHUZixiao
 *
 */
public class HtmlTextParser {
	public static final Logger		logger					= Logger.getLogger(HtmlTextParser.class);
	public static final String		HTML_PARSER_LOG_FILE	= "html_parser_data_log.txt";

	public static final String[]	HTML_TAGS_WHITELIST		= new String[] { "p", "li", "h1", "h2", "h3",
			"h4", "h5", "h6", "pre", "code" };

	public static void main(String[] args) throws IOException {
		System.out.println(Arrays.toString(HTML_TAGS_WHITELIST));
		System.out.println(Arrays.toString(HTML_TAGS_WHITELIST).contains("p"));
		FileWriter fw = new FileWriter(
				new File(Config.getDataLogsDir() + File.separator + HTML_PARSER_LOG_FILE));
		MyPostDAO dao = new MyPostDAO(DBConnPool.getConnection());
		List<PostInfo> posts = dao.getAll();
		for (int i = 0; i < posts.size(); i++) {

			if (i % 30 != 13)
				continue;
			PostInfo postInfo = posts.get(i);

			fw.write("================" + postInfo.getId() + "================\n");
			String str = postInfo.getBody();
			fw.write(str + "\n");
			fw.write("\n");
			String s = parseHTMLTextToParagraphedText(str);
			fw.write(s + "\n");
			fw.write(Arrays.toString(s.split(DocumentParser.PARAGRAPH_END_MARK)) + "\n");
			fw.write("\n");

			logger.info(postInfo.getId());
		}
	}

	public static String parseHTMLTextToParagraphedText(String htmlText) {
		StringBuilder sb = new StringBuilder();
		String[] paras = parseHTMLTextToParagraphs(htmlText);
		for (int i = 0; i < paras.length; i++) {
			sb.append(paras[i] + DocumentParser.PARAGRAPH_END_MARK);
		}
		return sb.toString();
	}

	public static String[] parseHTMLTextToParagraphs(String text) {
		Document htmlRoot = Jsoup.parse(text, "UTF-8");

		Whitelist whitelist = new Whitelist();
		whitelist.addTags(HTML_TAGS_WHITELIST);
		Cleaner cleaner = new Cleaner(whitelist);
		Document cleanedRoot = cleaner.clean(htmlRoot);

		List<String> strs = parseHTMLNodeToParagraphs(cleanedRoot);

		if (strs == null)
			return new String[0];

		return strs.toArray(new String[strs.size()]);
	}

	private static List<String> parseHTMLNodeToParagraphs(Node node) {
		List<String> strings = new ArrayList<>();
		List<Node> childNodes = node.childNodes();
		for (Node childNode : childNodes) {
			if (childNode.nodeName().equals("pre")) {
				for (Node grandChildNode : childNode.childNodes()) {
					if (grandChildNode.nodeName().equals("code")) {
						// decode strange characters
						String text = StringEscapeUtils.unescapeHtml4(((Element) grandChildNode).text());
						strings.add(DocumentParser.PARAGRAPH_CODE_PREFIX + text);
					}
				}
			}
			else if (childNode.nodeName().equals("code")) {
				// should be an impossible case
				System.err.println(childNode);
				String text = StringEscapeUtils.unescapeHtml4(((Element) childNode).text());
				System.err.println(text);
				strings.add(DocumentParser.PARAGRAPH_CODE_PREFIX + text);
			}
			else if (Arrays.toString(HTML_TAGS_WHITELIST).contains(childNode.nodeName())) {
				// the childnode tag is contained in the whitelist, i.e., p, li,
				// h1, h2,...
				strings.add(getNodeText(childNode));
			}
			else {
				strings.addAll(parseHTMLNodeToParagraphs(childNode));
			}
		}

		return strings;
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
		return StringEscapeUtils.unescapeHtml4(nodeText);
	}

}
