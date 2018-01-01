package cn.edu.pku.sei.tsr.dragon.codeparser.cfg;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg.DDG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.Miner;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningGraph;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.CFGUtil;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.Functions;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.ParseUtil;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.Predicates;
import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import de.parsemis.graph.Graph;

public class CodeExtractRunner {

	public static void createContentInfo(String library) {
		File threadDir = ObjectIO.getDataObjDirectory(ObjectIO.SOTHREAD_LIBRARY + File.separator + library);
		Stream<File> files = Stream.of(threadDir.listFiles());
		Stream<ContentInfo> contents = files.map(ObjectIO::readObject)
			.map(x -> (OldThreadInfo) x)
			.flatMap(x -> Stream.concat(Stream.of(x.getQuestion()), x.getAnswers().stream()))
			.filter(Predicates.notNull())
			.map(PostInfo::getContent)
			.map(ContentParser::parseContent);

		contents.forEach(p -> ObjectIO.writeObject(p, ObjectIO.CONTENT + File.separator + library + File.separator + p.getUuid() + ObjectIO.DAT_FILE_EXTENSION));
	}

	public static void main(String[] args) {
		Logger.getLogger(BasicCFG.class).setLevel(Level.OFF);

		File threadDir = ObjectIO.getDataObjDirectory(ObjectIO.SOTHREAD_LIBRARY + File.separator + APILibrary.GUAVA);
		Stream<File> files = Stream.of(threadDir.listFiles());
		List<PostInfo> posts = files
			.map(ObjectIO::readObject)
			.map(Functions.cast(OldThreadInfo.class))
			.map(OldThreadInfo::getAnswers)
			.flatMap(List::stream)
			.collect(Collectors.toList());

		posts.forEach(p -> p.getContent().setParent(p));

		Stream<ParagraphInfo> acceptedParagraphs = posts.stream()
			.filter(PostInfo::isAcceptedAnswer)
			.map(PostInfo::getContent)
			.map(ContentParser::parseContent)
			.map(ContentInfo::getParagraphList)
			.filter(Predicates.notNull())
			.flatMap(List::stream)
			.filter(ParagraphInfo::isCodeFragment);

		List<Pair<ParagraphInfo, String>> bodys = acceptedParagraphs
			.map(p -> Pair.of(p, ParseUtil.getMethodBodys(p.toString())))
			.flatMap(p -> p.getRight().stream().map(r -> Pair.of(p.getLeft(), r)))
			.collect(Collectors.toList());

		System.out.println("Accepted paragraph count: " + bodys.size());

		List<Pair<ParagraphInfo, DDG>> ddgs = bodys.stream()
			.map(x -> Pair.of(x.getLeft(), (DDG) DDG.createCFG(x.getRight())))
			.filter(x -> x.getRight() != null)
			.collect(Collectors.toList());

		System.out.println("DDG count: " + ddgs.size());

		List<Graph<MiningNode, Integer>> graphs = Miner.mineGraphFromDDG(ddgs.stream().map(Pair::getRight).collect(Collectors.toList()), Miner.createSetting(3, 2));

		System.out.println("Mined graph count: " + graphs.size());

		Graph<MiningNode, Integer> g = graphs.get(1);

		List<Pair<ParagraphInfo, DDG>> freddgs = ddgs.stream().filter(ddg -> MiningGraph.findSubDDG(ddg.getRight(), g) != null).collect(Collectors.toList());
//		Stream<Pair<ParagraphInfo, DDG>> freddgs = ddgs.stream().filter(ddg -> graphs.stream().anyMatch(graph -> MiningGraph.findSubDDG(ddg.getRight(), graph) != null));
//
		freddgs.forEach(ddgPair -> {
			ContentInfo content = ddgPair.getLeft().getParent();
			PostInfo post = (PostInfo) content.getParent();
			System.out.println(post.getParent().getId());
		});

		CFG cfg = MiningGraph.createCFGFromMiningGraph(freddgs.stream().map(Pair::getRight).collect(Collectors.toList()), g);

		CFGUtil.printCFG(cfg);


//		System.out.println("DDG contains pattern count: " + freddgs.count());


	}

}
