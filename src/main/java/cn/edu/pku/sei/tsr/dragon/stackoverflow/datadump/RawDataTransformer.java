package cn.edu.pku.sei.tsr.dragon.stackoverflow.datadump;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyThreadDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyThreadTagsDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyUserDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOBadgeDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOPostDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOTagDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOUserDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.UserInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOComment;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPost;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOUser;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class RawDataTransformer {
	public static final Logger	logger	= Logger.getLogger(RawDataTransformer.class);
	public static Connection	conn;

	public static void main(String[] args) {
		transformRawSODataIntoMyType();
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
	 * Handle the raw SO data, transform into my data types({@code}CommentInfo,
	 * {@code}PostInfo, {@code}ThreadInfo, {@code}UserInfo,
	 * {@code}ThreadTagInfo), and then store them into corresponding db tables.
	 * The transformed data are more concise and light-weighted. Also we
	 * re-established the relationships between thread and its belongings.
	 * 
	 */
	public static void transformRawSODataIntoMyType() {
		// The initial status of connection will be resumed in the finally
		// block. Get connection if it's not active.
		boolean connFlag = true;
		if (conn == null) {
			initializeConnection();
			connFlag = false;
		}

		try {
			SOPostDAO postDao = new SOPostDAO(conn);

			logger.info("Start loading all the question-posts....");
			long t0 = System.currentTimeMillis();
			List<SOPost> questions = postDao.getAllQuestions();

			long t1 = System.currentTimeMillis();
			logger.info(questions.size() + " questions fetched in " + (t1 - t0) / (double) 1000
					+ "s. Ready to process them.");
			long t2;
			double average_time_global;
			double average_time_local;

			t0 = System.currentTimeMillis();
			t1 = t0;
			for (int i = 0; i < questions.size(); i++) {
				SOPost question = questions.get(i);

				ThreadInfo threadInfo = handleQuestionPost(question);
				linkThreadTags(threadInfo.getId(), question.getTags());

				if (i % 1000 == 0) {
					t2 = System.currentTimeMillis();
					average_time_global = (t2 - t0) / (double) i;
					average_time_local = (t2 - t1) / (double) 1000;
					t1 = t2;
					logger.info(i + " questions handled in " + ((t2 - t0) / (double) 1000)
							+ " seconds. [global_avrg]" + average_time_global + "ms. [local_avrg]"
							+ average_time_local + "ms.");
				}
			}

			t2 = System.currentTimeMillis();
			average_time_global = (t2 - t0) / (double) questions.size();
			logger.info(questions.size() + " questions handled. [total_time]" + (t2 - t0) / (double) 1000
					+ "s. [avrg_time]" + average_time_global + "ms.\n");

			logger.info("Copying users info into myusers...");
			t0 = System.currentTimeMillis();
			SOUserDAO soUserDAO = new SOUserDAO(conn);
			List<SOUser> users = soUserDAO.getAll();
			t1 = System.currentTimeMillis();
			logger.info(users.size() + " users fetched in " + (t1 - t0) / (double) 1000
					+ "s. Ready to process them.");

			t0 = System.currentTimeMillis();
			t1 = t0;
			for (int i = 0; i < users.size(); i++) {
				handleUser(users.get(i));

				if (i % 100000 == 0) {
					t2 = System.currentTimeMillis();
					average_time_global = (t2 - t0) / (double) i;
					average_time_local = (t2 - t1) / (double) 100000;
					t1 = t2;
					logger.info(
							i + " users copied in " + ((t2 - t0) / (double) 1000) + " seconds. [global_avrg]"
									+ average_time_global + "ms. [local_avrg]" + average_time_local + "ms.");
				}
			}
			t2 = System.currentTimeMillis();
			average_time_global = (t2 - t0) / (double) users.size();
			logger.info("Finished copying " + users.size() + " users. [total_time]"
					+ (t2 - t0) / (double) 1000 + "s. [avrg_time]" + average_time_global + "ms.\n");

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

	public static ThreadInfo handleQuestionPost(SOPost questionPost) {
		if (questionPost == null)
			return null;
		/*** build up a thread based on the question post ***/
		ThreadInfo threadInfo = new ThreadInfo();

		threadInfo.setQuestionId(questionPost.getId());
		threadInfo.setTitle(questionPost.getTitle());

		if (questionPost.getAcceptedAnswerId() > 0)
			threadInfo.setAcceptedAnswerId(questionPost.getAcceptedAnswerId());

		threadInfo.setViewCount(questionPost.getViewCount());
		threadInfo.setFavoriteCount(questionPost.getFavoriteCount());
		threadInfo.setVote(questionPost.getScore());

		String[] tags = SOSubjectTagUtils.extractSubjectTags(questionPost.getTags());
		if (tags != null && tags.length > 0) {
			SOTagDAO tagDAO = new SOTagDAO(conn);
			int tagId = tagDAO.getTagIdByTagName(tags[0]);
			threadInfo.setLibraryTagId(tagId);
		}

		SOPostDAO soPostDAO = new SOPostDAO(conn);
		int[] answersId = soPostDAO.getAnswersIdByQuestionPostId(questionPost.getId());
		threadInfo.setAnswersId(answersId);

		/*** store this thread into database ***/
		MyThreadDAO myThreadDAO = new MyThreadDAO(conn);
		// threadId is blank until insertion
		threadInfo = myThreadDAO.addThread(threadInfo);

		int threadId = threadInfo.getId();
		answersId = threadInfo.getAnswersId();

		/*** handle question post of this thread ***/
		handlePost(questionPost, threadId);

		/*** handle accepted answer post (if existed) of this thread ***/
		int acceptedAnswerId = threadInfo.getAcceptedAnswerId();
		if (acceptedAnswerId > 0) {
			SOPost acceptedAnswerPost = soPostDAO.getById(acceptedAnswerId);
			handlePost(acceptedAnswerPost, threadId);
		}

		/*** handle other answer posts of this thread ***/
		for (int i = 0; i < answersId.length; i++) {
			if (answersId[i] == acceptedAnswerId)
				continue;

			SOPost answerPost = soPostDAO.getById(answersId[i]);
			handlePost(answerPost, threadId);
		}

		return threadInfo;
	}

	public static PostInfo handlePost(SOPost post, int threadId) {
		if (post == null)
			return null;
		/*** build up a post from the so post ***/
		PostInfo postInfo = new PostInfo();

		postInfo.setId(post.getId());
		postInfo.setPostType(post.getPostTypeId());
		postInfo.setScore(post.getScore());
		postInfo.setBody(post.getBody());

		postInfo.setThreadId(threadId);

		if (post.getOwnerUserId() > 0)
			postInfo.setOwnerUserId(post.getOwnerUserId());
		if (post.getLastEditorUserId() > 0)
			postInfo.setLastEditorUserId(post.getLastEditorUserId());

		SOCommentDAO soCommentDAO = new SOCommentDAO(conn);
		int[] commentsId = soCommentDAO.getIdsByPostId(postInfo.getId());
		postInfo.setCommentsId(commentsId);

		/*** store this post into database ***/
		MyPostDAO myPostDAO = new MyPostDAO(conn);
		postInfo = myPostDAO.addPost(postInfo);
		if (postInfo == null)
			return null;

		/*** handle comments of this post ***/
		commentsId = postInfo.getCommentsId();
		for (int i = 0; i < commentsId.length; i++) {
			SOComment comment = soCommentDAO.getById(commentsId[i]);
			handleComment(comment);
		}
		return postInfo;
	}

	public static CommentInfo handleComment(SOComment comment) {
		if (comment == null)
			return null;
		CommentInfo commentInfo = new CommentInfo();

		commentInfo.setId(comment.getId());
		commentInfo.setPostId(comment.getPostId());
		commentInfo.setScore(comment.getScore());
		commentInfo.setText(comment.getText());
		if (comment.getUserId() > 0)
			commentInfo.setUserId(comment.getUserId());

		MyCommentDAO myCommentDAO = new MyCommentDAO(conn);
		commentInfo = myCommentDAO.addComment(commentInfo);

		return commentInfo;
	}

	public static UserInfo handleUser(SOUser user) {
		if (user == null)
			return null;

		UserInfo userInfo = new UserInfo();
		userInfo.setId(user.getId());
		userInfo.setDisplayName(user.getDisplayName());
		userInfo.setReputation(user.getReputation());

		userInfo.setViews(user.getViews());
		userInfo.setUpVotes(user.getUpVotes());
		userInfo.setDownVotes(user.getDownVotes());

		int userId = userInfo.getId();
		SOBadgeDAO soBadgeDAO = new SOBadgeDAO(conn);
		userInfo.setBadgesCountFirstClass(soBadgeDAO.countFirstClassBadgeByUserId(userId));
		userInfo.setBadgesCountSecondClass(soBadgeDAO.countSecondClassBadgeByUserId(userId));
		userInfo.setBadgesCountThirdClass(soBadgeDAO.countThirdClassBadgeByUserId(userId));

		SOPostDAO soPostDAO = new SOPostDAO(conn);
		userInfo.setAskedQuestionsCount(soPostDAO.countQuestionsByUserId(userId));
		userInfo.setAnswersCount(soPostDAO.countAnswersByUserId(userId));

		MyUserDAO myUserDAO = new MyUserDAO(conn);
		userInfo = myUserDAO.addUser(userInfo);

		return userInfo;
	}

	/**
	 * Resolve all the tags of {@code}post and add a record of post-tag in
	 * posttag table.
	 * 
	 * @param questionPost
	 */
	public static void linkThreadTags(int threadId, String postTags) {
		if (threadId <= 0)
			return;
		try {
			SOTagDAO soTagDAO = new SOTagDAO(conn);
			MyThreadTagsDAO soPostTagDAO = new MyThreadTagsDAO(conn);

			String[] tags = SOSubjectTagUtils.splitTags(postTags);
			for (String tagName : tags) {
				int tagId = soTagDAO.getTagIdByTagName(tagName);
				soPostTagDAO.addThreadTag(threadId, tagId);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void transformRawSODataIntoMyTypeByTags() {
		try {
			SOPostDAO postDao = new SOPostDAO(conn);

			for (String subjectTag : SOSubjectTagUtils.subjectTags) {
				logger.info("Start reading posts of <" + subjectTag + ">....");
				List<SOPost> allQuestions = postDao.getQuestionsByTag(subjectTag);

				logger.info(allQuestions.size() + " questions have been found. Ready to process them.");
				long t0 = System.currentTimeMillis();
				long t1 = t0;
				long t2;
				for (int i = 0; i < allQuestions.size(); i++) {
					SOPost question = allQuestions.get(i);

					/*** handle questions and so forth... ***/
					handleQuestionPost(question);

					if ((i + 1) % 500 == 0) {
						t2 = System.currentTimeMillis();
						long average_time_global = (t2 - t0) / (i + 1);
						long average_time_local = (t2 - t1) / 500;
						t1 = t2;
						logger.info((i + 1) + " questions have been processed in " + ((t2 - t0) / 1000)
								+ " seconds. [global_avrg]" + average_time_global + "ms. [local_avrg]"
								+ average_time_local + "ms.");
					}
				}
				t2 = System.currentTimeMillis();
				long average_time_global = (t2 - t0) / allQuestions.size();
				logger.info("Posts of <" + subjectTag + "> are done. [avrg_time]" + average_time_global
						+ "ms.\n");
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(conn);
			logger.info("Connection closed. THE END.");
		}
	}
}
