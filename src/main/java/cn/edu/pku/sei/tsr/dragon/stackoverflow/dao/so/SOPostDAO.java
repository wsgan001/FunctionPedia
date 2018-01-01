package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOPost;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class SOPostDAO extends CommonDAO<SOPost, Integer> {

	public static final String	FIELD_ID					= "Id";
	public static final String	FIELD_POST_TYPE_ID			= "PostTypeId";
	public static final String	FIELD_PARENT_ID				= "ParentId";
	public static final String	FIELD_ACCEPTED_ANSWER_ID	= "AcceptedAnswerId";
	public static final String	FIELD_OWNER_ID				= "OwnerUserId";
	public static final String	FIELD_LAST_EDITOR_ID		= "LastEditorUserId";
	public static final String	FIELD_TAGS					= "Tags";

	public String				countPostsByOwnerIdSql;

	public SOPostDAO(Connection conn) {
		super(SOPost.class, SOPost.TABLE_NAME, conn);
		countPostsByOwnerIdSql = "SELECT count(*) FROM " + tableName + " WHERE " + FIELD_OWNER_ID
				+ " = ? and " + FIELD_POST_TYPE_ID + " = ?";
	}

	public static void main(String[] args) {
	}

	public int countQuestionsByUserId(int id) {
		return count(countPostsByOwnerIdSql, id, SOPost.POST_TYPE_QUESTION);
	}

	public int countAnswersByUserId(int id) {
		return count(countPostsByOwnerIdSql, id, SOPost.POST_TYPE_ANSWER);
	}

	public int[] getAnswersIdByQuestionPostId(int id) {
		String sql = "SELECT " + FIELD_ID + " FROM " + tableName + " WHERE " + FIELD_PARENT_ID
				+ " = " + id;
		QueryRunner queryRunner = new QueryRunner();
		try {
			List<Integer> idList = queryRunner.query(connection, sql,
					new ColumnListHandler<Integer>());
			return Utils.convertIntegerListToIntArray(idList);
		}
		catch (SQLException | NullPointerException e) {
			e.printStackTrace();
			return new int[] {};
		}
	}

	public String getTagsById(int id) {
		String sql = "SELECT " + FIELD_TAGS + " FROM " + tableName + " WHERE " + FIELD_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			ResultSetHandler<String> rsh = new BeanHandler<String>(String.class);
			return queryRunner.query(connection, sql, rsh, id);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<SOPost> getQuestionsByTag(String tag) {
		// No need to specify "posttypeid = 1" that tags are only populated when
		// it's a question
		String sql = "SELECT * FROM post WHERE Tags LIKE '%<" + tag + ">%' ";

		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<>(t));
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<SOPost> getAllQuestions() {
		return getBy(FIELD_POST_TYPE_ID, SOPost.POST_TYPE_QUESTION);
	}

	public List<SOPost> getAllAnswers() {
		return getBy(FIELD_POST_TYPE_ID, SOPost.POST_TYPE_ANSWER);
	}

	public List<SOPost> getByParentId(int id) {
		return getBy(FIELD_PARENT_ID, id);
	}

	public List<SOPost> getByAcceptedAnswerId(int id) {
		return getBy(FIELD_ACCEPTED_ANSWER_ID, id);
	}

	public List<SOPost> getByOwnerId(int id) {
		return getBy(FIELD_OWNER_ID, id);
	}

	public List<SOPost> getByLastEditorId(int id) {
		return getBy(FIELD_LAST_EDITOR_ID, id);
	}
}
