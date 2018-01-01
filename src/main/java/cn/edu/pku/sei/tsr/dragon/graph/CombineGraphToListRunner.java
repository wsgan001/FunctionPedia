package cn.edu.pku.sei.tsr.dragon.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

/**
 * 把按文件输出的Graph，按照项目Library全部合并到一个List，把这个list输出到文件
 * 
 * @author ZHUZixiao
 *
 */
public class CombineGraphToListRunner implements Runnable {
	public static final Logger logger = Logger.getLogger(CombineGraphToListRunner.class);

	public static int	THREAD_LIMIT	= 4000;	// 多线程读取文件有效提高读取速度，服务器可以到1024，台机不超过512
	public static int	TIME_UNIT		= 1000;	// main函数询问是否挖掘完成的时间间隔

	private static String			libraryName;
	private static List<GraphInfo>	dataList		= new ArrayList<>();
	private static List<File>		dataFiles		= new ArrayList<>();
	private static int				readIndex		= 0;
	private static int				liveThreadCount	= THREAD_LIMIT;					// 最后一个退出的线程作为标志
	private static long				time			= System.currentTimeMillis();

	public static void resetRunner(String _libraryName) {
		dataFiles = new ArrayList<>();
		dataList = new ArrayList<>();
		liveThreadCount = THREAD_LIMIT;
		readIndex = 0;
		time = System.currentTimeMillis();

		libraryName = _libraryName;
	}

	public static synchronized void addGraphToList(GraphInfo graphToAdd) {
		dataList.add(graphToAdd);
	}
	
	public static synchronized void stopThread() {
		liveThreadCount--;
	}

	@Override
	public void run() {
		File file;
		while ((file = getNextFile()) != null) {
			Object obj = ObjectIO.readObject(file);
			if (obj != null && obj instanceof GraphInfo) {
				GraphInfo graphInfo = (GraphInfo) obj;
				addGraphToList(graphInfo);
				if (dataList.size() % 1000 == 0) {
					logger.info("[Graph added: " + dataList.size() + "][Time:"
							+ (System.currentTimeMillis() - time) + "ms]");
					time = System.currentTimeMillis();
				}
			}
		}

		stopThread();// 线程退出的时候
	}

	// 多线程处理时，获取下一个应处理的帖子
	private static synchronized File getNextFile() {
		if (dataFiles == null)
			return null;
		File nextFile = null;
		try {
			if (readIndex >= dataFiles.size())
				return null;
			nextFile = dataFiles.get(readIndex);
			readIndex++;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return nextFile;
	}

	public static void main(String[] args) throws InterruptedException {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.GRAPH_LIBRARY);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File libDir : subdirs) {
			if (libDir.isDirectory()) {
				resetRunner(libDir.getName());

				logger.info("[" + libraryName + "]Start to load graph files...");
				File[] libFiles = libDir.listFiles();
				for (File file : libFiles) {
					dataFiles.add(file);
				}

				logger.info("[" + libraryName + "]Read in graph files and add to graph list...");
				CombineGraphToListRunner runners[] = new CombineGraphToListRunner[THREAD_LIMIT];
				for (int i = 0; i < THREAD_LIMIT; i++) {
					runners[i] = new CombineGraphToListRunner();
					Thread thread = new Thread(runners[i], "combinator" + i);
					thread.start();
				}

				while (true) {
					if (liveThreadCount <= 0) {
						logger.info("[" + libraryName + "]Output data list to file...");
						ObjectIO.writeObject(dataList, ObjectIO.GRAPH_LIBRARY + File.separator
								+ libDir.getName() + ObjectIO.DAT_FILE_EXTENSION);
						logger.info("[" + libraryName + "][Data:" + dataList.size() + "][Files:"
								+ libFiles.length + "]Data list has been outputed to file...");
						break;
					}
					Thread.sleep(TIME_UNIT);
				}
			}
		}

	}

	public static String getLibraryName() {
		return libraryName;
	}

	public static void setLibraryName(String libraryName) {
		CombineGraphToListRunner.libraryName = libraryName;
	}
}
