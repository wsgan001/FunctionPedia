package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.main.APILibrary;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class ContentRunner_Library implements Runnable {
	public static final Logger	logger			= Logger.getLogger(ContentRunner_Library.class);
	public static int			THREAD_LIMIT	= 1024;

	private static File[]	contentFiles	= null;
	private static int		readIndex		= 0;
	private static long		t_init			= System.currentTimeMillis();

	private boolean	readFromPool	= false;// 从content pool读取时，要求此为真
	private File	readDir			= null;	// 从分lib项目目录读取时，要求此项非空
	private int		count			= 0;

	public ContentRunner_Library() {
		super();
	}

	public ContentRunner_Library(boolean readFromPool) {
		super();
		this.readFromPool = readFromPool;
	}

	public ContentRunner_Library(File contentDir) {
		super();
		this.readDir = contentDir;
	}

	@Override
	public void run() {
		long t0 = System.currentTimeMillis();
		if (readDir != null) {
			// 从文件里读content
			String libName = APILibrary.judgeProjectByTags(readDir.getName());
			List<ContentInfo> contents = ObjectIO.readContentsFromDir(readDir);
			if (contents != null) {
				contents.forEach(content -> {
					content.setLibraryName(libName);
					APILibrary.addContent(content);
				});
			}
			long t1 = System.currentTimeMillis();
			logger.info("[" + Thread.currentThread().getName() + ": " + (t1 - t0) + "ms]");
		}
		else {
			// 处理contentPool里面的content
			ContentInfo content;
			while ((content = APILibrary.getNextContent()) != null) {
				try {
					long t1 = System.currentTimeMillis();
					ContentParser.parseContent(content);
					ObjectIO.writeObject(content, ObjectIO.PARSED_CONTENTS + File.separator
							+ content.getUuid() + ObjectIO.DAT_FILE_EXTENSION);
					count++;
					readIndex++;
					long t2 = System.currentTimeMillis();
					float avrgTime = ((int) (t2 - t0) / (float) readIndex);
					logger.info("[AVGT:" + avrgTime + "ms][Current:" + (t2 - t1) + "ms][count:"
							+ readIndex + "][" + Thread.currentThread().getName() + ": " + count
							+ "]");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			long t3 = System.currentTimeMillis();
			float avrgTime = count == 0 ? 0 : ((int) (t3 - t0) / (float) readIndex);
			logger.info("[" + Thread.currentThread().getName() + "][parsed: " + count
					+ " threads][AVGT:" + avrgTime + "ms]");
		}

	}

	public static void main(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENTPOOL);
		File[] contentFiles = dataObjDirectory.listFiles();
		ContentRunner_Library.setContentFiles(contentFiles);

		for (int i = 0; i < THREAD_LIMIT; i++) {
			ContentRunner_Library contentRunner = new ContentRunner_Library(true);// 从pool目录解析已有的content
			Thread thread = new Thread(contentRunner, "ContentRunnerFromPool" + i);
			thread.start();
		}
	}

	@Deprecated
	public static void main_old(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENTPOOL_LIBRARY);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				ContentRunner_Library contentRunner = new ContentRunner_Library(file); // 读content进来
				Thread thread = new Thread(contentRunner, "content-" + file.getName());
				thread.start();
			}
		}
		for (int i = 0; i < THREAD_LIMIT; i++) {
			ContentRunner_Library contentRunner = new ContentRunner_Library();// 解析已有的content
			Thread thread = new Thread(contentRunner, "ParseContent-" + i);
			thread.start();
		}
	}

	public static File[] getContentFiles() {
		return contentFiles;
	}

	public static void setContentFiles(File[] files) {
		contentFiles = files;
	}

	// 多线程处理时，获取下一个应处理的帖子
	public static File getNextContentFile() {
		File nextContentFile = null;
		try {
			long t_start = System.currentTimeMillis();
			int waitStartIndex = readIndex;
			while (readIndex > contentFiles.length - 1) {
				Thread.sleep(100); // 如果待处理池空了，先不退出，等100ms再看。
				if (readIndex == waitStartIndex && (System.currentTimeMillis() - t_start) > 10000)
					return null; // 如果长时间pool没有更新而且等待时间超过10s，则退出线程。
			}

			nextContentFile = contentFiles[readIndex];
			readIndex++;

		}
		catch (Exception e) {
			if (!(e instanceof IndexOutOfBoundsException))
				e.printStackTrace();
		}
		return nextContentFile;
	}

	public boolean isReadFromPool() {
		return readFromPool;
	}

	public void setReadFromPool(boolean readFromPool) {
		this.readFromPool = readFromPool;
	}
}
