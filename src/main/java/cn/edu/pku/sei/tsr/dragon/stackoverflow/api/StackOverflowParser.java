package cn.edu.pku.sei.tsr.dragon.stackoverflow.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import cn.edu.pku.sei.tsr.dragon.codeparser.utils.ParseUtil;
import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.utils.HttpUtils;

public class StackOverflowParser {
	public static Pair<ContentInfo, ContentInfo> parse(String content) {
		Document doc = Jsoup.parse(content);

		Element question = doc.select("div.question").first().select("div.post-text").first();
		ContentInfo questionContent = new ContentInfo(question.toString());
		questionContent.setHTMLContent(true);
		ContentParser.parseContent(questionContent);

		Element answer = doc.select("div.accepted-answer").first().select("div.post-text").first();
		ContentInfo answerContent = new ContentInfo(answer.toString());
		answerContent.setHTMLContent(true);
		ContentParser.parseContent(answerContent);

		return Pair.of(questionContent, answerContent);
	}

	public static void main(String[] args) {
		List<ParsedResult> results = StackExchange.search("parse json array", "java", 10, StackOverflowParser::parseSearchResult);
		List<String> codes = new ArrayList<>();
		for (ParsedResult result : results) {
//			codes.addAll(result.codeInAnswer);
			System.out.println(result.questionID);
		}

//		List<DDG> ddgs = codes.stream()
//			.map(x -> (DDG) DDG.createCFG(x))
//			.collect(Collectors.toList());
//
//		List<Graph<MiningNode, Integer>> graphs = Miner.mineGraphFromDDG(ddgs, Miner.createSetting(2, 2));
//
//		System.out.println(graphs.size());
//		int num = 0;
//
//		for (Graph<MiningNode, Integer> graph : graphs) {
//			CFG cfg = MiningGraph.createCFGFromMiningGraph(ddgs, graph);
//			CFGUtil.saveCFG(cfg, "testdata/cfg/" + num++ + ".png");
//		}
	}

	private static ParsedResult parseSearchResult(SearchResult result) {
		List<String> codes = new ArrayList<>();
		String html = HttpUtils.get(result.link);
		Pair<ContentInfo, ContentInfo> parsed = parse(html);
		ContentInfo questionInfo = parsed.getLeft();
		ContentInfo answerInfo = parsed.getRight();
		answerInfo.getParagraphList().stream().filter(ParagraphInfo::isCodeFragment).forEach(p -> {
			codes.addAll(ParseUtil.getMethodBodys(p.getContent()));
		});
		if (codes.isEmpty()) return null;
		return new ParsedResult(result.questionID, questionInfo, answerInfo, codes);
	}
}
