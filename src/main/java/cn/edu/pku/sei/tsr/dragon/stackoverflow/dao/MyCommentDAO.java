package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class MyCommentDAO extends CommonDAO<CommentInfo, Integer> {
	public static final String	FIELD_ID		= "Id";
	public static final String	FIELD_POST_ID	= "PostId";
	public static final String	FIELD_USER_ID	= "UserId";
	public static final String	FIELD_TEXT		= "text";
	public static final String	FIELD_SCORE		= "score";

	public MyCommentDAO(Connection connection) {
		super(CommentInfo.class, CommentInfo.TABLE_NAME, connection);
	}

	public CommentInfo addComment(CommentInfo comment) {
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			keys.add(FIELD_ID);
			values.add(comment.getId());

			keys.add(FIELD_POST_ID);
			values.add(comment.getPostId());

			keys.add(FIELD_SCORE);
			values.add(comment.getScore());

			keys.add(FIELD_TEXT);
			values.add(comment.getText());

			keys.add(FIELD_USER_ID);
			values.add(comment.getUserId());

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;
			return comment;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CommentInfo> getByPostId(int id) {
		return getBy(FIELD_POST_ID, id);
	}

	public List<CommentInfo> getByUserId(int id) {
		return getBy(FIELD_USER_ID, id);
	}
	
	public int[] getCommentsIdByLibraryTagId(int libraryTagId) {
		String sql = "SELECT C." + FIELD_ID + " FROM " + tableName +" as C, "+ PostInfo.TABLE_NAME+ " as P, " 
				+ ThreadInfo.TABLE_NAME + " as T WHERE T."
				+ MyThreadDAO.FIELD_LIBRARY_TAG_ID + " = "	+ libraryTagId
				+ " AND C." + FIELD_POST_ID + " = P." + MyPostDAO.FIELD_ID
				+ " AND P." + MyPostDAO.FIELD_THREAD_ID + " = T." + MyThreadDAO.FIELD_ID;
		QueryRunner queryRunner = new QueryRunner();
		try {
			List<Integer> idList = queryRunner.query(connection, sql, new ColumnListHandler<Integer>());
			return Utils.convertIntegerListToIntArray(idList);
		}
		catch (SQLException | NullPointerException e) {
			e.printStackTrace();
			return new int[] {};
		}
	}

	public int[] getIdsByPostId(int id) {
		String sql = "SELECT " + FIELD_ID + " FROM " + tableName + " WHERE " + FIELD_POST_ID + " = "
				+ id;
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

}
