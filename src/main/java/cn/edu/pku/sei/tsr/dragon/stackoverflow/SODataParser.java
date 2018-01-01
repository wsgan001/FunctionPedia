package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.outdated.Answer;
import cn.edu.pku.sei.tsr.dragon.outdated.AnswerDAO;
import cn.edu.pku.sei.tsr.dragon.outdated.Question;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.MyThreadTagsDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOCommentDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOUserDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so.SOVoteDAO;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadTagInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.UserInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOComment;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOUser;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOVote;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class SODataParser {
	public static final Logger logger = Logger.getLogger(SODataParser.class);

	public static OldThreadInfo parseThread(Question question) {
		if (question == null)
			return null;

		OldThreadInfo thread = new OldThreadInfo();
		Connection connection = DBConnPool.getConnection();
		try {
			long t1 = System.currentTimeMillis();
			// thread基本信息
			thread.setId(question.getId());
			thread.setViewCount(question.getViewCount());
			thread.setFavoriteCount(question.getFavoriteCount());

			// thread library
			thread.setLibraryName(APILibrary.judgeProjectByTags(question.getTags()));

			// thread title
			ContentInfo titleContent = new ContentInfo(question.getTitle());
			titleContent.setParentUuid(thread.getUuid());
			titleContent.setHTMLContent(true);
			thread.setTitle(titleContent);

			long t3 = System.currentTimeMillis();

			// thread的主问题
			PostInfo questionPost = parseQuestion(question);
			questionPost.setPostType(PostInfo.QUESTION_TYPE);

			thread.setQuestion(questionPost);
			questionPost.setParent(thread);

			long t4 = System.currentTimeMillis();
			AnswerDAO answerDAO = new AnswerDAO(connection);
			// thread的accepted answer
			int acceptedAnswerId = question.getAcceptedAnswerId();
			if (acceptedAnswerId > 0) {
				// some questions don't have an accepted answer, their query
				// result equals 0
				Answer rawAns = answerDAO.getAnswersById(acceptedAnswerId);
				PostInfo acceptedAnswer = parseAnswer(rawAns);
				if (acceptedAnswer != null) {
					acceptedAnswer.setPostType(PostInfo.ACCEPTED_ANSWER_TYPE);

					thread.getAnswers().add(acceptedAnswer);
					acceptedAnswer.setParent(thread);
				}
			}

			long t5 = System.currentTimeMillis();
			// thread 的answer list
			List<Answer> answers = answerDAO.getAnswersByParentId(question.getId());
			parseAnswerList(answers).forEach(answerPost -> {
				thread.getAnswers().add(answerPost);
				answerPost.setParent(thread);
			});

			long t6 = System.currentTimeMillis();
			// thread 的tag list
			MyThreadTagsDAO soPostTagDAO = new MyThreadTagsDAO(connection);
			List<ThreadTagInfo> soPostTags = soPostTagDAO.getByPostId(question.getId());
			thread.setTags(parseTagList(soPostTags));
			long t7 = System.currentTimeMillis();

			// logger.info("\t[Qid:" + question.getId() + "][info:" + (t3 - t1)
			// + "ms][question:" + (t4 - t3) + "ms][acptAns:" + (t5 - t4)
			// + "ms][ansList:" + (t6 - t5) + "ms][tags:" + (t7 - t6)
			// + "ms]");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(connection);
		}

		return thread;
	}

	private static PostInfo parseQuestion(Question question) {
		if (question == null)
			return null;

		PostInfo questionPost = new PostInfo();
		Connection connection = DBConnPool.getConnection();
		try {
			// 类型
			questionPost.setPostType(PostInfo.QUESTION_TYPE);

			// basic infos
			questionPost.setId(question.getId());
			questionPost.setCreationDate(question.getCreationDate());
			questionPost.setLastEditDate(question.getLastEditDate());
			questionPost.setScore(question.getScore());

			// question content
			ContentInfo questionContent = new ContentInfo(question.getBody());
			questionContent.setParent(questionPost);
			questionContent.setHTMLContent(true);
			questionPost.setContent(questionContent);

			SOUserDAO soUserDAO = new SOUserDAO(connection);
			// owner user
			SOUser ownerUser = soUserDAO.getById(question.getOwnerUserId());
			questionPost.setOwnerUser(parseUser(ownerUser));
			// last editor user
			SOUser lastEditorUser = soUserDAO.getById(question.getLastEditorUserId());
			questionPost.setLastEditorUser(parseUser(lastEditorUser));

			// comment list
			SOCommentDAO soCommentDAO = new SOCommentDAO(connection);
			List<SOComment> questionComments = soCommentDAO.getByPostId(question.getId());
			List<CommentInfo> commentList = parseCommentList(questionComments);
			if (commentList != null)
				for (CommentInfo comment : commentList) {
					questionPost.getComments().add(comment);
					comment.setParent(questionPost);
				}

			// vote list
			SOVoteDAO soVoteDAO = new SOVoteDAO(connection);
			List<SOVote> soVotes = soVoteDAO.getByPostId(question.getId());
			questionPost.setVotes(parseVoteList(soVotes));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(connection);
		}

		return questionPost;
	}

	private static PostInfo parseAnswer(Answer answer) {
		if (answer == null)
			return null;
		PostInfo answerPost = new PostInfo();
		Connection connection = DBConnPool.getConnection();
		try {
			// 类型
			answerPost.setPostType(PostInfo.ANSWER_TYPE);

			// basic info
			answerPost.setId(answer.getId());
			answerPost.setCreationDate(answer.getCreationDate());
			answerPost.setLastEditDate(answer.getLastEditDate());
			answerPost.setScore(answer.getScore());

			// answer content
			ContentInfo answerContent = new ContentInfo(answer.getBody());
			answerContent.setParent(answerPost);
			answerContent.setHTMLContent(true);
			answerPost.setContent(answerContent);

			SOUserDAO soUserDAO = new SOUserDAO(connection);
			// owner user
			SOUser ownerUser = soUserDAO.getById(answer.getOwnerUserId());
			answerPost.setOwnerUser(parseUser(ownerUser));
			// last editor user
			SOUser lastEditorUser = soUserDAO.getById(answer.getLastEditorUserId());
			answerPost.setLastEditorUser(parseUser(lastEditorUser));

			// comment list
			SOCommentDAO soCommentDAO = new SOCommentDAO(connection);
			List<SOComment> answerComments = soCommentDAO.getByPostId(answer.getId());
			List<CommentInfo> commentList = parseCommentList(answerComments);
			if (commentList != null)
				for (CommentInfo comment : commentList) {
					answerPost.getComments().add(comment);
					comment.setParent(answerPost);
				}

			// vote list
			SOVoteDAO soVoteDAO = new SOVoteDAO(connection);
			List<SOVote> soVotes = soVoteDAO.getByPostId(answer.getId());
			answerPost.setVotes(parseVoteList(soVotes));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(connection);
		}

		return answerPost;
	}

	private static List<PostInfo> parseAnswerList(List<Answer> answerList) {
		if (answerList == null)
			return null;
		List<PostInfo> newAnswerList = new ArrayList<PostInfo>();
		for (Answer answer : answerList) {
			PostInfo newAnswer = parseAnswer(answer);
			if (newAnswer != null)
				newAnswerList.add(newAnswer);
		}
		return newAnswerList;
	}

	private static CommentInfo parseComment(SOComment soComment) {
		if (soComment == null)
			return null;
		CommentInfo commentInfo = new CommentInfo();
		Connection connection = DBConnPool.getConnection();
		try {
			// basic info
			commentInfo.setId(soComment.getId());
			commentInfo.setCreationDate(soComment.getCreationDate());
			commentInfo.setScore(soComment.getScore());

			// comment content
			ContentInfo commentContent = new ContentInfo(soComment.getText());
			commentContent.setParent(commentInfo);
			commentContent.setHTMLContent(true);
			commentInfo.setContent(commentContent);

			// comment owner user
			UserInfo user = null;
			try {
				// 先处理非登录用户，没有id只有临时姓名的情况
				long userid = soComment.getUserId();
				if (userid <= 0) {
					user = new UserInfo();
					user.setDisplayName(soComment.getUserDisplayName());
				}
				else {
					// 根据id查询用户
					SOUserDAO soUserDAO = new SOUserDAO(connection);
					SOUser rawUser = soUserDAO.getById(soComment.getUserId());
					user = parseUser(rawUser);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			commentInfo.setOwnerUser(user);

			// vote list
			SOVoteDAO soVoteDAO = new SOVoteDAO(connection);
			List<SOVote> soVotes = soVoteDAO.getByPostId(soComment.getId());
			commentInfo.setVotes(parseVoteList(soVotes));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBConnPool.closeConnection(connection);
		}

		return commentInfo;
	}

	private static List<CommentInfo> parseCommentList(List<SOComment> commentList) {
		if (commentList == null)
			return null;
		List<CommentInfo> newCommentList = new ArrayList<CommentInfo>();
		for (SOComment soComment : commentList) {
			CommentInfo newComment = parseComment(soComment);
			if (newComment != null)
				newCommentList.add(newComment);
		}
		return newCommentList;
	}

	private static UserInfo parseUser(SOUser rawUser) {
		if (rawUser == null)
			return null;
		UserInfo user = new UserInfo();

		user.setId(rawUser.getId());
		user.setReputation(rawUser.getReputation());
		user.setDisplayName(rawUser.getDisplayName());
		user.setViews(rawUser.getViews());
		user.setUpVotes(rawUser.getUpVotes());
		user.setDownVotes(rawUser.getDownVotes());
		user.setWebsiteUrl(rawUser.getWebsiteUrl());
		user.setLocation(rawUser.getLocation());
		user.setAbouMe(rawUser.getAbouMe());
		user.setCreationDate(rawUser.getCreationDate());
		user.setLastAccessDate(rawUser.getLastAccessDate());
		user.setAge(rawUser.getAge());

		return user;
	}

	private static VoteInfo parseVote(SOVote rawVote) {
		if (rawVote == null)
			return null;
		VoteInfo newVote = new VoteInfo();
		return newVote;
	}

	private static List<VoteInfo> parseVoteList(List<SOVote> voteList) {
		if (voteList == null)
			return null;
		List<VoteInfo> newVoteList = new ArrayList<VoteInfo>();
		for (SOVote soVote : voteList) {
			VoteInfo newVote = parseVote(soVote);
			if (newVote != null)
				newVoteList.add(newVote);
		}
		return newVoteList;
	}

	private static TagInfo parseTag(ThreadTagInfo rawTag) {
		if (rawTag == null)
			return null;
		TagInfo newTag = new TagInfo(rawTag.getTag());
		return newTag;
	}

	private static List<TagInfo> parseTagList(List<ThreadTagInfo> tagList) {
		if (tagList == null)
			return null;
		List<TagInfo> newTagList = new ArrayList<TagInfo>();
		for (ThreadTagInfo soPostTag : tagList) {
			TagInfo newTag = parseTag(soPostTag);
			if (newTag != null)
				newTagList.add(newTag);
		}
		return newTagList;
	}
}
