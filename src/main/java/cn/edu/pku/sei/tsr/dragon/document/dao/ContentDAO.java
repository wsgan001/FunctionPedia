package cn.edu.pku.sei.tsr.dragon.document.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.lang3.tuple.Pair;

import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class ContentDAO extends CommonDAO<ContentInfo, Integer> {
	public static final String	FIELD_ID			= "Id";
	public static final String	FIELD_TEXT			= "Text";
	public static final String	FIELD_SOURCE_TYPE	= "SourceType";
	public static final String	FIELD_SOURCE_ID		= "SourceId";

	public ContentDAO(Connection conn) {
		super(ContentInfo.class, ContentInfo.TABLE_NAME, conn);
	}

	public ContentInfo addContent(ContentInfo contentInfo) {
		if (contentInfo == null || contentInfo.getText() == null)
			return null;
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			keys.add(FIELD_TEXT);
			values.add(contentInfo.getText());

			if (contentInfo.getSourceType() > 0) {
				keys.add(FIELD_SOURCE_TYPE);
				values.add(contentInfo.getSourceType());
			}

			if (contentInfo.getSourceId() > 0) {
				keys.add(FIELD_SOURCE_ID);
				values.add(contentInfo.getSourceId());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;

			/** auto-increment id is blank until insertion **/
			int id = getLastInsertedId();
			contentInfo.setId(id);

			return contentInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<ContentInfo> getBySourceTypeAndId(int sourceType, int sourceId) {
		String sql = "SELECT * FROM " + tableName + " WHERE " + FIELD_SOURCE_TYPE + " = ? and "
				+ FIELD_SOURCE_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<>(t), sourceType, sourceId);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int[] getIdsBySourceTypeAndId(int sourceType, int sourceId) {
		String sql = "SELECT "+FIELD_ID+" FROM " + tableName + " WHERE " + FIELD_SOURCE_TYPE + " = "+sourceType+" and "
				+ FIELD_SOURCE_ID + " = "+sourceId;
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

	public Pair<Integer, Integer> getSourceTypeAndIdById(int id) {
		String sql = "SELECT " + FIELD_SOURCE_TYPE + ", " + FIELD_SOURCE_ID + " FROM " + tableName + " WHERE "
				+ FIELD_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		ArrayHandler arrayHandler = new ArrayHandler();
		try {
			Object[] results = queryRunner.query(connection, sql, arrayHandler, id);
			// results should consist of two elements, [0] for source type and
			// [1] for source id, respectively
			if (results != null && results.length >= 2)
				return Pair.of((Integer) results[0], (Integer) results[1]);
			return null;
		}
		catch (SQLException | ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		ContentDAO cd = new ContentDAO(DBConnPool.getConnection());
		Pair<Integer, Integer> r = cd.getSourceTypeAndIdById(300000);
		System.out.println(r.getLeft());
		System.out.println(r.getRight());
	}
}
