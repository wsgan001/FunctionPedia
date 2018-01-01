package cn.edu.pku.sei.tsr.dragon.document.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class ParagraphDAO extends CommonDAO<ParagraphInfo, Integer> {
	public static final String	FIELD_ID				= "Id";
	public static final String	FIELD_PARAGRAPH_TYPE	= "ParagraphType";
	public static final String	FIELD_TEXT				= "Text";
	public static final String	FIELD_PARENT_ID			= "ParentId";
	public static final String	FIELD_INDEX_AS_CHILD	= "IndexAsChild";

	public ParagraphDAO(Connection conn) {
		super(ParagraphInfo.class, ParagraphInfo.TABLE_NAME, conn);
	}

	public ParagraphInfo addParagraph(ParagraphInfo paragraphInfo) {
		if (paragraphInfo == null || paragraphInfo.getText() == null)
			return null;
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			keys.add(FIELD_PARAGRAPH_TYPE);
			values.add(paragraphInfo.getParagraphType());

			keys.add(FIELD_TEXT);
			values.add(paragraphInfo.getText());

			if (paragraphInfo.getParentId() > 0) {
				keys.add(FIELD_PARENT_ID);
				values.add(paragraphInfo.getParentId());
			}

			if (paragraphInfo.getIndexAsChild() >= 0) {
				keys.add(FIELD_INDEX_AS_CHILD);
				values.add(paragraphInfo.getIndexAsChild());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;

			/** auto-increment id is blank until insertion **/
			int id = getLastInsertedId();
			paragraphInfo.setId(id);

			return paragraphInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getParentIdById(int id) {
		String sql = "SELECT " + FIELD_PARENT_ID + " FROM " + tableName + " WHERE " + FIELD_ID + " = ?";
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

	public List<ParagraphInfo> getByParentId(int parentId) {
		String sql = "SELECT * FROM " + tableName + " WHERE " + FIELD_PARENT_ID + " = ? ";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<>(t), parentId);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int[] getIdsByParentId(int parentId) {
		String sql = "SELECT "+ FIELD_ID +" FROM " + tableName + " WHERE " + FIELD_PARENT_ID + " = "+parentId;
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
}
