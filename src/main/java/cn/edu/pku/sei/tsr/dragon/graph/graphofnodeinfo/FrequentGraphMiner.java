package cn.edu.pku.sei.tsr.dragon.graph.graphofnodeinfo;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.graph.GraphUtils;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.NodeInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import de.parsemis.Miner;
import de.parsemis.graph.Graph;
import de.parsemis.graph.ListGraph;
import de.parsemis.miner.environment.Settings;
import de.parsemis.miner.environment.Statistics;
import de.parsemis.miner.general.Fragment;
import de.parsemis.miner.general.IntFrequency;
import de.parsemis.parsers.LabelParser;
import de.parsemis.strategy.ThreadedDFSStrategy;

/**
 * 读入Graph的列表，然后进行子图挖掘，并调用{@code GraphFilterOfStringNode.filter()}进行初步的过滤。使用NodeInfo作为Graph结点类型
 * 
 * @author ZHUZixiao
 *
 */
public class FrequentGraphMiner {
	public static final Logger logger = Logger.getLogger(FrequentGraphMiner.class);

	private static String								libraryName;
	private static List<Graph<NodeInfo, Integer>>		graphs				= new ArrayList<>();
	private static List<Fragment<NodeInfo, Integer>>	frequentSubgraphs	= new ArrayList<>();
	// 为了保留frequency信息，只能用Fragment来存

	public static void reset(String _libraryName) {
		for (Fragment<NodeInfo, Integer> fragment : frequentSubgraphs) {
			fragment.clear();
			fragment = null;
		}
		graphs = new ArrayList<>();
		frequentSubgraphs = new ArrayList<>();
		libraryName = _libraryName;
	}

	public static void main(String[] args) {
//		
//		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.GRAPH_LIBRARY);
//		File[] subdirs = dataObjDirectory.listFiles();
//		for (File libDir : subdirs) {
//			if (libDir.isDirectory()) {
//				mineGraphOfLibrary(libDir.getName());
//			}
//		}
//		
////		for (String libName : APILibrary.getAllLibraryNames()) {
////			mineGraphOfLibrary(libName);
////		}
		mineGraphOfLibrary(APILibrary.LUCENE);
		
	}

	public static void mineGraphOfLibrary(String libraryName) {
		FrequentGraphMiner.reset(libraryName);
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
				// 原来存的Graph里的node，都不包含number属性，所以要加进去。
				// GraphBuilderOfStringNode.addNodeIndexNumber(graphInfo);
				Graph<NodeInfo, Integer> graph = GraphBuilder.convertToParsemisGraph(graphInfo);
				if (graph != null) {
					graphs.add(graph); // 此时图中的结点编号顺序都是对的，已确认。
				}
			}
			long t2 = System.currentTimeMillis();
			logger.info("Start to mine frequent subgraphs from [" + graphs.size() + "] graphs...");

			mineFrequentGraph();
			graphs = null; // 试着释放空间
			long t3 = System.currentTimeMillis();
			logger.info("You have mined " + frequentSubgraphs.size()
					+ " frequent subgraphs. It costs " + ((t3 - t2) / (float) 1000) + "s");

			FrequentGraphPostRunner.filter(libraryName);
		}
	}

	public static List<Fragment<NodeInfo, Integer>> mineFrequentGraph() {
		for (int i = 0; i < graphs.size(); i++) {
			Graph<NodeInfo, Integer> g = graphs.get(i);
			if (g == null) {
				logger.error("ERROR_NULL_GRAPH!!! INDEX: " + i + "\t" + graphs.get(i));
				FrequentGraphMiner.getGraphs().remove(i);
				i--;
			}
		}
		Settings<NodeInfo, Integer> settings = new Settings<>();
		settings.threadCount = 16;
		settings.naturalOrderedNodeLabels = true;
		settings.algorithm = new de.parsemis.algorithms.gSpan.Algorithm<>();
		settings.strategy = new ThreadedDFSStrategy<>(16, new Statistics());
		settings.minFreq = new IntFrequency(2);// iTextPdf项目只能用3，否则内存就不够了
		settings.factory = new ListGraph.Factory<>(new NodeInfoLabelParser(),
				new IntegerLabelParser());

		Collection<Fragment<NodeInfo, Integer>> result = Miner.mine(graphs, settings);

		frequentSubgraphs.addAll(result);
		result = null;
		return frequentSubgraphs;
	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		FrequentGraphMiner.libraryName = libraryName;
	}

	public synchronized static List<Graph<NodeInfo, Integer>> getGraphs() {
		return graphs;
	}

	public static void setGraphs(List<Graph<NodeInfo, Integer>> graphs) {
		FrequentGraphMiner.graphs = graphs;
	}

	public static List<Fragment<NodeInfo, Integer>> getFrequentSubgraphs() {
		return frequentSubgraphs;
	}

	public static void setFrequentSubgraphs(List<Fragment<NodeInfo, Integer>> frequentSubgraphs) {
		FrequentGraphMiner.frequentSubgraphs = frequentSubgraphs;
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