
package cn.edu.pku.sei.tsr.dragon.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

// 从contentpool_library读入，汇总到一个pool，然后批量建线程
public class ContentRunner implements Runnable {
	public static final Logger	logger			= Logger.getLogger(ContentRunner.class);
	public static int			THREAD_LIMIT	= 48;

	private static List<File>	contentFiles		= new ArrayList<>();
	private static int			readIndex			= 0;
	private static int			globalFinishCount	= 0;
	private static long			t_init				= System.currentTimeMillis();

	private int localCount = 0;

	public ContentRunner() {
		super();
	}

	@Override
	public void run() {
		// 从content池的文件目录中读出内容
		File nextContentFile;
		while ((nextContentFile = getNextContentFile()) != null) {
			long t_start = System.currentTimeMillis();
			Object obj = ObjectIO.readObject(nextContentFile);
			if (obj != null && obj instanceof ContentInfo) {
				ContentInfo content = (ContentInfo) obj;
				ContentParser.parseContent(content);
				ObjectIO.writeObject(content,
						ObjectIO.PARSED_CONTENTS + "_test" + File.separator
								+ content.getLibraryName() + File.separator + content.getUuid()
								+ ObjectIO.DAT_FILE_EXTENSION);
				try {
					content.freeMe();
				}
				catch (Throwable e) {
					e.printStackTrace();
				}
			}
			globalFinishCount++;
			localCount++;
			long t_mid = System.currentTimeMillis();
			float avrgTime = ((int) (t_mid - t_init) / (float) globalFinishCount);
			logger.info("[AVGT:" + avrgTime + "ms][Current:" + (t_mid - t_start) + "ms][total:"
					+ globalFinishCount + "][" + Thread.currentThread().getName() + ": "
					+ localCount + "]");
		}
	}

	public static void main(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENTPOOL_LIBRARY);

		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				File[] libContents = file.listFiles();
				for (File content : libContents) {
					contentFiles.add(content);
				}
			}
		}

		for (int i = 0; i < THREAD_LIMIT; i++) {
			ContentRunner contentRunner = new ContentRunner();// 从pool目录解析已有的content
			Thread thread = new Thread(contentRunner, "content-" + i);
			thread.start();
		}
	}

	public static List<File> getContentFiles() {
		return contentFiles;
	}

	public static void setContentFiles(List<File> contentFiles) {
		ContentRunner.contentFiles = contentFiles;
	}

	// 多线程处理时，获取下一个应处理的帖子
	public static File getNextContentFile() {
		if (contentFiles == null)
			return null;
		File nextContentFile = null;
		try {
			long t_start = System.currentTimeMillis();
			int waitStartIndex = readIndex;
			while (readIndex > contentFiles.size() - 1) {
				Thread.sleep(100); // 如果待处理池空了，先不退出，等100ms再看。
				if (readIndex == waitStartIndex && (System.currentTimeMillis() - t_start) > 10000)
					return null; // 如果长时间pool没有更新而且等待时间超过10s，则退出线程。
			}

			nextContentFile = contentFiles.get(readIndex);
			readIndex++;

		}
		catch (Exception e) {
			if (!(e instanceof IndexOutOfBoundsException))
				e.printStackTrace();
		}
		return nextContentFile;
	}
}
