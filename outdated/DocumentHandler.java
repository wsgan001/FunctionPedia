package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.common.io.Files;

import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.nlpdata.TrunkInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.main.APILibrary;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.utils.nlp.Stemmer;

public class DocumentHandler {
	private static final Logger	logger			= Logger.getLogger(DocumentHandler.class);

	public static int			count			= 0;
	public static long			time			= System.currentTimeMillis();

	public static final String	SRC_DATA_PATH	= "E:/Dragon-dataobj-backup-full-0824"
												// Config.getDataObjDir()
														+ File.separator + ObjectIO.AFTER_EXTRACTION;
	public static final String	DOC_DATA_PATH	= "E:" + File.separator + Config.getDataDocDir();

	public static void main(String args[]) {
		readDocuments();
	}

	public static List<String> readDocuments() {
		String fileName = getProjectDocFileNameByLevel(APILibrary.POI, PhraseInfo.LEVEL_2);
		File file = new File(fileName);
		List<String> lines = new ArrayList<String>();
		List<String> phrases = new ArrayList<String>();

		try {
			lines = Files.readLines(file, Charset.defaultCharset());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			String phrase = line.substring(line.indexOf("]") + 1);
			phrases.add(phrase);
			System.out.println(phrase);

			String[] words = phrase.split(" ");
			for (String string : words) {
				System.out.println(string);
			}
			System.out.println("==========");
		}
		return phrases;
	}

