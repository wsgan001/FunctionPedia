package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.taskdocmodel.search.SearchEngine.MyThread;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class MyPostDAO extends CommonDAO<PostInfo, Integer> {
	public static final String	FIELD_ID				= "Id";
	public static final String	FIELD_POST_TYPE			= "PostType";
	public static final String	FIELD_THREAD_ID			= "ThreadId";
	public static final String	FIELD_BODY				= "Body";
	public static final String	FIELD_SCORE				= "Score";
	public static final String	FIELD_OWNER_USER_ID		= "OwnerUserId";
	public static final String	FIELD_LAST_EDITOR_ID	= "LastEditorUserId";

	public MyPostDAO(Connection conn) {
		super(PostInfo.class, PostInfo.TABLE_NAME, conn);
	}

	public PostInfo addPost(PostInfo post) {
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			keys.add(FIELD_ID);
			values.add(post.getId());

			keys.add(FIELD_THREAD_ID);
			values.add(post.getThreadId());

			keys.add(FIELD_POST_TYPE);
			values.add(post.getPostType());

			keys.add(FIELD_BODY);
			values.add(post.getBody());

			keys.add(FIELD_SCORE);
			values.add(post.getScore());

			if (post.getOwnerUserId() > 0) {
				keys.add(FIELD_OWNER_USER_ID);
				values.add(post.getOwnerUserId());
			}
			if (post.getLastEditorUserId() > 0) {
				keys.add(FIELD_LAST_EDITOR_ID);
				values.add(post.getLastEditorUserId());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;
			return post;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int[] getAllPostsIdByThreadId(int id) {
		String sql = "SELECT " + FIELD_ID + " FROM " + tableName + " WHERE " + FIELD_THREAD_ID + " = " + id;
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
	
	public int[] getPostsIdByLibraryTagId(int libraryTagId) {
		String sql = "SELECT P." + FIELD_ID + " FROM " + tableName + " as P, " + ThreadInfo.TABLE_NAME + " as T WHERE T."
				+ MyThreadDAO.FIELD_LIBRARY_TAG_ID + " = "	+ libraryTagId
				+ " AND P." + FIELD_THREAD_ID + " = T." + MyThreadDAO.FIELD_ID;
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

	public int[] getAnswersIdByThreadId(int id) {
		String sql = "SELECT " + FIELD_ID + " FROM " + tableName + " WHERE " + FIELD_THREAD_ID + " = " + id
				+ " and " + FIELD_POST_TYPE + " = " + PostInfo.ANSWER_TYPE;
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

	public int getThreadIdById(int id) {
		String sql = "SELECT " + FIELD_THREAD_ID + " FROM " + tableName + " WHERE " + FIELD_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		ScalarHandler<Integer> scalarHandler = new ScalarHandler<>();
		try {
			Integer result = queryRunner.query(connection, sql, scalarHandler, id);
			return result.intValue();
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<PostInfo> getAllQuestions() {
		return getBy(FIELD_POST_TYPE, PostInfo.QUESTION_TYPE);
	}

	public List<PostInfo> getAllAnswers() {
		return getBy(FIELD_POST_TYPE, PostInfo.ANSWER_TYPE);
	}

	public List<PostInfo> getByThreadId(int id) {
		return getBy(FIELD_THREAD_ID, id);
	}

	public List<PostInfo> getByOwnerUserId(int id) {
		return getBy(FIELD_OWNER_USER_ID, id);
	}

	public List<PostInfo> getByLastEditorId(int id) {
		return getBy(FIELD_LAST_EDITOR_ID, id);
	}

}
