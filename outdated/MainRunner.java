package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.SODataParser;
import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.outdated.QuestionDao;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.entity.rawdata.Question;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.nlp.TrunkExtractor;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class MainRunner implements Runnable {
	public static final Logger logger = Logger.getLogger(MainRunner.class);

	public static int THREAD_NUMBER = 8;
	private static List<Question> rawQuestionList;

	private static long index = 0;
	private static long time_start = System.currentTimeMillis();
	private static long total_count = 0;
	private int thread_count; //本线程处理的数量

	public MainRunner() {
		super();
		thread_count = 0;
	}

	// 多线程处理时，获取下一个应处理的问题
	public static synchronized long nextIndex() {
		long tmp = index;
		index++;
		return tmp;
	}

	@Override
	public void run() {
		long nextIndex = nextIndex();
		while (nextIndex < rawQuestionList.size()) {
			// long t_start = System.currentTimeMillis();
			long t0 = System.currentTimeMillis();

			Question rawQuestion = rawQuestionList.get((int) nextIndex);

			String projectName = APILibrary.judgeProjectByTags(rawQuestion.getTags());

			if (!projectName.equals(APILibrary.POI))
				continue;

			String filePath = projectName + File.separator + "[thread]" + rawQuestion.getId()
					+ ObjectIO.DAT_FILE_EXTENSION;
					// System.out.println(filePath);

			// File dataobjdir = new File(Config.getDataIODir());
			// String[] fileNames = dataobjdir.list();
			// if (Arrays.asList(fileNames).contains("[thread]" +
			// rawQuestion.getId())) {
			// long t1 = System.currentTimeMillis();
			// // System.out.println(rawQuestion.getId() + " " + (t1 - t0) +
			// // "ms");
			// continue;
			// }

			long t1 = System.currentTimeMillis();

			ThreadInfo thread = SODataParser.parseThread(rawQuestion);
			ObjectIO.writeObject(thread, ObjectIO.RAW_SODATA + File.separator + filePath);

			long t2 = System.currentTimeMillis();

			ThreadContentParser.parseHTMLContentInPostsOfThread(thread);
			long t3 = System.currentTimeMillis();

			SentenceParser.separateParagraphToSentencesInThread(thread);
			long t4 = System.currentTimeMillis();

			List<SentenceInfo> sentences = thread.getSentences();
			for (SentenceInfo sentenceInfo : sentences) {
				SentenceParser.parseGrammaticalTree(sentenceInfo);
			}
			long t41 = System.currentTimeMillis();

			ObjectIO.writeObject(thread, ObjectIO.AFTER_TREE_PARSE + File.separator + filePath);

			// System.out.println("=== [Thread] " + thread.getTitle() +
			// " ===");

			for (SentenceInfo sentenceInfo : sentences) {
				// System.out.println("=== [Sentence] ===");
				// System.out.println(sentenceInfo.getContent());

				// 提取原始的短语（vp或np）
				PhraseExtractor.extractVerbPhrases(sentenceInfo);
				PhraseExtractor.extractNounPhrases(sentenceInfo);
				boolean goodSentence = false;

				for (PhraseInfo phrase : sentenceInfo.getPhrases()) {
					// 对每个短语进行过滤，添加proof（evidence）
					PhraseFilter.filter(phrase, sentenceInfo);

					// 通过过滤，总得分超过阈值，是候选短语
					if (phrase.getProofTotalScore() > Proof.MID) {
						// phrase.getTree().pennPrint();

						// 提取短语主干
						new TrunkExtractor(phrase).extract();
						// System.out.println(phrase.getProofString());

						goodSentence = true;
					}
				}
				if (goodSentence) {
					// System.out.println("===========sentence
					// start===============");
					// System.out.println(sentenceInfo.getContent());
					// System.out.println("==========sentence
					// finish===============");
				}
			}
			long t5 = System.currentTimeMillis();
			ObjectIO.writeObject(thread, ObjectIO.AFTER_EXTRACTION + File.separator + filePath);

			long t_end = System.currentTimeMillis();

			thread_count++;
			total_count++;

			long avrg_time = (t_end - time_start) / total_count;
			logger.info("[AVGT: " + avrg_time + "ms] [" + total_count + "] [" + Thread.currentThread().getName() + ":"
					+ thread_count + "] [id:" + thread.getQuestion().getId() + "] [time: " + (t_end - t0) + "ms] "
					+ thread.getTitle());
			logger.info("[parseThread: " + (t2 - t1) + "ms] [parseHtml: " + (t3 - t2) + "ms] [parseParagraph: "
					+ (t4 - t3) + "ms] [extractPhrases: " + (t5 - t4) + "ms]");
			// logger.info("[parseThread: " + (t2 - t1) + "ms]");

			nextIndex = nextIndex();
		}
	}

	public static void main(String[] args) {
		Connection connection = DBConnPool.getConnection();
		try {
			QuestionDao questionDao = new QuestionDao(connection);// 获取数据库中的所有问题，以此为入手点创建thread
			rawQuestionList = questionDao.getAllQuestions();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnPool.closeConnection(connection);
		}

		if (rawQuestionList == null)
			return;

		for (int i = 0; i < THREAD_NUMBER; i++) {
			MainRunner mainRunner = new MainRunner();
			Thread thread = new Thread(mainRunner, "Dragon-" + i);
			thread.start();
		}

	}

	public long getIndex() {
		return index;
	}

	public synchronized void setIndex(long index) {
		MainRunner.index = index;
	}

	public static List<Question> getRawQuestionList() {
		return rawQuestionList;
	}

	public static void setRawQuestionList(List<Question> rawQuestionList) {
		MainRunner.rawQuestionList = rawQuestionList;
	}

	public static long getTime_start() {
		return time_start;
	}

	public static void setTime_start(long time_start) {
		MainRunner.time_start = time_start;
	}

	public int getThread_count() {
		return thread_count;
	}

}
