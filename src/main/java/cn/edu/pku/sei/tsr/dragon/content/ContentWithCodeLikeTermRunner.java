
package cn.edu.pku.sei.tsr.dragon.content;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.codeparser.code.CodeParser;
import cn.edu.pku.sei.tsr.dragon.codeparser.code.entity.JavaProjectInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

/**
 * 从contentpool_library读入，汇总到一个pool，然后批量建线程。 Parse every content, generate
 * paragraphs->sentences->phrases, and generate VBStructureInfo for valuable phrases.
 * DocumentParser.parseContent(ContentInfo) is the kernel method.
 * 
 * @author ZHUZixiao
 *
 */
public class ContentWithCodeLikeTermRunner implements Runnable {
	public static final Logger	logger			= Logger.getLogger(ContentWithCodeLikeTermRunner.class);
	public static int			THREAD_LIMIT	= 64; //數量太多也沒用，cpu早就滿額了

	private static List<String>	existedFileNames	= new ArrayList<>();
	private static List<File>	contentFiles		= new ArrayList<>();
	private static int			readIndex			= 0;
	private static int			globalFinishCount	= 0;
	private static int			processedCount		= 0;
	private static long			t_init				= System.currentTimeMillis();
	public static JavaProjectInfo project;
	static{
		project = CodeParser.parse("lucene", "D:\\Codes\\lucene-5.2.1");
	}
	private int localCount = 0;

	private static boolean flag = true;

	public ContentWithCodeLikeTermRunner() {
		super();
	}

	@Override
	public void run() {
		// 从content池的文件目录中读出内容
		File nextContentFile;
		CodeTermsParser codeParser = new CodeTermsParser(this.project); 
		while ((nextContentFile = getNextContentFile()) != null) {
			long t_start = System.currentTimeMillis();
			if (existedFileNames.contains(nextContentFile.getName())) {
				// 不处理已经处理过的
				// logger.error("existed file:" + nextContentFile.getPath());
			}
			else {
				if (flag) {// flag表示是否还在遍历那些以处理过的文件，正式开工后起表
					startClock();
					flag = false;
				}
				Object obj = ObjectIO.readObject(nextContentFile);
				if (obj != null && obj instanceof ContentInfo) {
					ContentInfo content = (ContentInfo) obj;

					ContentParser.parseContent(content);// 目前添加了结构
					codeParser.parseRelativeCodeTerms(content);
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
			float avrgTime = (processedCount == 0) ? 0 : ((int) (t_mid - t_init) / (float) processedCount);
			logger.info("[AVGT:" + avrgTime + "ms][Current:" + (t_mid - t_start) + "ms][total:"
					+ globalFinishCount + "][Processed:"+processedCount+"][" + Thread.currentThread().getName() + ": "
					+ localCount + "]");
		}
	}

	public static void startClock() {
		t_init = System.currentTimeMillis();
	}

	public static void main(String[] args) {
		File existedFileDir = ObjectIO.getDataObjDirectory("tempLuceneContentStructLibrary");
		if (existedFileDir.exists() && existedFileDir.listFiles() != null) {
			File[] existedSubDirs = existedFileDir.listFiles();
			for (File file : existedSubDirs) {
				if (file.isDirectory()) {
					File[] existedLibFiles = file.listFiles();
					for (File existedFile : existedLibFiles) {
						existedFileNames.add(existedFile.getName());
					}
				}
			}
		}

		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENTPOOL_FROMCOMMENT);
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

		ContentWithCodeLikeTermRunner.startClock();

		for (int i = 0; i < THREAD_LIMIT; i++) {
			ContentWithCodeLikeTermRunner contentRunner = new ContentWithCodeLikeTermRunner();// 从pool目录解析已有的content
			Thread thread = new Thread(contentRunner, "content-" + i);
			thread.start();
		}
	}

	public static List<File> getContentFiles() {
		return contentFiles;
	}

	public static void setContentFiles(List<File> contentFiles) {
		ContentWithCodeLikeTermRunner.contentFiles = contentFiles;
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
