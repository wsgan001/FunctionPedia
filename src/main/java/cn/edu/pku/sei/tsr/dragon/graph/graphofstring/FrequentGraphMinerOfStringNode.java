package cn.edu.pku.sei.tsr.dragon.graph.graphofstring;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import de.parsemis.Miner;
import de.parsemis.graph.Graph;
import de.parsemis.graph.ListGraph;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.environment.Statistics;
import de.parsemis.miner.general.Fragment;
import de.parsemis.miner.general.Frequency;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.LabelParser;
import de.parsemis.strategy.ThreadedDFSStrategy;

/**
 * 读入Graph的列表，然后进行子图挖掘，并调用{@code GraphFilterOfStringNode.filter()}进行初步的过滤 使用String 作为Graph结点类型
 * 
 * @author ZHUZixiao
 *
 */
public class FrequentGraphMinerOfStringNode {
	public static final Logger logger = Logger.getLogger(FrequentGraphMinerOfStringNode.class);

	private static String							libraryName;
	private static List<Graph<String, Integer>>		graphs				= new ArrayList<>();
	private static List<Fragment<String, Integer>>	frequentSubgraphs	= new ArrayList<>();
	// 为了保留frequency信息，只能用Fragment来存

	public static void reset(String _libraryName) {
		for (Fragment<String, Integer> fragment : frequentSubgraphs) {
			fragment.clear();
			fragment = null;
		}
		for (Graph<String, Integer> graph : graphs) {
			graph = null;
		}
		graphs = new ArrayList<>();
		frequentSubgraphs = new ArrayList<>();
		libraryName = _libraryName;
	}

	public static void main(String[] args) {
//		for (String libName : APILibrary.getAllLibraryNames()) {
//			if (!libName.equals(APILibrary.ITEXTPDF) && !libName.equals(APILibrary.LUCENE)
//					&& !libName.equals(APILibrary.NEO4J))
//				mineGraphOfLibrary(libName);
//		}
		mineGraphOfLibrary(APILibrary.POI);
	}

	public static void mineGraphOfLibrary(String libraryName) {
		FrequentGraphMinerOfStringNode.reset(libraryName);
		System.gc();

		logger.info("[" + libraryName + "] Read in graph files...");
		long t0 = System.currentTimeMillis();

		File libfile = ObjectIO.getDataObjDirectory(ObjectIO.GRAPH_LIBRARY + File.separator
				+ libraryName + ObjectIO.DAT_FILE_EXTENSION);
		Object obj = ObjectIO.readObject(libfile);
		long t1 = System.currentTimeMillis();
		logger.info("Loaded graphInfo list:" + (t1 - t0) + "ms");

		if (obj != null && obj instanceof List<?>) {
			List<GraphInfo> graphList = (List<GraphInfo>) obj;
			for (GraphInfo graphInfo : graphList) {
				Graph<String, Integer> graph = GraphBuilderOfStringNode
						.convertToParsemisGraph(graphInfo);
				if (graph != null)
					graphs.add(graph);
			}
			// System.err.println("============="+graphs.size()+"========");
			long t2 = System.currentTimeMillis();
			logger.info("Start to mine frequent subgraphs from [" + graphs.size() + "] graphs...");

			mineFrequentGraph();
			long t3 = System.currentTimeMillis();
			logger.info("You have mined " + frequentSubgraphs.size()
					+ " frequent subgraphs. It costs " + ((t3 - t2) / (float) 1000) + "s");

			GraphFilterOfStringNode.filter(libraryName);
		}
	}

