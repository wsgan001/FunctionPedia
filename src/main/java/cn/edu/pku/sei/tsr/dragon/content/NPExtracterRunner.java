package cn.edu.pku.sei.tsr.dragon.content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class NPExtracterRunner implements Runnable {
	public static final Logger	logger			= Logger.getLogger(NPExtracterRunner.class);
	public static int			THREAD_LIMIT	= 8;										// 數量太多也沒用，cpu早就滿額了

	private static List<String>	existedFileNames	= new ArrayList<>();
	private static List<File>	contentFiles		= new ArrayList<>();
	private static int			readIndex			= 0;
	private static int			globalFinishCount	= 0;
	private static int			processedCount		= 0;
	private static long			t_init				= System.currentTimeMillis();

	private int localCount = 0;

	private static boolean flag = true;

	public NPExtracterRunner() {
		super();
	}

	@Override
	public void run() {
		// 从content池的文件目录中读出内容
		File nextContentFile;
		while ((nextContentFile = getNextContentFile()) != null) {
			long t_start = System.currentTimeMillis();

			startClock();

			Object obj = ObjectIO.readObject(nextContentFile);
			if (obj != null && obj instanceof ContentInfo) {
				ContentInfo content = (ContentInfo) obj;

				ContentParser.parseContent(content);// 目前添加了结构
				SyntaxParser.extractPhrases(content);

				String filePath = ObjectIO.CONTENT_STRUCTURED_LIBRARY + File.separator
						+ content.getLibraryName() + File.separator + content.getUuid()
						+ ObjectIO.DAT_FILE_EXTENSION;

				ObjectIO.writeObject(content, filePath);
				try {
					content.freeMe();
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
			nextContentFile = null;
			processedCount++;
		}
		globalFinishCount++;
		localCount++;
		long t_mid = System.currentTimeMillis();
		float avrgTime = (processedCount == 0) ? 0
				: ((int) (t_mid - t_init) / (float) processedCount);
		logger.info("[AVGT:" + avrgTime + "ms][Current:" + (t_mid - t_init) + "ms][total:"
				+ globalFinishCount + "][Processed:" + processedCount + "]["
				+ Thread.currentThread().getName() + ": " + localCount + "]");
	}

	public static void startClock() {
		t_init = System.currentTimeMillis();
	}

	public static void main(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENTPOOL_LIBRARY);
		logger.info("Loading content files from libDirs to pool");

		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				File[] libContents = file.listFiles();
				for (File content : libContents) {
					contentFiles.add(content);
				}
			}
		}

		NPExtracterRunner.startClock();

		for (int i = 0; i < THREAD_LIMIT; i++) {
			NPExtracterRunner npRunner = new NPExtracterRunner();// 从pool目录解析已有的content
			Thread thread = new Thread(npRunner, "npRunner-" + i);
			thread.start();
		}
	}

	public static List<File> getContentFiles() {
		return contentFiles;
	}

	public static void setContentFiles(List<File> _contentFiles) {
		contentFiles = _contentFiles;
	}

	// 多线程处理时，获取下一个应处理的帖子
	private static File getNextContentFile() {
		if (contentFiles == null)
			return null;
		File nextContentFile = null;
		try {
			if (readIndex >= contentFiles.size())
				return null;

			nextContentFile = contentFiles.get(readIndex);
			readIndex++;
		}
		catch (Exception e) {
			// if (!(e instanceof IndexOutOfBoundsException))
			e.printStackTrace();
		}
		return nextContentFile;
	}
}
