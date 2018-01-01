package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg.DDG;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.Predicates;
import cn.edu.pku.sei.tsr.dragon.utils.FileUtils;
import de.parsemis.graph.Graph;
import de.parsemis.graph.ListGraph;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.IntLabelParser;
import de.parsemis.strategy.BFSStrategy;

public class Miner {
	public static Settings<MiningNode, Integer> createSetting(int minFreq, int minNodes) {
		Settings<MiningNode, Integer> setting = new Settings<>();
		setting.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
		setting.strategy = new BFSStrategy<>();
		setting.minFreq = new IntFrequency(minFreq);
		setting.factory = new ListGraph.Factory<>(new MiningNodeParser(), new IntLabelParser());
		setting.minNodes = minNodes;

		return setting;
	}

	public static List<CFG> mineFromFiles(List<String> files) {
		Settings<MiningNode, Integer> setting = new Settings<>();
		setting.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
		setting.strategy = new BFSStrategy<>();
		setting.minFreq = new IntFrequency(2);
		setting.factory = new ListGraph.Factory<>(new MiningNodeParser(), new IntLabelParser());
		setting.minNodes = 2;

		return mineFromFiles(files, setting);
	}

	public static List<CFG> mineFromFiles(List<String> files, Settings<MiningNode, Integer> setting) {
		List<String> bodys = files.stream()
			.map(f -> new File("testdata/cfg/" + f))
			.map(FileUtils::getFileContent)
			.collect(Collectors.toList());

		return mine(bodys, setting);
	}

	public static List<CFG> mine(List<String> bodys, Settings<MiningNode, Integer> setting) {
		List<Graph<MiningNode, Integer>> graphs = new ArrayList<>();
		List<DDG> ddgs = bodys.stream()
				.map(DDG::createCFG)
				.map(x -> (DDG) x)
				.filter(Predicates.notNull())
				.collect(Collectors.toList());

		return mineFromDDG(ddgs, setting);
	}

	public static List<Graph<MiningNode, Integer>> mineGraphFromDDG(List<DDG> ddgs, Settings<MiningNode, Integer> setting) {
		List<Graph<MiningNode, Integer>> graphs = ddgs.stream().map(MiningGraph::convertDDGToMiningGraph).collect(Collectors.toList());
		return MiningGraph.resultFilter(de.parsemis.Miner.mine(graphs, setting));
	}

	public static List<CFG> mineFromDDG(List<DDG> ddgs, Settings<MiningNode, Integer> setting) {
		return mineGraphFromDDG(ddgs, setting).stream().map(r -> MiningGraph.createCFGFromMiningGraph(ddgs, r)).collect(Collectors.toList());
	}
}
