package cn.edu.pku.sei.tsr.dragon.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.VerbalPhraseInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.graphofstring.GraphBuilderOfStringNode;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

/**
 * 将已经解析过的content文件，提取出Structure结构，生成自定义的图，然后把短语、结构和图分别输出到文件
 * 
 * @author ZHUZixiao
 *
 */
public class GraphRunner implements Runnable {
	public static final Logger logger = Logger.getLogger(GraphRunner.class);

	public static int THREAD_LIMIT = 1536;

	private static List<String>	existedFileNames	= new ArrayList<>();
	private static List<File>	contentFiles		= new ArrayList<>();
	private static int			readIndex			= 0;
	private static int			globalFinishCount	= 0;
	private static int			processedCount		= 0;
	private static int			graphCount			= 0;
	private static long			t_init				= System.currentTimeMillis();

	private int localCount = 0;

	private static boolean flag = true;

	public GraphRunner() {
		super();
	}

	@Override
	public void run() {
		// 从content池的文件目录中读出内容
		File nextContentFile;
		while ((nextContentFile = getNextFile()) != null) {
			long t_start = System.currentTimeMillis();
			if (existedFileNames.contains(nextContentFile.getName())) {
				// logger.error("existed file:" + nextContentFile.getPath());
			}
			else {
				if (flag) {
					startClock();
					flag = false;
				}

				Object obj = ObjectIO.readObject(nextContentFile);
				if (obj != null && obj instanceof ContentInfo) {
					ContentInfo content = (ContentInfo) obj;

					try {
						// 很可能在某一次操作中，list并不包含子元素，或者出现nullPointer
						for (ParagraphInfo para : content.getParagraphList()) {
							for (SentenceInfo sentence : para.getSentences()) {
								for (PhraseInfo phrase : sentence.getPhrases()) {
									// 限制structure的数量，按照得分进行过滤, 20150819
									if (phrase.getProofTotalScore() < Proof.MID)
										continue;

									if (phrase instanceof VerbalPhraseInfo) {
										VerbalPhraseInfo verbalPhrase = (VerbalPhraseInfo) phrase;
										VerbalPhraseStructureInfo vpstructure = verbalPhrase
												.getStructure();
										if (vpstructure != null) {
											GraphInfo graphInfo = GraphBuilderOfStringNode
													.buildGraphInfoFromStructure(vpstructure);

											// 对图做一些过滤，包括无效字符、停用词等
											if (!GraphUtils.isValidGraph(graphInfo))
												continue;

											String structureFileName = File.separator
													+ content.getLibraryName() + File.separator
													+ vpstructure.getUuid()
													+ ObjectIO.DAT_FILE_EXTENSION;
											// phrase(唯一包含)vpstructure(唯一对应)graph
											// phrase/vpstructure/graph均采用vpstructure的uuid作为自己的文件名，方便查找
											ObjectIO.writeObject(verbalPhrase,
													ObjectIO.PHRASE_LIBRARY + structureFileName);
											ObjectIO.writeObject(vpstructure,
													ObjectIO.STRUCTURE_LIBRARY + structureFileName);
											ObjectIO.writeObject(graphInfo,
													ObjectIO.GRAPH_LIBRARY + structureFileName);
											// if (phrase.getProofTotalScore() >= Proof.MID) {
											// logger.warn("\t" + verbalPhrase);
											// logger.warn("\t" + vpstructure);
											// logger.warn("\t" + graphInfo);
											// }
											graphCount++;
										}
									}
								}
							}
						}
					}
					catch (NullPointerException e) {
						// 某些自然语言文段结构不包含子结构所致，正常的处理逻辑
						// System.err.println("Caught NullPointerException!");
					}

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
			float avrgTime = ((int) (t_mid - t_init) / (float) processedCount);
			logger.info("[AVGT:" + avrgTime + "ms][Graph:" + graphCount + "][Finished:"
					+ globalFinishCount + "][Current:" + (t_mid - t_start) + "ms]["
					+ Thread.currentThread().getName() + ": " + localCount + "]");
		}
	}

	public static void startClock() {
		t_init = System.currentTimeMillis();
	}

	public static void main(String[] args) {
		logger.info("Loading structured content files...");
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.CONTENT_STRUCTURED_LIBRARY);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				File[] libContents = file.listFiles();
				for (File content : libContents) {
					contentFiles.add(content);
				}
			}
		}
		logger.info("Start to process contents and extract graphs...");

		startClock();
		for (int i = 0; i < THREAD_LIMIT; i++) {
			GraphRunner graphRunner = new GraphRunner();// 从pool目录解析已有的content
			Thread thread = new Thread(graphRunner, "graphrunner-" + i);
			thread.start();
		}
	}

	// 多线程处理时，获取下一个应处理的帖子
	private static synchronized File getNextFile() {
		if (contentFiles == null)
			return null;
		File nextFile = null;
		try {
			if (readIndex >= contentFiles.size())
				return null;
			nextFile = contentFiles.get(readIndex);
			readIndex++;
		}
		catch (Exception e) {
			// if (!(e instanceof IndexOutOfBoundsException))
			e.printStackTrace();
		}
		return nextFile;
	}

}
