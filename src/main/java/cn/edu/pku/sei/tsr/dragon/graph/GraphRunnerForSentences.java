package cn.edu.pku.sei.tsr.dragon.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.VerbalPhraseInfo;
import cn.edu.pku.sei.tsr.dragon.graph.entity.GraphInfo;
import cn.edu.pku.sei.tsr.dragon.graph.graphofstring.GraphBuilderOfStringNode;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class GraphRunnerForSentences implements Runnable {
	public static final Logger	logger				= Logger
			.getLogger(GraphRunnerForSentences.class);

	public static int			THREAD_LIMIT		= 16;

	private static String		libName;

	private static List<File>	sentenceFiles		= new ArrayList<>();
	private static int			readIndex			= 0;
	private static int			globalFinishCount	= 0;
	private static int			processedCount		= 0;
	private static int			graphCount			= 0;
	private static long			t_init				= System.currentTimeMillis();

	private int					localCount			= 0;

	public GraphRunnerForSentences() {
		super();
	}

	@Override
	public void run() {
		// 从content池的文件目录中读出内容
		File nextContentFile;
		while ((nextContentFile = getNextFile()) != null) {
			long t_start = System.currentTimeMillis();

			Object obj = ObjectIO.readObject(nextContentFile);
			if (obj != null && obj instanceof SentenceInfo) {
				SentenceInfo sentence = (SentenceInfo) obj;

				for (PhraseInfo phrase : sentence.getPhrases()) {
					// 限制structure的数量，按照得分进行过滤, 20150819
					if (phrase.getProofTotalScore() < Proof.MID)
						continue;

					if (phrase instanceof VerbalPhraseInfo) {
						VerbalPhraseInfo verbalPhrase = (VerbalPhraseInfo) phrase;
						VerbalPhraseStructureInfo vpstructure = verbalPhrase.getStructure();
						if (vpstructure != null) {
							GraphInfo graphInfo = GraphBuilderOfStringNode
									.buildGraphInfoFromStructure(vpstructure);

							// 对图做一些过滤，包括无效字符、停用词等
							if (!GraphUtils.isValidGraph(graphInfo))
								continue;

							String structureFileName = File.separator + libName + File.separator
									+ vpstructure.getUuid() + ObjectIO.DAT_FILE_EXTENSION;
							// phrase(唯一包含)vpstructure(唯一对应)graph
							// phrase/vpstructure/graph均采用vpstructure的uuid作为自己的文件名，方便查找
							ObjectIO.writeObject(verbalPhrase,
									ObjectIO.PHRASE_LIBRARY + structureFileName);
							ObjectIO.writeObject(vpstructure,
									ObjectIO.STRUCTURE_LIBRARY + structureFileName);
							ObjectIO.writeObject(graphInfo,
									ObjectIO.GRAPH_LIBRARY + structureFileName);
							// if (phrase.getProofTotalScore() >= Proof.MID)
							// {
							// logger.warn("\t" + verbalPhrase);
							// logger.warn("\t" + vpstructure);
							// logger.warn("\t" + graphInfo);
							// }
							graphCount++;
						}
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
		logger.info("Loading structured sentence files...");
		File dataObjDirectory = ObjectIO.getDataObjDirectory(ObjectIO.STRUCTURED_SENTENCE_LIBRARY);
		File[] subdirs = dataObjDirectory.listFiles();
		for (File file : subdirs) {
			if (file.isDirectory()) {
				GraphRunnerForSentences.libName = file.getName();
				File[] libContents = file.listFiles();
				for (File content : libContents) {
					sentenceFiles.add(content);
				}
			}
		}
		logger.info("Start to process sentences and extract graphs...");

		startClock();
		for (int i = 0; i < THREAD_LIMIT; i++) {
			GraphRunnerForSentences runner = new GraphRunnerForSentences();// 从pool目录解析已有的content
			Thread thread = new Thread(runner, "graphrunner-" + i);
			thread.start();
		}
	}

	// 多线程处理时，获取下一个应处理的帖子
	private static synchronized File getNextFile() {
		if (sentenceFiles == null)
			return null;
		File nextFile = null;
		try {
			if (readIndex >= sentenceFiles.size())
				return null;
			nextFile = sentenceFiles.get(readIndex);
			readIndex++;
		}
		catch (Exception e) {
			// if (!(e instanceof IndexOutOfBoundsException))
			e.printStackTrace();
		}
		return nextFile;
	}

}
