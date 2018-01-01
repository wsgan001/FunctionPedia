package cn.edu.pku.sei.tsr.dragon.document.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class SentenceDAO extends CommonDAO<SentenceInfo, Integer> {
	public static final String	FIELD_ID				= "Id";
	public static final String	FIELD_TEXT				= "Text";
	public static final String	FIELD_PARENT_ID			= "ParentId";
	public static final String	FIELD_INDEX_AS_CHILD	= "IndexAsChild";
	public static final String	FIELD_TREE_STRING		= "TreeString";
	public static final String	FIELD_CODE_TERM_STRING	= "CodeTermString";

	public SentenceDAO(Connection conn) {
		super(SentenceInfo.class, SentenceInfo.TABLE_NAME, conn);
	}

	public List<SentenceInfo> getByParentId(int parentId) {
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
		String sql = "SELECT "+FIELD_ID+" FROM " + tableName + " WHERE " + FIELD_PARENT_ID + " = "+parentId;
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


	public List<SentenceInfo> getAllAfterId(int startId) {
		String sql = "SELECT * FROM " + tableName + " WHERE " + FIELD_ID + " > ?";
		QueryRunner queryRunner = new QueryRunner();

		try {
			// log.info("[CommonDAO] 执行查询：" + sql);
			List<SentenceInfo> entities = queryRunner.query(connection, sql, new BeanListHandler<>(t),
					startId);
			return entities;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int updateTreeById(int id, String treeText) {
		if (treeText == null)
			return -1;
		String sql = "UPDATE " + tableName + " SET " + FIELD_TREE_STRING + " = ? WHERE " + FIELD_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.update(connection, sql, treeText, id);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int updateCodeTermsById(int id, String codeTerms) {
		if (codeTerms == null)
			return -1;
		String sql = "UPDATE " + tableName + " SET " + FIELD_CODE_TERM_STRING + " = ? WHERE " + FIELD_ID
				+ " = ?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.update(connection, sql, codeTerms, id);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int updateTreeAndCodeTermsById(int id, String tree, String codeTerms) {
		if (codeTerms == null)
			return -1;
		String sql = "UPDATE " + tableName + " SET " + FIELD_TREE_STRING + " = ?, " + FIELD_CODE_TERM_STRING
				+ " = ? WHERE " + FIELD_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.update(connection, sql, tree, codeTerms, id);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
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

	public SentenceInfo addSentence(SentenceInfo sentenceInfo) {
		if (sentenceInfo == null || sentenceInfo.getText() == null)
			return null;
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			keys.add(FIELD_TEXT);
			values.add(sentenceInfo.getText());

			if (sentenceInfo.getParentId() > 0) {
				keys.add(FIELD_PARENT_ID);
				values.add(sentenceInfo.getParentId());
			}

			if (sentenceInfo.getIndexAsChild() >= 0) {
				keys.add(FIELD_INDEX_AS_CHILD);
				values.add(sentenceInfo.getIndexAsChild());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;

			/** auto-increment id is blank until insertion **/
			int id = getLastInsertedId();
			sentenceInfo.setId(id);

			return sentenceInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
