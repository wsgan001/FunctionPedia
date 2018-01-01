package cn.edu.pku.sei.tsr.dragon.outdated;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class AnswerDAO extends CommonDAO<Answer, Integer> {
	private static final String	FIELD_ID					= "Id";
	private static final String	FIELD_POST_TYPE_ID			= "PostTypeId";
	private static final String	FIELD_PARENT_ID				= "ParentId";
	private static final String	FIELD_CREATION_DATE			= "CreationDate";
	private static final String	FIELD_SCORE					= "Score";
	private static final String	FIELD_BODY					= "Body";
	private static final String	FIELD_OWNER_USER_ID			= "OwnerUserId";
	private static final String	FIELD_LAST_EDITOR_USER_ID	= "LastEditorUserId";
	private static final String	FIELD_LAST_EDIT_DATE		= "LastEditDate";
	private static final String	FIELD_LAST_ACTIVITY_DATE	= "LastActivityDate";
	private static final String	FIELD_COMMENT_COUNT			= "CommentCount";
	private static final String	FIELD_FAVORITE_COUNT		= "FavoriteCount";

	public AnswerDAO(Connection connection) {
		super(Answer.class, connection);
		tableName = "answers";
	}
	
	public Answer getById(int id) {
		return null;
		
	}

	public Answer getAnswersById(int id) {
		String sql_getAnswersById = "SELECT * FROM " + tableName + " WHERE " + FIELD_ID + " = "
				+ id;
		Answer answer = null;
		try {
			PreparedStatement statement = connection.prepareStatement(sql_getAnswersById);
			ResultSet resultSet = statement.executeQuery();

			SimpleDateFormat format = new SimpleDateFormat(Config.getDateFormat());

			// should be only one result
			while (resultSet.next()) {
				answer = new Answer();

				answer.setId(resultSet.getInt(FIELD_ID));
				answer.setPostTypeId(resultSet.getInt(FIELD_POST_TYPE_ID));
				answer.setParentId(resultSet.getInt(FIELD_PARENT_ID));
				answer.setCreationDate(format.parse(resultSet.getString(FIELD_CREATION_DATE)));
				answer.setScore(resultSet.getInt(FIELD_SCORE));
				answer.setBody(resultSet.getString(FIELD_BODY));
				answer.setOwnerUserId(resultSet.getInt(FIELD_OWNER_USER_ID));
				answer.setLastEditorUserId(resultSet.getInt(FIELD_LAST_EDITOR_USER_ID));

				String last_edit_date = resultSet.getString(FIELD_LAST_EDIT_DATE);
				if (last_edit_date != null)
					answer.setLastEditDate(format.parse(resultSet.getString(FIELD_LAST_EDIT_DATE)));

				answer.setLastActivityDate(format.parse(resultSet
						.getString(FIELD_LAST_ACTIVITY_DATE)));
				answer.setCommentCount(resultSet.getInt(FIELD_COMMENT_COUNT));
				answer.setFavoriteCount(resultSet.getInt(FIELD_FAVORITE_COUNT));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}

	public List<Answer> getAnswersByParentId(int id) {
		String sql_getAnswersByParentId = "SELECT * FROM " + tableName + " WHERE "
				+ FIELD_PARENT_ID + " = " + id;
		List<Answer> answerList = new ArrayList<Answer>();
		try {
			PreparedStatement statement = connection.prepareStatement(sql_getAnswersByParentId);
			ResultSet resultSet = statement.executeQuery();

			SimpleDateFormat format = new SimpleDateFormat(Config.getDateFormat());

			while (resultSet.next()) {
				Answer answer = new Answer();

				answer.setId(resultSet.getInt(FIELD_ID));
				answer.setPostTypeId(resultSet.getInt(FIELD_POST_TYPE_ID));
				answer.setParentId(resultSet.getInt(FIELD_PARENT_ID));
				answer.setCreationDate(format.parse(resultSet.getString(FIELD_CREATION_DATE)));
				answer.setScore(resultSet.getInt(FIELD_SCORE));
				answer.setBody(resultSet.getString(FIELD_BODY));
				answer.setOwnerUserId(resultSet.getInt(FIELD_OWNER_USER_ID));
				answer.setLastEditorUserId(resultSet.getInt(FIELD_LAST_EDITOR_USER_ID));

				String last_edit_date = resultSet.getString(FIELD_LAST_EDIT_DATE);
				if (last_edit_date != null)
					answer.setLastEditDate(format.parse(resultSet.getString(FIELD_LAST_EDIT_DATE)));

				answer.setLastActivityDate(format.parse(resultSet
						.getString(FIELD_LAST_ACTIVITY_DATE)));
				answer.setCommentCount(resultSet.getInt(FIELD_COMMENT_COUNT));
				answer.setFavoriteCount(resultSet.getInt(FIELD_FAVORITE_COUNT));

				answerList.add(answer);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return answerList;
	}

	public List<Answer> getAllAnswers() {
		String sql_getAllAnswers = "SELECT * FROM " + tableName;
		List<Answer> answerList = new ArrayList<Answer>();
		try {
			PreparedStatement statement = connection.prepareStatement(sql_getAllAnswers);
			ResultSet resultSet = statement.executeQuery();

			SimpleDateFormat format = new SimpleDateFormat(Config.getDateFormat());

			while (resultSet.next()) {
				Answer answer = new Answer();

				answer.setId(resultSet.getInt(FIELD_ID));
				answer.setPostTypeId(resultSet.getInt(FIELD_POST_TYPE_ID));
				answer.setParentId(resultSet.getInt(FIELD_PARENT_ID));
				answer.setCreationDate(format.parse(resultSet.getString(FIELD_CREATION_DATE)));
				answer.setScore(resultSet.getInt(FIELD_SCORE));
				answer.setBody(resultSet.getString(FIELD_BODY));
				answer.setOwnerUserId(resultSet.getInt(FIELD_OWNER_USER_ID));
				answer.setLastEditorUserId(resultSet.getInt(FIELD_LAST_EDITOR_USER_ID));

				String last_edit_date = resultSet.getString(FIELD_LAST_EDIT_DATE);
				if (last_edit_date != null)
					answer.setLastEditDate(format.parse(resultSet.getString(FIELD_LAST_EDIT_DATE)));

				answer.setLastActivityDate(format.parse(resultSet
						.getString(FIELD_LAST_ACTIVITY_DATE)));
				answer.setCommentCount(resultSet.getInt(FIELD_COMMENT_COUNT));
				answer.setFavoriteCount(resultSet.getInt(FIELD_FAVORITE_COUNT));

				answerList.add(answer);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return answerList;
	}

	public static void main(String[] args) {
		AnswerDAO answerDAO = new AnswerDAO(DBConnPool.getConnection());
		List<Answer> q = answerDAO.getAllAnswers();
		for (Answer answer : q) {
			log.info("\nAnswer　" + answer.getId());
			log.info(answer.getOwnerUserId());
			log.info(answer.getBody());
			log.info(answer.getCreationDate());
		}
	}
}
