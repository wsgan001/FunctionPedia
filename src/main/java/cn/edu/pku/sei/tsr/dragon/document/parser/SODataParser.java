package cn.edu.pku.sei.tsr.dragon.document.parser;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.document.dao.ContentDAO;
import cn.edu.pku.sei.tsr.dragon.document.dao.ParagraphDAO;
import cn.edu.pku.sei.tsr.dragon.document.dao.SentenceDAO;
import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.feature.entity.LibraryInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.dao.PhraseDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyThreadDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.task.dao.TaskDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class SODataParser {
	public static final Logger	logger	= Logger.getLogger(SODataParser.class);
	public static Connection	conn;

	public static void main(String[] args) {
		//extractAndParseContentsFromSO();
		
		//20170727 软报数据统计
		
		boolean connFlag = true;
		if (conn == null) {
			initializeConnection();
			connFlag = false;
		}
		
		MyThreadDAO threadDAO=new MyThreadDAO(conn);
		MyPostDAO postDAO=new MyPostDAO(conn);
		MyCommentDAO commentDAO=new MyCommentDAO(conn);
		ContentDAO contentDAO=new ContentDAO(conn);
		ParagraphDAO paragraphDAO=new ParagraphDAO(conn);
		SentenceDAO sentenceDAO=new SentenceDAO(conn);
		PhraseDAO phraseDAO=new PhraseDAO(conn);
		TaskDAO taskDAO=new TaskDAO(conn);
		
		int libraryTagId= 9468; //jfreechart-9468
		int[] threads=threadDAO.getThreadsIdByLibraryTagId(libraryTagId);
		int[] posts=postDAO.getPostsIdByLibraryTagId(libraryTagId);
		int[] comments=commentDAO.getCommentsIdByLibraryTagId(libraryTagId);
		System.out.println(threads.length);
		System.out.println(posts.length);
		System.out.println(comments.length);
		
		List<Integer> contents=new ArrayList<>();
		List<Integer> paragraphs=new ArrayList<>();
		List<Integer> sentences=new ArrayList<>();
		List<Integer> phrases=new ArrayList<>();
		
		for (int i = 0; i < threads.length; i++) {
			for(int id:contentDAO.getIdsBySourceTypeAndId(ContentInfo.SOURCE_TYPE_THREAD_TITLE, threads[i]))
				contents.add(id);
		}
		System.out.println(contents.size());
		for (int i = 0; i < posts.length; i++) {
			for(int id:contentDAO.getIdsBySourceTypeAndId(ContentInfo.SOURCE_TYPE_POST_BODY, posts[i]))
				contents.add(id);
		}
		System.out.println(contents.size());
		for (int i = 0; i < comments.length; i++) {
			for(int id:contentDAO.getIdsBySourceTypeAndId(ContentInfo.SOURCE_TYPE_COMMENT_TEXT, comments[i]))
				contents.add(id);
		}
		System.out.println(contents.size());
		
		for (Integer contentId : contents) {
			for(int id:paragraphDAO.getIdsByParentId(contentId))
				paragraphs.add(id);
		}
		System.out.println(paragraphs.size());
		
		for (Integer paraId : paragraphs) {
			for(int id:sentenceDAO.getIdsByParentId(paraId))
				sentences.add(id);
		}
		System.out.println(sentences.size());
		
		for (Integer sentId : sentences) {
			for(int id:phraseDAO.getIdsByParentId(sentId))
				phrases.add(id);
		}
		System.out.println(phrases.size());
		
		finalizeConnection();
	}

	public static Connection initializeConnection() {
		if (conn == null)
			conn = DBConnPool.getConnection();
		return conn;
	}

	public static void finalizeConnection() {
		if (conn != null)
			DBConnPool.closeConnection(conn);
	}

	/**
	 * Read SO data from my parsed db tables, extract the {@code}ContentInfo in
	 * thread_title/post_body/comment_text. Separate contents into
	 * {@code}ParagraphInfo, then into {@code}SentenceInfo. Store all these
	 * things into database. *
	 */
	public static void extractAndParseContentsFromSO() {
		// The initial status of connection will be resumed in the finally
		// block. Get connection if it's not active.
		boolean connFlag = true;
		if (conn == null) {
			initializeConnection();
			connFlag = false;
		}

		logger.info("Start to extract contents from stackoverflow db.");
		try {
			int handleCount = 0;
			long t0, t1, t2;
			double avrg_time;

			/** Extract @body from @PostInfo **/
			logger.info("Extract @body from @PostInfo, from table myposts.");
			MyPostDAO myPostDAO = new MyPostDAO(conn);
			List<PostInfo> posts = myPostDAO.getAll();
			logger.info(posts.size() + " post records fetched.");

			t0 = System.currentTimeMillis();
			handleCount = 0;
			if (posts != null) {
				for (PostInfo postInfo : posts) {
					String postBody = postInfo.getBody();
					// System.out.println(postInfo.getId() +
					// "===============post==============\n" + postBody);

					postBody = HtmlTextParser.parseHTMLTextToParagraphedText(postBody);

					ContentInfo contentInfo = new ContentInfo();
					contentInfo.setText(postBody);
					contentInfo.setSourceType(ContentInfo.SOURCE_TYPE_POST_BODY);
					contentInfo.setSourceId(postInfo.getId());

					DocumentParser.parseContent(contentInfo);

					if ((++handleCount) % 5000 == 0) {
						t1 = System.currentTimeMillis();
						avrg_time = ((t1 - t0) / (double) handleCount);
						logger.info(
								"[Handled]" + handleCount + "  [avrg_time]" + avrg_time + "ms. [remaining]"
										+ (posts.size() - handleCount) * avrg_time / 60000 + "min.");

					}
				}
			}
			t1 = System.currentTimeMillis();
			logger.info("Post bodies parsing finished.  [total_count]" + handleCount + "  [average_time]"
					+ ((t1 - t0) / (double) handleCount) + "ms.");
			posts = null;
			

			/** Extract @text from @CommentInfo **/
			logger.info("Extract @text from @CommentInfo, from table mycomments.");
			MyCommentDAO myCommentDAO = new MyCommentDAO(conn);
			List<CommentInfo> comments = myCommentDAO.getAll();
			logger.info(comments.size() + " comment records fetched.");

			t0 = System.currentTimeMillis();
			handleCount = 0;
			if (comments != null) {
				for (CommentInfo commentInfo : comments) {
					String commentText = commentInfo.getText();
					// System.out.println(
					// commentInfo.getId() +
					// "===============comment==============\n" + commentText);

					commentText = PlainTextParser.parsePlainTextToParagraphedText(commentText);

					ContentInfo contentInfo = new ContentInfo();
					contentInfo.setText(commentText);
					contentInfo.setSourceType(ContentInfo.SOURCE_TYPE_COMMENT_TEXT);
					contentInfo.setSourceId(commentInfo.getId());

					DocumentParser.parseContent(contentInfo);

					if ((++handleCount) % 5000 == 0) {
						t1 = System.currentTimeMillis();
						avrg_time = ((t1 - t0) / (double) handleCount);
						logger.info(
								"[Handled]" + handleCount + "  [avrg_time]" + avrg_time + "ms. [remaining]"
										+ (comments.size() - handleCount) * avrg_time / 60000 + "min.");

					}
				}
			}
			t1 = System.currentTimeMillis();
			logger.info("Comment texts parsing finished.  [total_count]" + handleCount + "  [average_time]"
					+ ((t1 - t0) / (double) handleCount) + "ms.");
			comments = null;
			

			/** Extract @title from @ThreadInfo **/
			logger.info("Extract  @title from @ThreadInfo, from table mythreads.");
			MyThreadDAO myThreadDAO = new MyThreadDAO(conn);
			List<ThreadInfo> threads = myThreadDAO.getAll();
			logger.info(threads.size() + " thread records fetched.");

			t0 = System.currentTimeMillis();
			handleCount = 0;
			if (threads != null) {
				for (ThreadInfo threadInfo : threads) {
					String threadTitle = threadInfo.getTitle();
					// System.out.println(threadInfo.getId() +
					// "=============thread================\n"
					// + threadTitle + "\n");

					ContentInfo contentInfo = new ContentInfo();
					contentInfo.setText(threadTitle);
					contentInfo.setSourceType(ContentInfo.SOURCE_TYPE_THREAD_TITLE);
					contentInfo.setSourceId(threadInfo.getId());

					DocumentParser.parseContent(contentInfo);

					if ((++handleCount) % 5000 == 0) {
						t1 = System.currentTimeMillis();
						avrg_time = ((t1 - t0) / (double) handleCount);
						logger.info(
								"[Handled]" + handleCount + "  [avrg_time]" + avrg_time + "ms. [remaining]"
										+ (threads.size() - handleCount) * avrg_time / 60000 + "min.");

					}
				}
			}
			t1 = System.currentTimeMillis();
			logger.info("Thread titles parsing finished.  [total_count]" + handleCount + "  [average_time]"
					+ ((t1 - t0) / (double) handleCount) + "ms.");
			threads = null;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (!connFlag)
				finalizeConnection();
			logger.info("Connection closed. THE END.");
		}
	}

}
