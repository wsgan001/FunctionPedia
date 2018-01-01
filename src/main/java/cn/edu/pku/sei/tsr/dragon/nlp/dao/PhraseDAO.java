package cn.edu.pku.sei.tsr.dragon.nlp.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.lang3.StringUtils;

import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;
import cn.edu.pku.sei.tsr.dragon.utils.Utils;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;
import cn.edu.pku.sei.tsr.dragon.utils.database.DBConnPool;

public class PhraseDAO extends CommonDAO<PhraseInfo, Integer> {
	public static final String	FIELD_ID			= "Id";
	public static final String	FIELD_PHRASE_TYPE	= "PhraseType";
	public static final String	FIELD_PARENT_ID		= "ParentId";
	// public static final String FIELD_OFFSET_IN_SENTENCE = "OffsetInSentence";
	public static final String	FIELD_SOURCE_PATH	= "SourcePath";

	public static final String	FIELD_TEXT			= "Text";
	public static final String	FIELD_SYNTAX_TREE	= "SyntaxTree";
	public static final String	FIELD_PROOF_STRING	= "ProofString";
	public static final String	FIELD_PROOF_SCORE	= "ProofScore";

	public static final String	FIELD_TASK_ID		= "TaskId";

	public PhraseDAO(Connection conn) {
		super(PhraseInfo.class, PhraseInfo.TABLE_NAME, conn);
	}

	public PhraseInfo addPhrase(PhraseInfo phraseInfo) {
		if (phraseInfo == null || phraseInfo.getText() == null)
			return null;
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();

			if (phraseInfo.getId() > 0) {
				keys.add(FIELD_ID);
				values.add(phraseInfo.getId());
			}

			keys.add(FIELD_PHRASE_TYPE);
			values.add(phraseInfo.getPhraseType());

			if (phraseInfo.getParentId() > 0) {
				keys.add(FIELD_PARENT_ID);
				values.add(phraseInfo.getParentId());
			}

			if (!StringUtils.isBlank(phraseInfo.getSourcePath())) {
				keys.add(FIELD_SOURCE_PATH);
				values.add(phraseInfo.getSourcePath());
			}

			keys.add(FIELD_TEXT);
			values.add(phraseInfo.getText());

			if (phraseInfo.getSyntaxTree() != null) {
				keys.add(FIELD_SYNTAX_TREE);
				values.add(phraseInfo.getSyntaxTree());
			}

			String proofString = phraseInfo.getProofString();
			if (!StringUtils.isBlank(proofString)) {
				keys.add(FIELD_PROOF_STRING);
				values.add(phraseInfo.getProofString());
			}

			int proofScore = phraseInfo.getProofScore();
			if (proofScore != PhraseInfo.PROOF_SCORE_DEFAULT) {
				keys.add(FIELD_PROOF_SCORE);
				values.add(phraseInfo.getProofScore());
			}

			if (phraseInfo.getTaskId() > 0) {
				keys.add(FIELD_TASK_ID);
				values.add(phraseInfo.getTaskId());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;

			/** auto-increment id is blank until insertion **/
			int id = getLastInsertedId();
			phraseInfo.setId(id);

			return phraseInfo;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<PhraseInfo> getPhrasesByScoreThreshold(int scoreThreshold) {
		String sql = "SELECT * FROM " + tableName + " WHERE " + FIELD_PROOF_SCORE + " >= ? ";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<>(t), scoreThreshold);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<PhraseInfo> getPhrasesByParentIdByScoreThreshold(int parentId, int scoreThreshold) {
		String sql = "SELECT * FROM " + tableName + " WHERE " + FIELD_PARENT_ID + " = ? and "
				+ FIELD_PROOF_SCORE + " >= ? ";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<>(t), parentId, scoreThreshold);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public int updateTaskId(int phraseId, int taskId) {
		if (phraseId <= 0 || taskId <= 0)
			return -1;

		String sql = "UPDATE " + tableName + " SET " + FIELD_TASK_ID + " = ? WHERE " + FIELD_ID + " = ?";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.update(connection, sql, taskId, phraseId);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<PhraseInfo> getByParentId(int parentId) {
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
	
	public int[]  getIdsByParentId(int parentId) {
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


	@Deprecated
	public int deleteByParentIdLargerThan(int id) {
		String sql = "DELETE FROM " + tableName + " WHERE " + FIELD_PARENT_ID + " >= ?";
		try {
			return execute(sql, id);
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<String> getProofStrings() {
		String sql = "SELECT " + FIELD_SYNTAX_TREE + " FROM " + tableName;
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<>(String.class));
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		PhraseDAO pd = new PhraseDAO(DBConnPool.getConnection());
		// int i = pd.deleteByParentIdLargerThan(289500);
		PhraseInfo p = pd.getById(2255);
		System.out.println(p.getProofString());
		System.out.println();
		p.setProofs(Proof.extractProofs(p.getProofString()));
		System.out.println(p.getProofs());
		System.out.println(p.getSyntaxTree());
		System.out.println(p.getText());

		// for (String string : pd.getProofStrings()) {
		// System.out.println(string);
		// }
	}
}
