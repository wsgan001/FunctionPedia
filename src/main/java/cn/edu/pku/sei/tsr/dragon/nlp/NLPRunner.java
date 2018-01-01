
package cn.edu.pku.sei.tsr.dragon.nlp;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.document.dao.SentenceDAO;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.dao.PhraseDAO;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.NLPParser;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

/**
 * 从contentpool_library读入，汇总到一个pool，然后批量建线程。 Parse every content, generate
 * paragraphs->sentences->phrases, and generate VBStructureInfo for valuable phrases.
 * DocumentParser.parseContent(ContentInfo) is the kernel method.
 * 
 * @author ZHUZixiao
 *
 */
public class NLPRunner implements Runnable {
	public static final Logger	logger			= Logger.getLogger(NLPRunner.class);
	public static int			THREAD_LIMIT	= 16;								// CPU+1

	private static List<SentenceInfo>	sentencePool	= new ArrayList<>();
	private static int					nextIndex		= 0;
	private static int					globalCount		= 0;
	private static long					t_init			= System.currentTimeMillis();

	private int			localCount	= 0;
	private Connection	conn;

	public NLPRunner() {
		super();
	}

	@Override
	public void run() {
		conn = DBConnPool.getConnection();
		// 从sentence池的文件目录中读出内容
		SentenceInfo sentence;
		while ((sentence = getNextSentence()) != null) {
			long t_start = System.currentTimeMillis();

			NLPParser.parseSentence(sentence, conn);

			globalCount++;
			localCount++;
			long t_mid = System.currentTimeMillis();
			float avrgTimeLocal = (t_mid - t_init) / (float) localCount;
			float avrgTimeGlobal = (t_mid - t_init) / (float) globalCount;
			logger.info("[avgt_g]" + avrgTimeGlobal + "ms [remain]"
					+ (sentencePool.size() - globalCount) * avrgTimeGlobal / 1000 + "s [avgt_l]"
					+ avrgTimeLocal + "ms [current]" + (t_mid - t_start) + "ms [sentId]"
					+ sentence.getId() + " [total]" + globalCount + " [nextIndex]" + nextIndex
					+ " [" + Thread.currentThread().getName() + "] " + localCount);
		}
		DBConnPool.closeConnection(conn);
	}

	public static void startClock() {
		t_init = System.currentTimeMillis();
	}

	public static void main(String[] args) {
		Connection conn = DBConnPool.getConnection();
		logger.info("Load all sentences from database...");
		SentenceDAO sentenceDAO = new SentenceDAO(conn);
		sentencePool = sentenceDAO.getAll();
		// int cursor = 370650;
		// PhraseDAO phraseDAO = new PhraseDAO(conn);
		// phraseDAO.deleteByParentIdLargerThan(cursor);
		// sentencePool = sentenceDAO.getAllAfterId(cursor);
		logger.info("Loading completed. Sentence count:" + sentencePool.size());
		DBConnPool.closeConnection(conn);

		NLPParser.initializeConnection();

		NLPRunner.startClock();
		for (int i = 0; i < THREAD_LIMIT; i++) {
			NLPRunner nlpRunner = new NLPRunner();// 从pool目录解析已有的content
			Thread thread = new Thread(nlpRunner, "NLPRunner-" + i);
			thread.start();
		}
	}

	// get next sentence to handle, in multi-thread program
	private static SentenceInfo getNextSentence() {
		if (sentencePool == null)
			return null;
		SentenceInfo sentenceInfo = null;
		try {
			if (nextIndex >= sentencePool.size())
				return null;

			sentenceInfo = sentencePool.get(nextIndex);
			nextIndex++;
		}
		catch (Exception e) {
			// if (!(e instanceof IndexOutOfBoundsException))
			e.printStackTrace();
		}
		return sentenceInfo;
	}
}