	public static void generatePhraseDocuments() {
		long startTime = System.currentTimeMillis();
		long time = System.currentTimeMillis();
		int count = 0;

		File rootDir = new File(SRC_DATA_PATH);

		File[] projDirs = rootDir.listFiles();
		for (int i = 0; i < projDirs.length; i++) {
			// 一个项目的所有数据
			File projDir = projDirs[i];

			// datadocs/{project-name}/
			File outDir = new File(getProjectDirName(projDir.getName()));
			if (!outDir.exists())
				outDir.mkdirs();

			try {
				// datadocs/{project-name}/level_{n}/
				File level0Dir = new File(getProjectDirNameByLevel(projDir.getName(), PhraseInfo.LEVEL_0));
				File level1Dir = new File(getProjectDirNameByLevel(projDir.getName(), PhraseInfo.LEVEL_1));
				File level2Dir = new File(getProjectDirNameByLevel(projDir.getName(), PhraseInfo.LEVEL_2));
				File level3Dir = new File(getProjectDirNameByLevel(projDir.getName(), PhraseInfo.LEVEL_3));
				File level4Dir = new File(getProjectDirNameByLevel(projDir.getName(), PhraseInfo.LEVEL_4));
				File level5Dir = new File(getProjectDirNameByLevel(projDir.getName(), PhraseInfo.LEVEL_5));

				if (!level0Dir.exists())
					level0Dir.mkdirs();
				if (!level1Dir.exists())
					level1Dir.mkdirs();
				if (!level2Dir.exists())
					level2Dir.mkdirs();
				if (!level3Dir.exists())
					level3Dir.mkdirs();
				if (!level4Dir.exists())
					level4Dir.mkdirs();
				if (!level5Dir.exists())
					level5Dir.mkdirs();
				
				// datadocs/{project-name}/{project-name}_level_{n}.dat
				File level0File = new File(getProjectDocFileNameByLevel(projDir.getName(), PhraseInfo.LEVEL_0));
				File level1File = new File(getProjectDocFileNameByLevel(projDir.getName(), PhraseInfo.LEVEL_1));
				File level2File = new File(getProjectDocFileNameByLevel(projDir.getName(), PhraseInfo.LEVEL_2));
				File level3File = new File(getProjectDocFileNameByLevel(projDir.getName(), PhraseInfo.LEVEL_3));
				File level4File = new File(getProjectDocFileNameByLevel(projDir.getName(), PhraseInfo.LEVEL_4));
				File level5File = new File(getProjectDocFileNameByLevel(projDir.getName(), PhraseInfo.LEVEL_5));

				level0File.delete();
				level1File.delete();
				level2File.delete();
				level3File.delete();
				level4File.delete();
				level5File.delete();

				RandomAccessFile writer0 = new RandomAccessFile(level0File, "rw");
				RandomAccessFile writer1 = new RandomAccessFile(level1File, "rw");
				RandomAccessFile writer2 = new RandomAccessFile(level2File, "rw");
				RandomAccessFile writer3 = new RandomAccessFile(level3File, "rw");
				RandomAccessFile writer4 = new RandomAccessFile(level4File, "rw");
				RandomAccessFile writer5 = new RandomAccessFile(level5File, "rw");

				File[] objFiles = projDir.listFiles();
				for (int j = 0; j < objFiles.length; j++) {
					File objFile = objFiles[j];

					ThreadInfo thread = (ThreadInfo) ObjectIO.readObject(objFile);
					String threadFileName = thread.getId() + ObjectIO.DAT_FILE_EXTENSION;

					// datadocs/{project-name}/level_{n}/{thread-id}.dat
					File threadFile0 = new File(level0Dir, threadFileName);
					File threadFile1 = new File(level1Dir, threadFileName);
					File threadFile2 = new File(level2Dir, threadFileName);
					File threadFile3 = new File(level3Dir, threadFileName);
					File threadFile4 = new File(level4Dir, threadFileName);
					File threadFile5 = new File(level5Dir, threadFileName);

					FileWriter threadWriter0 = new FileWriter(threadFile0);
					FileWriter threadWriter1 = new FileWriter(threadFile1);
					FileWriter threadWriter2 = new FileWriter(threadFile2);
					FileWriter threadWriter3 = new FileWriter(threadFile3);
					FileWriter threadWriter4 = new FileWriter(threadFile4);
					FileWriter threadWriter5 = new FileWriter(threadFile5);

					Map<String, PhraseInfo> map = thread.getMarkedPhrasesMap();
					for (Entry<String, PhraseInfo> entry : map.entrySet()) {
						PhraseInfo phrase = entry.getValue();
						String phraseKey = "[" + entry.getKey() + "]";

						if (phrase == null)
							continue;

						if (phrase.getTrunk() != null) {
							
						}

						// System.out.println("orginal:" + phrase.getContent());

						String phraseContent = phrase.getStemmedContent();
						if (phraseContent != null) {
							writer0.writeBytes(phraseKey + phraseContent + "\n");
							threadWriter0.write(phraseKey + phraseContent + "\n");
						}

						// System.out.println("phrase content:" +
						// phraseContent);
						// System.out.println("tree:" + phrase.getTree());
						// System.out.println("stmtree:" +
						// phrase.getStemmedTree());
						// System.out.println("stmc:" +
						// phrase.getStemmedContent());

						TrunkInfo trunk = phrase.getTrunk();
						if (trunk == null)
							continue;

						PhraseInfo level1Phrase = trunk.getKernal();
						PhraseInfo level2Phrase = trunk.getTrunk();
						PhraseInfo level3Phrase = trunk.getSkeleton();

						if (level1Phrase != null) {
							String strToWrite = phraseKey + level1Phrase.getStemmedContent() + "\n";
							writer1.writeBytes(strToWrite);
							threadWriter1.write(strToWrite);
							// System.out.println("kernel:" +
							// level1Phrase.getStemmedContent());
						}
						if (level2Phrase != null) {
							String strToWrite = phraseKey + level2Phrase.getStemmedContent() + "\n";
							writer2.writeBytes(strToWrite);
							threadWriter2.write(strToWrite);
							// System.out.println("trunk:" +
							// level2Phrase.getStemmedContent());
						}
						if (level3Phrase != null) {
							String strToWrite = phraseKey + level3Phrase.getStemmedContent() + "\n";
							writer3.writeBytes(strToWrite);
							threadWriter3.write(strToWrite);
							// System.out.println("skeleton:" +
							// level3Phrase.getStemmedContent());
						}
						// System.out.println("====================================");
					}// end of phrase

					threadWriter0.close();
					threadWriter1.close();
					threadWriter2.close();
					threadWriter3.close();
					threadWriter4.close();
					threadWriter5.close();

					count++;
					long curr = System.currentTimeMillis();
					logger.info("[Thread] [" + count + "] [AVGT:" + (curr - startTime) / count + "ms] [time:"
							+ (curr - time) + "ms] " + thread.getTitle());
					time = curr;
				}// end of thread

				writer0.close();
				writer1.close();
				writer2.close();
				writer3.close();
				writer4.close();
				writer5.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {

			}
		}// end of project
	}

	public static void main2(String args[]) {
		List<ThreadInfo> threadList = new ArrayList<ThreadInfo>();

		try {
			File rootDir = new File(SRC_DATA_PATH);
			File[] subDirs = rootDir.listFiles();
			for (int i = 0; i < subDirs.length; i++) {
				File[] files = subDirs[i].listFiles();
				for (File file : files) {
					ThreadInfo t = (ThreadInfo) ObjectIO.readObject(file);
					logger.info("COUNT:" + (count++) + " AVGT:" + (System.currentTimeMillis() - time) / count + "ms");
					System.out.println("==========" + t.getTitle() + "==========");
					Map<String, PhraseInfo> map = t.getMarkedPhrasesMap();
					for (Entry<String, PhraseInfo> entry : map.entrySet()) {
						System.out.println(entry.getKey() + "  *****  ");
						try {
							PhraseInfo phrase = entry.getValue();
							System.out.println(phrase.getContent());

							TrunkInfo trunk = phrase.getTrunk();

							System.out.println(Stemmer.stem(trunk.getKernal().getContent()));
							System.out.println(Stemmer.stem(trunk.getTrunk().getContent()));
							System.out.println(Stemmer.stem(trunk.getSkeleton().getContent()));

							// Tree newt = StanfordParser.parseTree(stem);
							// phrase.getTree().pennPrint();
							// newt.pennPrint();
							// System.out.println(entry.getValue().getTrunk().getTrunk().getTree());
						}
						catch (Exception e) {

						}
					}
					System.out.println("==================");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getProjectDirName(String projectName) {
		StringBuilder str = new StringBuilder(DOC_DATA_PATH);

		switch (projectName) {
		case APILibrary.LUCENE:
		case APILibrary.SWING:
		case APILibrary.NEO4J:
		case APILibrary.POI:
			str.append(File.separator + projectName);
		}

		System.out.println(str);

		return str.toString();
	}

	public static String getProjectDirNameByLevel(String projectName, String level) {
		StringBuilder str = new StringBuilder(DOC_DATA_PATH);

		switch (projectName) {
		case APILibrary.LUCENE:
		case APILibrary.SWING:
		case APILibrary.NEO4J:
		case APILibrary.POI:
			str.append(File.separator + projectName);
		}

		switch (level) {
		case PhraseInfo.LEVEL_0:
		case PhraseInfo.LEVEL_1:
		case PhraseInfo.LEVEL_2:
		case PhraseInfo.LEVEL_3:
		case PhraseInfo.LEVEL_4:
		case PhraseInfo.LEVEL_5:
			str.append(File.separator + level);
		}

		System.out.println(str);
		return str.toString();
	}

	public static String getProjectDocFileNameByLevel(String projectName, String level) {
		StringBuilder str = new StringBuilder(DOC_DATA_PATH);

		switch (projectName) {
		case APILibrary.LUCENE:
		case APILibrary.SWING:
		case APILibrary.NEO4J:
		case APILibrary.POI:
			str.append(File.separator + projectName + File.separator + projectName);
		}

		switch (level) {
		case PhraseInfo.LEVEL_0:
		case PhraseInfo.LEVEL_1:
		case PhraseInfo.LEVEL_2:
		case PhraseInfo.LEVEL_3:
		case PhraseInfo.LEVEL_4:
		case PhraseInfo.LEVEL_5:
			str.append("_" + level + ObjectIO.DAT_FILE_EXTENSION);
		}
		System.out.println(str);

		return str.toString();
	}

}
