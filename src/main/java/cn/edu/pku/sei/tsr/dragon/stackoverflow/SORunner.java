package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.outdated.Question;
import cn.edu.pku.sei.tsr.dragon.outdated.QuestionDao;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

/**
 * Parse SO raw data (in database) to OldThreadInfo and attached objects. Get
 * ContentInfo objects from each so thread. Output threadInfos to
 * directory(thread_lib) and contentInfos to directory(contentpool_lib), each
 * library owns a sub-directory.
 * 
 * @author ZHUZixiao
 *
 */
public class SORunner implements Runnable {
	public static final Logger		logger			= Logger.getLogger(SORunner.class);

	public static int				THREAD_LIMIT	= 16;								// 线程太多带不动
	private static List<Question>	rawQuestionList;

	private static long				index			= 0;
	private static long				time_start		= System.currentTimeMillis();
	private static long				total_count		= 0;
	private int						thread_count;

	public SORunner() {
		super();
		thread_count = 0;
	}

	// 多线程处理时，获取下一个应处理的问题
	private static synchronized long nextIndex() {
		long tmp = index;
		index++;
		return tmp;
	}

	@Override
	public void run() {
		long nextIndex = nextIndex();
		while (nextIndex < rawQuestionList.size()) {
			long t_start = System.currentTimeMillis();

			Question rawQuestion = rawQuestionList.get((int) nextIndex);
			// generate threadinfo and belonging objects, from raw so objects
			OldThreadInfo thread = SODataParser.parseThread(rawQuestion);

			// write parsed threads to files
			String soThreadfilePath = ObjectIO.SOTHREAD_LIBRARY + File.separator
					+ thread.getLibraryName() + File.separator + thread.getUuid()
					+ ObjectIO.DAT_FILE_EXTENSION;
			ObjectIO.writeObject(thread, soThreadfilePath);

			long t_mid = System.currentTimeMillis();

			List<ContentInfo> threadContents = new ArrayList<>();
			// extract all contents from threads
			threadContents.add(thread.getTitle());
			threadContents.add(thread.getQuestion().getContent());
			thread.getQuestion().getComments()
					.forEach(comment -> threadContents.add(comment.getContent()));
			thread.getAnswers().forEach(answer -> {
				threadContents.add(answer.getContent());
				answer.getComments().forEach(comment -> threadContents.add(comment.getContent()));
			});

			// write contentInfos to files
			threadContents.forEach(content -> {
				content.setLibraryName(thread.getLibraryName());

				content.setParentUuid(thread.getUuid());// 除了title都没有设置parentUUID

				// parent信息，只设uuid，20150814 zhuzx
				content.replaceParentByUuid();

				String contentLibFilePath = ObjectIO.CONTENTPOOL_LIBRARY + File.separator
						+ thread.getLibraryName() + File.separator + content.getUuid()
						+ ObjectIO.DAT_FILE_EXTENSION;
				ObjectIO.writeObject(content, contentLibFilePath);
			});

			long t_end = System.currentTimeMillis();

			thread_count++;
			total_count++;

			long avrg_time = (t_end - time_start) / total_count;
			logger.info("[AVGT: " + avrg_time + "ms][" + total_count + "]["
					+ Thread.currentThread().getName() + ":" + thread_count + "][id:"
					+ thread.getQuestion().getId() + "][parseSO:" + (t_mid - t_start)
					+ "ms][contentPool:" + (t_end - t_mid) + "ms]" + thread.getTitle());

			nextIndex = nextIndex();
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

		for (int i = 0; i < THREAD_LIMIT; i++) {
			SORunner soRunner = new SORunner();
			Thread thread = new Thread(soRunner, "Dragon-" + i);
			thread.start();
		}
	}

	public long getIndex() {
		return index;
	}

	public synchronized void setIndex(long index) {
		SORunner.index = index;
	}

	public static List<Question> getRawQuestionList() {
		return rawQuestionList;
	}

	public static void setRawQuestionList(List<Question> rawQuestionList) {
		SORunner.rawQuestionList = rawQuestionList;
	}

	public static long getTime_start() {
		return time_start;
	}

	public static void setTime_start(long time_start) {
		SORunner.time_start = time_start;
	}

	public int getThread_count() {
		return thread_count;
	}
}
