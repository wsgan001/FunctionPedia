package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.graphofstring.FrequentGraphMinerOfStringNode;
import cn.edu.pku.sei.tsr.dragon.graph.graphofstring.GraphBuilderOfStringNode;
import cn.edu.pku.sei.tsr.dragon.graph.graphofstring.GraphFilterOfStringNode;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import de.parsemis.graph.Graph;

/**
 * Replace all the "FrequentStructureFilter" with "FrequentGraphMinerOfNodeInfo", and change line
 * 50. Then you can mine sub-graph in that way.
 * 
 * @author ZHUZixiao
 *
 */
@Deprecated
public class GraphMinerRunner implements Runnable {
	public static final Logger logger = Logger.getLogger(GraphMinerRunner.class);

	public static int	THREAD_LIMIT	= 8;
	public static int	TIME_UNIT		= 1000;	// main函数询问是否挖掘完成的时间间隔

	private static String		libraryName;
	private static List<File>	graphFiles		= new ArrayList<>();
	private static int			readIndex		= 0;
	private static boolean		startMiner		= false;						// 标识着是否已经读完文件，开始挖掘子图
	private static int			liveThreadCount	= THREAD_LIMIT;					// 有时图数量和文件数量不对等，那就要等到最后一个退出的线程来处理子图
	private static boolean		finishedMining	= false;						// 标识着子图挖掘是否已经完成
	private static int			validMinerIndex	= -1;							// 执行了子图挖掘的miner的编号
	private static long			time			= System.currentTimeMillis();

	private int index;

	public GraphMinerRunner(int idx) {
		this.index = idx;
	}

	public static synchronized void stopThread() {
		liveThreadCount--;
	}

	public static synchronized int getLiveThreadCount() {
		return liveThreadCount;
	}

	@Override
	public void run() {
		File file;
		while ((file = getNextFile()) != null) {
			Object obj = ObjectIO.readObject(file);
			if (obj != null && obj instanceof GraphInfo) {
				GraphInfo graphInfo = (GraphInfo) obj;

				Graph<String, Integer> graph = GraphBuilderOfStringNode.convertToParsemisGraph(graphInfo);
				if (graph != null)
					FrequentGraphMinerOfStringNode.getGraphs().add(graph);

				if (FrequentGraphMinerOfStringNode.getGraphs().size() % 1000 == 0) {
					logger.info("[Graph added: " + FrequentGraphMinerOfStringNode.getGraphs().size() + "][Time:"
							+ (System.currentTimeMillis() - time) + "ms]");
					time = System.currentTimeMillis();
				}
			}
		}
		// 当无文件可继续加载时，看看是不是所有图都解析好了，如果是，试图争夺子图挖掘的处理权
		int fileSize = graphFiles.size();
		int graphSize = FrequentGraphMinerOfStringNode.getGraphs().size();

		if (getLiveThreadCount() <= 1) {
			if (!startMiner) {// 说明还没有开始挖掘子图
				startMiner = true;// 阻止其他线程的操作
				validMinerIndex = this.index;// 把自己设为挖掘者
				logger.info("[FileSize:" + fileSize + "][GraphSize:" + graphSize
						+ "] Start to mine frequent subgraphs: Miner-" + validMinerIndex);
				long t1 = System.currentTimeMillis();

				FrequentGraphMinerOfStringNode.mineFrequentGraph();
				time = System.currentTimeMillis() - t1;

				finishedMining = true;// 挖掘完成后，发消息给main
			}
		}

		stopThread();// 线程退出的时候
	}

	// 多线程处理时，获取下一个应处理的帖子
	private static synchronized File getNextFile() {
		if (graphFiles == null)
			return null;
		File nextFile = null;
		try {
			if (readIndex >= graphFiles.size())
				return null;
			nextFile = graphFiles.get(readIndex);
			readIndex++;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return nextFile;
	}

	public static void main(String[] args) throws InterruptedException {
		String libraryName = APILibrary.NUTCH;
		logger.info("Start to read " + libraryName + " graph files...");

		FrequentGraphMinerOfStringNode.reset(libraryName);
		File libDir = ObjectIO
				.getDataObjDirectory(ObjectIO.GRAPH_LIBRARY + File.separator + libraryName);
		File[] files = libDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			graphFiles.add(files[i]);
		}

		// 分线程读取文件 最后交由一个线程完成子图挖掘
		GraphMinerRunner miners[] = new GraphMinerRunner[THREAD_LIMIT];
		for (int i = 0; i < THREAD_LIMIT; i++) {
			miners[i] = new GraphMinerRunner(i);

			Thread thread = new Thread(miners[i], "miner" + i);
			thread.start();
		}

		while (true) {
			if (GraphMinerRunner.isFinishedMining()) {
				GraphFilterOfStringNode.filter(libraryName);
				break;
			}
			Thread.sleep(TIME_UNIT);
			// System.out.println("噫！");
		}
		logger.info("Mined " + FrequentGraphMinerOfStringNode.getGraphs().size() + " graphs. "
				+ FrequentGraphMinerOfStringNode.getFrequentSubgraphs().size()
				+ " frequent subgraphs found. It costs " + (time / (float) 1000) + "s");
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static boolean isFinishedMining() {
		return finishedMining;
	}

	public static int getValidMinerIndex() {
		return validMinerIndex;
	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		GraphMinerRunner.libraryName = libraryName;
	}
}
