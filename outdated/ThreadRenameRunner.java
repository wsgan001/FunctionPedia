package cn.edu.pku.sei.tsr.dragon.main;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class ThreadRenameRunner implements Runnable {
	public static final Logger	logger			= Logger.getLogger(ThreadRenameRunner.class);
	public static int			THREAD_LIMIT	= 1024;

	private static int	readIndex	= 0;
	private static long	t_init		= System.currentTimeMillis();

	private File	readDir	= null;	// 从分lib项目目录读取时，要求此项非空
	private int		count	= 0;

	public ThreadRenameRunner(File contentDir) {
		super();
		this.readDir = contentDir;
	}

	@Override
	public void run() {
		long t0 = System.currentTimeMillis();
		if (readDir != null) {
			// 从文件里读content
			for (File file : readDir.listFiles()) {
				long t_start = System.currentTimeMillis();
				Object obj = ObjectIO.readObject(file);
				if (obj != null && obj instanceof ThreadInfo) {
					ThreadInfo thread = (ThreadInfo) obj;
					String soThreadfilePath = ObjectIO.RAW_SODATA_UUID + File.separator
							+ thread.getLibraryName() + File.separator + thread.getUuid()
							+ ObjectIO.DAT_FILE_EXTENSION;

					ObjectIO.writeObject(thread, soThreadfilePath);
				}
				count++;
			}

			long t1 = System.currentTimeMillis();
			logger.info("[" + Thread.currentThread().getName() + ": " + (t1 - t0) + "ms]");
		}
		long t3 = System.currentTimeMillis();
		float avrgTime = count == 0 ? 0 : ((int) (t3 - t0) / (float) count);
		logger.info("[" + Thread.currentThread().getName() + "][parsed: " + count
				+ " threads][AVGT:" + avrgTime + "ms]");
	}

	public static void main(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.RAW_SODATA);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				ThreadRenameRunner contentRunner = new ThreadRenameRunner(file);
				Thread thread = new Thread(contentRunner, "Rename thread-" + file.getName());
				thread.start();
			}
		}
	}

}
