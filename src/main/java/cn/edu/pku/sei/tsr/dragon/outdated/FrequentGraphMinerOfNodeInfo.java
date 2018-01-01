package cn.edu.pku.sei.tsr.dragon.outdated;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.graph.GraphUtils;
import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeInfo;
import de.parsemis.Miner;
import de.parsemis.graph.Graph;
import de.parsemis.graph.ListGraph;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.general.Fragment;
import de.parsemis.miner.general.Frequency;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.LabelParser;
import de.parsemis.strategy.BFSStrategy;

public class FrequentGraphMinerOfNodeInfo {
	public static final Logger logger = Logger.getLogger(FrequentGraphMinerOfNodeInfo.class);

	private static String								libraryName;
	private static List<Graph<NodeInfo, Integer>>		graphs				= new ArrayList<>();
	private static List<Fragment<NodeInfo, Integer>>	frequentSubgraphs	= new ArrayList<>();
	// 为了保留frequency信息，只能用Fragment来存

	public static List<Fragment<NodeInfo, Integer>> mineFrequentGraph() {
		Settings<NodeInfo, Integer> settings = new Settings<>();
		settings.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
		settings.strategy = new BFSStrategy<>();
		settings.minFreq = new IntFrequency(10);
		settings.factory = new ListGraph.Factory<>(new NodeInfoLabelParser(),
				new IntegerLabelParser());

		Collection<Fragment<NodeInfo, Integer>> result = Miner.mine(graphs, settings);

		frequentSubgraphs.addAll(result);
		return frequentSubgraphs;
	}

	public static void traverseFrequentSubgraph() {
		for (Fragment<NodeInfo, Integer> fragment : frequentSubgraphs) {
			Frequency freq = fragment.frequency();
			Graph<NodeInfo, Integer> subgraph = fragment.toGraph();
			if (Integer.parseInt(freq.toString()) >= 3 && subgraph.getNodeCount() >= 4) {
				logger.info(
						"[Freq:" + freq.toString() + "]" + GraphUtils.toString(subgraph));
			}
		}
	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		FrequentGraphMinerOfNodeInfo.libraryName = libraryName;
	}

	public static List<Graph<NodeInfo, Integer>> getGraphs() {
		return graphs;
	}

	public static void setGraphs(List<Graph<NodeInfo, Integer>> graphs) {
		FrequentGraphMinerOfNodeInfo.graphs = graphs;
	}

	public static List<Fragment<NodeInfo, Integer>> getFrequentSubgraphs() {
		return frequentSubgraphs;
	}

	public static void setFrequentSubgraphs(List<Fragment<NodeInfo, Integer>> frequentSubgraphs) {
		FrequentGraphMinerOfNodeInfo.frequentSubgraphs = frequentSubgraphs;
	}
}

class NodeInfoLabelParser implements LabelParser<NodeInfo> {
	private static final long serialVersionUID = 8844206741183890442L;

	@Override
	public NodeInfo parse(String s) throws ParseException {
		return GraphUtils.parseStringToNodeInfo(s);
	}

	@Override
	public String serialize(NodeInfo node) {
		return node.toString();
	}
}

class IntegerLabelParser implements LabelParser<Integer> {
	private static final long serialVersionUID = 4752511081764291545L;

	@Override
	public Integer parse(String s) throws ParseException {
		return Integer.parseInt(s);
	}

	@Override
	public String serialize(Integer integer) {
		return integer.toString();
	}
}
