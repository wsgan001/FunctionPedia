package cn.edu.pku.sei.tsr.dragon.graph.graphofstring;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import de.parsemis.graph.Graph;
import de.parsemis.miner.general.Fragment;
import de.parsemis.miner.general.Frequency;

public class GraphFilterOfStringNode implements Runnable {
	public static final Logger	logger			= Logger.getLogger(GraphFilterOfStringNode.class);
	public static int			THREAD_LIMIT	= 1024;
	public static int			TIME_UNIT		= 10000;											// 询问是否挖掘完成的时间间隔

	private static long	time			= System.currentTimeMillis();
	private static int	liveThreadCount	= THREAD_LIMIT;					// 最后一个退出的线程作为标志
	private static int	readIndex		= 0;

	private static String						libraryName;
	private static int							validGraphCount				= 0;
	private static HashMap<String, Integer>		graphUUIDToFrequencyMap		= new HashMap<>();
	private static HashMap<Integer, Integer>	frequencyDistributionMap	= new HashMap<>();

	public static synchronized void stopThread() {
		liveThreadCount--;
	}

	public static synchronized int getLiveThreadCount() {
		return liveThreadCount;
	}

	public static void reset(String _libraryName) {
		libraryName = _libraryName;
		validGraphCount = 0;
		graphUUIDToFrequencyMap = new HashMap<>();
		frequencyDistributionMap = new HashMap<>();

		liveThreadCount = THREAD_LIMIT;
		readIndex = 0;
		time = System.currentTimeMillis();
	}

	// 多线程处理时，获取下一个应处理的图
	private static synchronized Fragment<String, Integer> getNext() {
		if (FrequentGraphMinerOfStringNode.getFrequentSubgraphs() == null)
			return null;
		Fragment<String, Integer> next = null;
		try {
			if (readIndex >= FrequentGraphMinerOfStringNode.getFrequentSubgraphs().size())
				return null;
			next = FrequentGraphMinerOfStringNode.getFrequentSubgraphs().get(readIndex);
			readIndex++;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return next;
	}

	@Override
	public void run() {
		Fragment<String, Integer> nextFragment;
		while ((nextFragment = getNext()) != null) {
			Frequency freq = nextFragment.frequency();
			Graph<String, Integer> subgraph = nextFragment.toGraph();

			GraphInfo graphInfo = GraphParserOfStringNode.parseGraphAndValidate(subgraph);
			
			if (graphInfo != null) {
				int frequency = Integer.parseInt(freq.toString());
				graphUUIDToFrequencyMap.put(graphInfo.getUuid(), frequency); // 频数统计添加到map
				if (frequencyDistributionMap.get(frequency) == null)
					frequencyDistributionMap.put(frequency, 1); // initialize
				else
					frequencyDistributionMap.put(frequency,
							frequencyDistributionMap.get(frequency) + 1); // add

				String filePath = ObjectIO.FREQUENT_SUBGRAPH_STR + File.separator + libraryName
						+ File.separator + graphInfo.getUuid() + ObjectIO.DAT_FILE_EXTENSION;
				ObjectIO.writeObject(graphInfo, filePath);// 输出子图到文件夹
				// logger.info("[" + freq.toString() + "]" + graphInfo.toString());
				validGraphCount++;
			}
		}

		stopThread();// 线程退出的时候
	}

	public static void filter(String _libraryName) {
		GraphFilterOfStringNode.reset(_libraryName);

		// 分线程读取文件 最后交由一个线程完成子图挖掘
		GraphFilterOfStringNode filters[] = new GraphFilterOfStringNode[THREAD_LIMIT];
		for (int i = 0; i < THREAD_LIMIT; i++) {
			filters[i] = new GraphFilterOfStringNode();

			Thread thread = new Thread(filters[i], "graph-filter" + i);
			thread.start();
		}

		while (true) {
			if (liveThreadCount <= 0) {
				long t_mid = System.currentTimeMillis();
				logger.info("[" + ((t_mid - time) / (float) 1000) + "s] " + validGraphCount
						+ " valid graphs have been filtered and outputed.");

				String filePath = ObjectIO.FREQUENT_SUBGRAPH_STR_LIBRARY + File.separator
						+ "[FrequencyMap]" + libraryName + ObjectIO.DAT_FILE_EXTENSION;
				ObjectIO.writeObject(graphUUIDToFrequencyMap, filePath);// 输出uuid-频数map到文件

//				String filePath2 = ObjectIO.FREQUENT_SUBGRAPH_STR_LIBRARY + File.separator
//						+ "[FrequencyDistribution]" + libraryName + ObjectIO.DAT_FILE_EXTENSION;
//				ObjectIO.writeObject(frequencyDistributionMap, filePath2);// 输出频数分布map到文件
				logger.error(frequencyDistributionMap);

				long t_end = System.currentTimeMillis();
				logger.info(
						"Output \"Frequency\" relevant maps to file: " + (t_end - t_mid) + "ms");

				break;
			}

			try {
				Thread.sleep(TIME_UNIT);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		GraphFilterOfStringNode.libraryName = libraryName;
	}

	public static HashMap<String, Integer> getGraphUUIDToFrequencyMap() {
		return graphUUIDToFrequencyMap;
	}

	public static void setGraphUUIDToFrequencyMap(
			HashMap<String, Integer> graphUUIDToFrequencyMap) {
		GraphFilterOfStringNode.graphUUIDToFrequencyMap = graphUUIDToFrequencyMap;
	}

	public static HashMap<Integer, Integer> getFrequencyDistributionMap() {
		return frequencyDistributionMap;
	}

	public static void setFrequencyDistributionMap(
			HashMap<Integer, Integer> frequencyDistributionMap) {
		GraphFilterOfStringNode.frequencyDistributionMap = frequencyDistributionMap;
	}

}