	public static List<Fragment<String, Integer>> mineFrequentGraph() {
		for (int i = 0; i < graphs.size(); i++) {
			Graph<String, Integer> g = graphs.get(i);
			if (g == null) {
				logger.error("ERROR_NULL_GRAPH!!! INDEX: " + i + "\t" + graphs.get(i));
				FrequentGraphMinerOfStringNode.getGraphs().remove(i);
				i--;
			}
		}
		Settings<String, Integer> settings = new Settings<>();
		settings.threadCount = 16;
		settings.naturalOrderedNodeLabels = true;
		settings.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
		settings.strategy = new ThreadedDFSStrategy<>(16, new Statistics());
		settings.minFreq = new IntFrequency(2);// iTextPdf项目只能用3，否则内存就不够了
		settings.factory = new ListGraph.Factory<>(new StringLabelParser(),
				new IntegerLabelParser());

		Collection<Fragment<String, Integer>> result = Miner.mine(graphs, settings);

		frequentSubgraphs.addAll(result);
		result = null;
		return frequentSubgraphs;
	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		FrequentGraphMinerOfStringNode.libraryName = libraryName;
	}

	public synchronized static List<Graph<String, Integer>> getGraphs() {
		return graphs;
	}

	public static void setGraphs(List<Graph<String, Integer>> graphs) {
		FrequentGraphMinerOfStringNode.graphs = graphs;
	}

	public static List<Fragment<String, Integer>> getFrequentSubgraphs() {
		return frequentSubgraphs;
	}

	public static void setFrequentSubgraphs(List<Fragment<String, Integer>> frequentSubgraphs) {
		FrequentGraphMinerOfStringNode.frequentSubgraphs = frequentSubgraphs;
	}

	/**
	 * 单线程过滤处理后的子图，现在已经废弃了
	 */
	@Deprecated
	public static void filterFrequentSubgraph() {
		HashMap<String, Integer> graphUUIDToFrequencyMap = new HashMap<>();
		HashMap<Integer, Integer> frequencyDistributionMap = new HashMap<>();
		long t1 = System.currentTimeMillis();
		int validGraphCount = 0;
		for (Fragment<String, Integer> fragment : frequentSubgraphs) {
			Frequency freq = fragment.frequency();
			Graph<String, Integer> subgraph = fragment.toGraph();

			GraphInfo graphInfo = GraphParserOfStringNode.parseGraphAndValidate(subgraph);
			if (graphInfo != null) {
				int frequency = Integer.parseInt(freq.toString());
				graphUUIDToFrequencyMap.put(graphInfo.getUuid(), frequency); // 频数统计添加到map
				if (frequencyDistributionMap.get(frequency) == null)
					frequencyDistributionMap.put(frequency, 1); // initialize
				else
					frequencyDistributionMap.put(frequency,
							frequencyDistributionMap.get(frequency) + 1); // add

				String filePath = ObjectIO.FREQUENT_SUBGRAPH_STR + File.separator
						+ FrequentGraphMinerOfStringNode.libraryName + File.separator
						+ graphInfo.getUuid() + ObjectIO.DAT_FILE_EXTENSION;
				ObjectIO.writeObject(graphInfo, filePath);// 输出子图到文件夹
				// logger.info("[" + freq.toString() + "]" + graphInfo.toString());

				validGraphCount++;
				graphInfo = null;
			}
		}
		long t2 = System.currentTimeMillis();
		logger.info("[" + (t2 - t1) + "ms] " + validGraphCount
				+ " valid graphs have been filtered out.");

		String filePath = ObjectIO.FREQUENT_SUBGRAPH_STR_LIBRARY + File.separator
				+ ObjectIO.FREQUENCY_MAP_PREFIX + FrequentGraphMinerOfStringNode.libraryName
				+ ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(graphUUIDToFrequencyMap, filePath);// 输出uuid-频数map到文件

		String filePath2 = ObjectIO.FREQUENT_SUBGRAPH_STR_LIBRARY + File.separator
				+ ObjectIO.FREQUENCY_DISTRIBUTION_PREFIX
				+ FrequentGraphMinerOfStringNode.libraryName + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(frequencyDistributionMap, filePath2);// 输出频数分布map到文件

		System.err.println(frequencyDistributionMap);

		long t3 = System.currentTimeMillis();
		logger.info("Output \"Frequency\" relevant maps to file: " + (t3 - t2) + "ms");
	}
}

class StringLabelParser implements LabelParser<String> {
	private static final long serialVersionUID = 8844206741183890442L;

	@Override
	public String parse(String s) throws ParseException {
		return s;
	}

	@Override
	public String serialize(String node) {
		return node;
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