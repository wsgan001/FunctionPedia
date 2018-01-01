package cn.edu.pku.sei.tsr.dragon.main;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

// 从contentpool_library读入，每个lib目录建1个线程读
public class ContentRunnerForLib implements Runnable {
	public static final Logger logger = Logger.getLogger(ContentRunnerForLib.class);

	private static int	globalFinishCount	= 0;
	private static long	t_init				= System.currentTimeMillis();

	private File	readDir		= null;	// 从分lib项目目录读取时，要求此项非空
	private int		localCount	= 0;

	public ContentRunnerForLib(File contentLibDir) {
		super();
		this.readDir = contentLibDir;
	}

	@Override
	public void run() {
		List<ContentInfo> contents = ObjectIO.readContentsFromDir(readDir);
		if (contents == null)
			return;
		for (ContentInfo content : contents) {
			long t0 = System.currentTimeMillis();
			ContentParser.parseContent(content);

			ObjectIO.writeObject(content,
					ObjectIO.PARSED_CONTENTS_LIBRARY + File.separator + content.getLibraryName()
							+ File.separator + content.getUuid() + ObjectIO.DAT_FILE_EXTENSION);
			globalFinishCount++;
			localCount++;
			long t_mid = System.currentTimeMillis();
			float avrgTime = ((int) (t_mid - t_init) / (float) globalFinishCount);
			logger.info("[AVGT:" + avrgTime + "ms][total:" + globalFinishCount + "][Current:"
					+ (t_mid - t0) + "ms][" + Thread.currentThread().getName() + ": " + localCount
					+ "]");
		}
	}

	public static void main(String[] args) {
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENTPOOL_LIBRARY);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				ContentRunnerForLib contentRunner = new ContentRunnerForLib(file); // 读content进来
				Thread thread = new Thread(contentRunner, "content-" + file.getName());
				thread.start();
			}
		}
	}
}
