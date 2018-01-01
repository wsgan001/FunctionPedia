package cn.edu.pku.sei.tsr.dragon.outdated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class QuestionDao extends CommonDAO<Question, Integer> {
	private static final String	FIELD_ID					= "Id";
	private static final String	FIELD_POST_TYPE_ID			= "PostTypeId";
	private static final String	FIELD_ACCEPTED_ANSWER_ID	= "AcceptedAnswerId";
	private static final String	FIELD_CREATION_DATE			= "CreationDate";
	private static final String	FIELD_SCORE					= "Score";
	private static final String	FIELD_VIEW_COUNT			= "ViewCount";
	private static final String	FIELD_BODY					= "Body";
	private static final String	FIELD_OWNER_USER_ID			= "OwnerUserId";
	private static final String	FIELD_LAST_EDITOR_USER_ID	= "LastEditorUserId";
	private static final String	FIELD_LAST_EDIT_DATE		= "LastEditDate";
	private static final String	FIELD_LAST_ACTIVITY_DATE	= "LastActivityDate";
	private static final String	FIELD_TITLE					= "Title";
	private static final String	FIELD_TAGS					= "Tags";
	private static final String	FIELD_ANSWER_COUNT			= "AnswerCount";
	private static final String	FIELD_COMMENT_COUNT			= "CommentCount";
	private static final String	FIELD_FAVORITE_COUNT		= "FavoriteCount";

	public QuestionDao(Connection conn) {
		super(Question.class, conn);
		tableName = "questions";
	}

	public List<Question> getAllQuestions() {
		String sql_getAllQuestions = "SELECT * FROM " + tableName;
		List<Question> questionList = new ArrayList<Question>();
		try {
			PreparedStatement statement = connection.prepareStatement(sql_getAllQuestions);
			ResultSet resultSet = statement.executeQuery();

			SimpleDateFormat format = new SimpleDateFormat(Config.getDateFormat());

			while (resultSet.next()) {
				Question question = new Question();

				question.setId(resultSet.getInt(FIELD_ID));
				question.setPostTypeId(resultSet.getInt(FIELD_POST_TYPE_ID));
				question.setAcceptedAnswerId(resultSet.getInt(FIELD_ACCEPTED_ANSWER_ID));

				question.setCreationDate(format.parse(resultSet.getString(FIELD_CREATION_DATE)));
				question.setScore(resultSet.getInt(FIELD_SCORE));
				question.setViewCount(resultSet.getInt(FIELD_VIEW_COUNT));
				question.setBody(resultSet.getString(FIELD_BODY));
				question.setOwnerUserId(resultSet.getInt(FIELD_OWNER_USER_ID));
				question.setLastEditorUserId(resultSet.getInt(FIELD_LAST_EDITOR_USER_ID));

				String last_edit_date = resultSet.getString(FIELD_LAST_EDIT_DATE);
				if (last_edit_date != null)
					question.setLastEditDate(format.parse(resultSet.getString(FIELD_LAST_EDIT_DATE)));

				question.setLastActivityDate(format.parse(resultSet.getString(FIELD_LAST_ACTIVITY_DATE)));
				question.setTitle(resultSet.getString(FIELD_TITLE));
				question.setTags(resultSet.getString(FIELD_TAGS));
				question.setAnswerCount(resultSet.getInt(FIELD_ANSWER_COUNT));
				question.setCommentCount(resultSet.getInt(FIELD_COMMENT_COUNT));
				question.setFavoriteCount(resultSet.getInt(FIELD_FAVORITE_COUNT));

				questionList.add(question);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return questionList;
	}

	public Question getQuestionById(int id) {
		String sql_getQuestionById = "SELECT * FROM " + tableName + " WHERE id = " + id;
		try {
			PreparedStatement statement = connection.prepareStatement(sql_getQuestionById);
			ResultSet resultSet = statement.executeQuery();

			SimpleDateFormat format = new SimpleDateFormat(Config.getDateFormat());

			while (resultSet.next()) {
				Question question = new Question();

				question.setId(resultSet.getInt(FIELD_ID));
				question.setPostTypeId(resultSet.getInt(FIELD_POST_TYPE_ID));
				question.setAcceptedAnswerId(resultSet.getInt(FIELD_ACCEPTED_ANSWER_ID));

				question.setCreationDate(format.parse(resultSet.getString(FIELD_CREATION_DATE)));
				question.setScore(resultSet.getInt(FIELD_SCORE));
				question.setViewCount(resultSet.getInt(FIELD_VIEW_COUNT));
				question.setBody(resultSet.getString(FIELD_BODY));
				question.setOwnerUserId(resultSet.getInt(FIELD_OWNER_USER_ID));
				question.setLastEditorUserId(resultSet.getInt(FIELD_LAST_EDITOR_USER_ID));

				String last_edit_date = resultSet.getString(FIELD_LAST_EDIT_DATE);
				if (last_edit_date != null)
					question.setLastEditDate(format.parse(resultSet.getString(FIELD_LAST_EDIT_DATE)));

				question.setLastActivityDate(format.parse(resultSet.getString(FIELD_LAST_ACTIVITY_DATE)));
				question.setTitle(resultSet.getString(FIELD_TITLE));
				question.setTags(resultSet.getString(FIELD_TAGS));
				question.setAnswerCount(resultSet.getInt(FIELD_ANSWER_COUNT));
				question.setCommentCount(resultSet.getInt(FIELD_COMMENT_COUNT));
				question.setFavoriteCount(resultSet.getInt(FIELD_FAVORITE_COUNT));

				return question;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<String> getQuestionTitleList() {
		String sql_getQuestionTitleList = "SELECT title FROM " + tableName;
		List<String> titleList = new ArrayList<String>();

		try {
			PreparedStatement statement = connection.prepareStatement(sql_getQuestionTitleList);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				titleList.add(resultSet.getString("title"));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return titleList;
	}

	public static void main(String[] args) {
		QuestionDao questionDao = new QuestionDao(DBConnPool.getConnection());
		// List<Question> q = questionDao.getAllQuestions();
		// for (Question question : q) {
		// log.info("\nQUESTION "+question.getId());
		// log.info(question.getTitle());
		// log.info(question.getBody());
		// log.info(question.getCreationDate());
		// }

		Question question = questionDao.getQuestionById(1437721);

		log.info("\nQUESTION　" + question.getId());
		log.info(question.getTitle());
		log.info(question.getBody());
		log.info(question.getCreationDate());

	}

}
