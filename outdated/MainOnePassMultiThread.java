package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
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
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import edu.stanford.nlp.util.CollectionFactory;

public class MainOnePassMultiThread implements Runnable {
	public static final Logger		logger	= Logger.getLogger(MainOnePassMultiThread.class);

	private static List<Question>	rawQuestionList;
	private static long				index	= 0;

	private static long				t_start;
	private int						number;
	private int						startIndex;
	private int						endIndex;
	private static int				count	= 0;
	private int						t_count;

	public MainOnePassMultiThread(int start, int end, int threadNo) {
		startIndex = start;
		endIndex = end;
		number = threadNo;
		t_count = 0;
	}

	@Override
	public void run() {
		// long t_start = System.currentTimeMillis();
		for (int i = startIndex; i < endIndex; i++) {
			long t0 = System.currentTimeMillis();

			Question rawQuestion = rawQuestionList.get(i);
			if (rawQuestion.getTags().contains("swing"))
				continue;
			File dataobjdir = new File(Config.getDataObjDir());
			String[] fileNames = dataobjdir.list();
			if (Arrays.asList(fileNames).contains("[thread]" + rawQuestion.getId())) {
				long t1 = System.currentTimeMillis();
				// System.out.println(rawQuestion.getId() + "  " + (t1 - t0) +
				// "ms");
				continue;
			}
			long t1 = System.currentTimeMillis();

			ThreadInfo thread = SODataParser.parseThread(rawQuestion);
			long t2 = System.currentTimeMillis();

			ThreadContentParser.parseHTMLContentInPostsOfThread(thread);
			long t3 = System.currentTimeMillis();

			SentenceParser.separateParagraphToSentencesInThread(thread);
			long t4 = System.currentTimeMillis();

			// System.out.println("=== [Thread] " + thread.getTitle() + " ===");

			List<SentenceInfo> sentences = thread.getSentences();
			for (SentenceInfo sentenceInfo : sentences) {
				// System.out.println("=== [Sentence] ===");
				// System.out.println(sentenceInfo.getContent());
				
				SentenceParser.parseGrammaticalTree(sentenceInfo);

				PhraseExtractor.extractVerbPhrases(sentenceInfo);
				PhraseExtractor.extractNounPhrases(sentenceInfo);

				for (PhraseInfo phraseInfo : sentenceInfo.getPhrases()) {
					PhraseFilter.filter(phraseInfo, sentenceInfo);
					// if (phraseInfo.getProofWeights() > Proof.MID) {
					// System.out
					// .println("[Phrase] " + phraseInfo.getPhrase());
					// System.out.println(phraseInfo.printProofToString());
					// phraseInfo.getTree().pennPrint();
					// }
				}
			}
			long t5 = System.currentTimeMillis();
			ObjectIO.writeObject(thread, "[thread]" + thread.getId());
			long t6 = System.currentTimeMillis();

			long t_end = System.currentTimeMillis();
			synchronized (this) {
				count++;
			}
			t_count++;

			long avrg_time = (t_end - t_start) / count;
			logger.info("[AVGT: " + avrg_time + "ms] " + count + " [#" + number + "-" + t_count + "] [i=" + i
					+ "] [id:" + thread.getQuestion().getId() + "] [time: " + (t_end - t0) + "ms] " + thread.getTitle());
			logger.info("[parseThread: " + (t2 - t1) + "ms] [parseHtml: " + (t3 - t2) + "ms] [parseParagraph: "
					+ (t4 - t3) + "ms] [extractPhrases: " + (t5 - t4) + "ms] [outputOBJ: " + (t6 - t5) + "ms]");

		}
	}
	public static void main(String[] args) {
		Connection connection = DBConnPool.getConnection();
		try {
			QuestionDao questionDao = new QuestionDao(connection);// 获取数据库中的所有问题，以此为入手点创建thread
			rawQuestionList = questionDao.getAllQuestions();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(connection);
		}

		if (rawQuestionList == null)
			return;

		// new Thread(new MainOnePassMultiThread(0, 20000, 1)).start();
		// new Thread(new MainOnePassMultiThread(20000, rawQuestionList.size(),
		// 2)).start();

		int step = 40000;
		t_start = System.currentTimeMillis();
		for (int i = 0; i < rawQuestionList.size(); i += step) {
			Thread thread;
			if (i + step <= rawQuestionList.size())
				thread = new Thread(new MainOnePassMultiThread(i, i + step, i / step + 1));
			else
				thread = new Thread(new MainOnePassMultiThread(i, rawQuestionList.size(), i / step + 1));
			thread.start();
		}

	}

	public synchronized long nextIndex() {
		long tmp = index;
		index++;
		return tmp;
	}

	public long getIndex() {
		return index;
	}

	public synchronized void setIndex(long index) {
		MainOnePassMultiThread.index = index;
	}

}
