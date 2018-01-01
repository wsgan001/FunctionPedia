package cn.edu.pku.sei.tsr.dragon.stackoverflow.dao.so;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ColumnListHandler;

import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so.SOComment;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class SOCommentDAO extends CommonDAO<SOComment, Integer> {
	public static final String	FIELD_POST_ID	= "PostId";
	public static final String	FIELD_USER_ID	= "UserId";

	public SOCommentDAO(Connection connection) {
		super(SOComment.class, SOComment.TABLE_NAME, connection);
	}

	public List<SOComment> getByPostId(int id) {
		return getBy(FIELD_POST_ID, id);
	}

	public List<SOComment> getByUserId(int id) {
		return getBy(FIELD_USER_ID, id);
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

	public static void main(String[] args) {
		SOCommentDAO soCommentDAO = new SOCommentDAO(DBConnPool.getConnection());
		int[] ids = soCommentDAO.getIdsByPostId(44);
		for (int i = 0; i < ids.length; i++) {
			System.out.println(ids[i]);
		}
	}

}
