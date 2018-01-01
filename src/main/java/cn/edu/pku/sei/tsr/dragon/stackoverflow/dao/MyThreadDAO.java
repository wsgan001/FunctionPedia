package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class MyThreadDAO extends CommonDAO<ThreadInfo, Integer> {
	public static final String	FIELD_ID					= "Id";
	public static final String	FIELD_TITLE					= "Title";
	public static final String	FIELD_QUESTION_ID			= "QuestionId";
	public static final String	FIELD_ACCEPTED_ANSWER_ID	= "AcceptedAnswerId";
	public static final String	FIELD_VIEW_COUNT			= "ViewCount";
	public static final String	FIELD_FAVORITE_COUNT		= "FavoriteCount";
	public static final String	FIELD_VOTE					= "Vote";
	public static final String	FIELD_LIBRARY_TAG_ID		= "LibraryTagId";

	public MyThreadDAO(Connection connection) {
		super(ThreadInfo.class, ThreadInfo.TABLE_NAME, connection);
	}

	public static void main(String[] args) {
		MyThreadDAO myThreadDAO = new MyThreadDAO(DBConnPool.getConnection());
		int[] arr = myThreadDAO.getThreadsIdByLibraryTagIdOrderByVoteLimitK(152, 200);
		System.out.println(Arrays.toString(arr));
		System.out.println(arr.length);
	}

	public ThreadInfo addThread(ThreadInfo threadInfo) {
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			if (threadInfo.getQuestionId() <= 0)
				return null;

			keys.add(FIELD_TITLE);
			values.add(threadInfo.getTitle());

			keys.add(FIELD_QUESTION_ID);
			values.add(threadInfo.getQuestionId());

			if (threadInfo.getAcceptedAnswerId() > 0) {
				keys.add(FIELD_ACCEPTED_ANSWER_ID);
				values.add(threadInfo.getAcceptedAnswerId());
			}
			keys.add(FIELD_VIEW_COUNT);
			values.add(threadInfo.getViewCount());

			keys.add(FIELD_FAVORITE_COUNT);
			values.add(threadInfo.getFavoriteCount());

			keys.add(FIELD_VOTE);
			values.add(threadInfo.getVote());

			if (threadInfo.getLibraryTagId() > 0) {
				keys.add(FIELD_LIBRARY_TAG_ID);
				values.add(threadInfo.getLibraryTagId());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;

			// answersId[] are blank in the returnedInsertion,
			// while id is blank in the threadInfo
			int id = getLastInsertedId();
			threadInfo.setId(id);
			return threadInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int[] getThreadsIdByLibraryTagIdOrderByVoteLimitK(int libraryTagId, int limit) {

		String sql = "SELECT " + FIELD_ID + " FROM " + tableName + " WHERE " + FIELD_LIBRARY_TAG_ID + " = "
				+ libraryTagId + " ORDER BY " + FIELD_VOTE + " DESC ";
		if (limit > 0)
			sql += " LIMIT " + limit;

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
	
	public int[] getThreadsIdByLibraryTagId(int libraryTagId) {

		String sql = "SELECT " + FIELD_ID + " FROM " + tableName + " WHERE " + FIELD_LIBRARY_TAG_ID + " = "
				+ libraryTagId;

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

	public ThreadInfo getByQuestionId(int id) {
		List<ThreadInfo> results = getBy(FIELD_QUESTION_ID, id);
		if (results == null || results.size() <= 0)
			return null;
		else
			return results.get(0);
	}

	public List<ThreadInfo> getByAcceptedAnswerId(int id) {
		return getBy(FIELD_ACCEPTED_ANSWER_ID, id);
	}

	// public int[] getAnswersIdByQuestionId(int id) {
	// ThreadInfo soThread = getByQuestionId(id);
	// if (soThread == null)
	// return null;
	//
	// try {
	// String[] answersIdString = soThread.getAnswersId().split(",");
	// int[] answersId = new int[answersIdString.length];
	// for (int i = 0; i < answersId.length; i++) {
	// answersId[i] = Integer.parseInt(answersIdString[i]);
	// }
	// return answersId;
	// }
	// catch (NullPointerException e) {
	// // thread.getAnswersId() returns null
	// return null;
	// }
	// }
}
