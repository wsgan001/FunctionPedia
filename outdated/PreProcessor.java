package cn.edu.pku.sei.tsr.dragon.outdated;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;
import cn.edu.pku.sei.tsr.dragon.outdated.QuestionDao;
import cn.edu.pku.sei.tsr.dragon.entity.rawdata.Question;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class PreProcessor {
	private static final Logger	logger	= Logger.getLogger(PreProcessor.class);
	private List<ThreadInfo>	soThreads;

	public PreProcessor() {
		soThreads = new ArrayList<ThreadInfo>();
	}

	public static void main(String[] args) {
		PreProcessor preProcessor = new PreProcessor();
		preProcessor.intepretRawDataToSOThreads();
	}

	public void intepretRawDataToSOThreads() {
		Connection connection = DBConnPool.getConnection();
		try {
			QuestionDao questionDao = new QuestionDao(connection);
			// 获取数据库中的所有问题，以此为入手点创建thread
			List<Question> rawQuestionList = questionDao.getAllQuestions();

			if (rawQuestionList != null) {

				long t0 = System.currentTimeMillis();
				int count = 0;

				for (int i = 0; i < rawQuestionList.size(); i += 200) {
					// if (new Random().nextDouble() > 0.0002)
					// continue;

					Question rawQuestion = rawQuestionList.get(i);
					long t1 = System.currentTimeMillis();

					ThreadInfo newThread = SODataParser.parseThread(rawQuestion);
					ThreadContentParser.parseHTMLContentInPostsOfThread(newThread);

					SentenceParser.separateParagraphToSentencesInThread(newThread);

					// ObjectIO.writeObject(newThread,
					// "[thread]" + newThread.getUuid());

					soThreads.add(newThread);
					long t2 = System.currentTimeMillis();
					count++;
					long avrg_time = (t2 - t0) / count;
					logger.info("[AVGT: " + avrg_time + "ms] " + count + " [" + newThread.getQuestion().getId() + "] "
							+ (t2 - t1) + "ms " + newThread.getTitle());
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(connection);
		}

	}
	public List<ThreadInfo> getSoThreads() {
		return soThreads;
	}

	public void setSoThreads(List<ThreadInfo> soThreads) {
		this.soThreads = soThreads;
	}

}
