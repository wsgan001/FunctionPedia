/**
 * File-Name:CommonDAO.java
 * 
 * Created on 2010-12-21 上午11:08:51
 * 
 * @author: Neo (neolimeng@gmail.com) Software Engineering Institute, Peking
 *          University, China
 * 
 *          Copyright (c) 2009, Peking University
 * 
 * 
 */
package cn.edu.pku.sei.tsr.dragon.utils.database;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

/**
 * Description: Zhuzixiao added following methods: deleteById,deleteByUuid;
 * updateById,UpdateByUuid,update(参数可定制的),updateAll; Two version of
 * getBy(queryKeys, queryValues) 2011-11-27
 * 
 * @author: Neo (neolimeng@gmail.com) Software Engineering Institute, Peking
 *          University, China
 * @version 1.0 2010-12-21 上午11:08:51
 */

public class CommonDAO<T, PK extends Serializable> {
	protected static Logger	log			= Logger.getLogger(CommonDAO.class);
	public static String	FIELD_ID	= "Id";

	protected String		tableName	= null;
	protected Class<T>		t;
	protected Connection	connection;

	public CommonDAO(Class<T> t, Connection connection) {
		if (t == null || connection == null) {
			log.error("[CommonDAO] CommonDAO中传入的参数为null！！");
		}
		else {
			this.t = t;
			tableName = t.getSimpleName().toLowerCase();
			this.connection = connection;
		}
	}

	public CommonDAO(Class<T> t, String tableName, Connection connection) {
		if (t == null || connection == null) {
			log.error("[CommonDAO] CommonDAO中传入的参数为null！！");
		}
		else {
			this.t = t;
			this.connection = connection;
			this.tableName = tableName;
		}
	}

	/**
	 * Description: 保存对象到数据库，通用类。
	 * 
	 * @param keys
	 *            要插入数据的字段
	 * @param values
	 *            要插入的值
	 * @throws SQLException
	 */
	public int insert(Object[] keys, Object[] values) throws SQLException {
		StringBuffer sql = new StringBuffer();
		StringBuffer valuePart = new StringBuffer();

		sql.append("INSERT INTO " + tableName + "(");
		valuePart.append(") VALUES(");

		if (keys.length != values.length) {
			log.error("[CommonDAO] insert方法传入的Key和Value个数不相同！");
			return -1;
		}

		for (int i = 0; i < keys.length; i++) {
			sql.append(keys[i]);
			valuePart.append("?");

			if (i < keys.length - 1) {
				sql.append(", ");
				valuePart.append(", ");
			}
			else {
				sql.append(valuePart.toString() + ")");
			}
		}

		return execute(sql.toString(), values);
	}

	/**
	 * Description: getBy:QueryKeys=QueryValues
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param queryKeys
	 * @param queryValues
	 * @return List<T>
	 */
	public List<T> getBy(Object[] queryKeys, Object[] queryValues) {
		String sql = generateSqlForGetBy(queryKeys, queryValues);

		return find(sql, queryValues);
	}

	public List<T> getBy(String queryKey, Object queryValue) {
		String[] queryKeys = new String[] { queryKey };
		Object[] queryValues = new Object[] { queryValue };

		return getBy(queryKeys, queryValues);
	}

	public T getById(int id) {
		List<T> ts = getBy(FIELD_ID, id);
		if (ts == null) {
			return null;
		}
		else if (ts.size() == 1) {
			return ts.get(0);
		}
		else if (ts.size() < 1) {
			log.error("[Table]" + tableName + " [ID]" + id + " Did not find record of this id！");
		}
		else if (ts.size() > 1) {
			log.error("[Table]" + tableName + " [ID]" + id + " 查询到的个数不唯一，无法得到唯一确定值！");
		}
		return null;
	}

	public T getByUuid(String uuid) {
		List<T> ts = getBy("uuid", uuid);
		if (ts == null) {
			return null;
		}
		else if (ts.size() == 1) {
			return ts.get(0);
		}
		else if (ts.size() <= 0) {
			log.error("[CommonDAO] 未查询到结果！");
			return null;
		}
		else if (ts.size() > 1) {
			log.error("[CommonDAO] 查询到的个数大于1，无法得到唯一确定值！");
			return null;
		}

		return null;
	}

	public List<T> getAll() {
		String sql = "SELECT * FROM " + tableName;
		return find(sql);
	}

	private String generateSqlForGetBy(Object[] queryKeys, Object[] queryValues) {
		StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName);
		StringBuilder whereSubSql = new StringBuilder(" WHERE ");

		if (queryKeys.length != queryValues.length) {
			log.error("[CommonDAO] selectQuery（getBy方法）的参数中Key和Value个数不相同！");
			return null;
		}

		for (int i = 0; i < queryKeys.length; i++) {

			if (i < queryKeys.length - 1) {
				whereSubSql.append(queryKeys[i] + "=? and ");
			}
			else {
				whereSubSql.append(queryKeys[i] + "=? ");
			}
		}
		sql.append(whereSubSql);

		return sql.toString();
	}

	public int execute(String sql, Object... params) throws SQLException {
		Long start = System.currentTimeMillis();
		QueryRunner queryRunner = new QueryRunner();
		// log.info("[CommonDAO] 执行查询:" + sql);
		int resultcode = queryRunner.update(connection, sql, params);
		// log.debug("[CommonDAO] execute方法执行的时间为：" +
		// (System.currentTimeMillis() - start)
		// + "ms。SQL语句为" + sql);
		return resultcode;
	}

	/**
	 * Description: 删除，通过uuid
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param uuid
	 * @throws SQLException
	 *             void
	 */
	public void deleteByUuid(String uuid) throws SQLException {
		String sql = "DELETE FROM " + tableName + " WHERE uuid = ?";
		execute(sql, uuid);
	}

	/**
	 * Description: delete a record by id
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param id
	 * @throws SQLException
	 *             void
	 */
	public void deleteById(int id) throws SQLException {
		String sql = "DELETE FROM " + tableName + " WHERE " + FIELD_ID + " = ?";
		execute(sql, id);
	}

	public int countAll() {
		String sql = "SELECT COUNT(*) FROM " + tableName;
		return count(sql);
	}

	public int count(String sql, Object... params) {
		Long start = System.currentTimeMillis();
		Object result = null;
		ScalarHandler<?> handler = new ScalarHandler<Object>();
		QueryRunner queryRunner = new QueryRunner();

		try {
			result = queryRunner.query(connection, sql, handler, params);
			log.debug("[CommonDAO] count查询到的Entity个数为：" + result + "，执行的时间为："
					+ (System.currentTimeMillis() - start) + "ms。SQL语句为：" + sql);
			return ((Long) result).intValue();
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<T> find(String sql, Object... params) {

		if (sql == null || sql.equals("")) {
			log.error("[CommonDAO] Null sql statement in method: find!!");
			return null;
		}

		Long start = System.currentTimeMillis();
		List<T> entities = null;
		ResultSetHandler<List<T>> handler = new BeanListHandler<T>(t);
		QueryRunner queryRunner = new QueryRunner();

		try {
			// log.info("[CommonDAO] 执行查询：" + sql);
			entities = queryRunner.query(connection, sql, handler, params);
			return entities;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if (entities != null) {
				log.debug("[CommonDAO] find查询到的Entity个数为：" + entities.size() + "，执行的时间为："
						+ (System.currentTimeMillis() - start) + "ms. SQL语句为：" + sql);
			}
			else {
				log.info("[CommonDAO] find未查询到任何Entity!SQL语句为：" + sql);
			}
		}
	}

	/**
	 * 
	 * @Title: columnFind
	 * 
	 * @Description: 按列查询 取出一列的值
	 * 
	 * @param @param
	 *            sql
	 * @param @param
	 *            params
	 * @param @return
	 * 
	 * @return List<T>
	 * 
	 * @author jinyong
	 */

	@SuppressWarnings("unchecked")
	public List<T> columnFind(String sql, Object... params) {

		if (sql == null || sql.equals("")) {
			log.error("Null sql statement in method: find!!");
			return null;
		}

		// long start = System.currentTimeMillis();
		List<T> entities = null;
		ColumnListHandler<?> handler = new ColumnListHandler<Object>();
		QueryRunner queryRunner = new QueryRunner();

		try {
			entities = (List<T>) queryRunner.query(connection, sql, handler, params);

			return entities;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Description: 需要update的属性：keysToSet(属性名的String数组)，对应的值：valuesToSet
	 * update时需要满足的条件（where xx=?）的属性：queryKeys，对应的值：queryValues;
	 * 
	 * 调用示例： update({"name","age"}, {"myNewName",21},{"name"},{"myOldName"});
	 * 
	 * generated sql string: update xxx set name='myNewName', age=21 where
	 * name="myOldName"
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param keysToSet
	 * @param valuesToSet
	 * @param queryKeys
	 * @param queryValues
	 * @throws SQLException
	 *             void
	 */
	public void update(Object[] keysToSet, Object[] valuesToSet, Object[] queryKeys, Object[] queryValues)
			throws SQLException {

		StringBuilder sqlString = new StringBuilder("UPDATE " + tableName);
		StringBuilder setSubSql = new StringBuilder(" SET ");
		StringBuilder whereSubSql = new StringBuilder(" WHERE ");

		if ((keysToSet.length != valuesToSet.length) && (queryKeys.length != queryValues.length)) {
			log.error("[CommonDAO.update(..)] update方法传入的Key和Value个数不相同！");
			return;
		}

		for (int i = 0; i < keysToSet.length; i++) {
			setSubSql.append(keysToSet[i] + " = ? ");

			if (i < keysToSet.length - 1) {
				setSubSql.append(", ");
			}

		}

		for (int i = 0; i < queryKeys.length; i++) {
			whereSubSql.append(queryKeys[i] + " = ? ");

			if (i < queryKeys.length - 1) {
				whereSubSql.append(", ");
			}
		}

		sqlString.append(setSubSql);
		sqlString.append(whereSubSql);

		Object[] params = new Object[valuesToSet.length + queryValues.length];
		System.arraycopy(valuesToSet, 0, params, 0, valuesToSet.length);
		System.arraycopy(queryValues, 0, params, valuesToSet.length, queryValues.length);

		StringBuilder paramString = new StringBuilder();
		for (int i = 0; i < params.length; i++) {
			paramString.append(params[i] + "\t");
		}
		log.info("[CommonDAO.update(..)] The concatenated paramString is :" + paramString);

		execute(sqlString.toString(), params);
	}

	/**
	 * Description: 调用本类定义的update方法。
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param keys
	 * @param values
	 * @param id
	 * @throws SQLException
	 *             void
	 */
	public void updateById(Object[] keys, Object[] values, int id) throws SQLException {
		String[] queryKeys = new String[] { FIELD_ID };
		Object[] queryValues = new Object[] { id };

		update(keys, values, queryKeys, queryValues);
	}

	/**
	 * Description: 调用update方法
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param keys
	 * @param values
	 * @param uuid
	 * @throws SQLException
	 *             void
	 */
	public void updateByUuid(Object[] keys, Object[] values, String uuid) throws SQLException {
		String[] queryKeys = new String[] { "uuid" };
		Object[] queryValues = new Object[] { uuid };

		update(keys, values, queryKeys, queryValues);
	}

	/**
	 * Description: Another version of update. no where conditions, set all
	 * records to new value.
	 * 
	 * @author: 朱子骁 (sei_zzx@126.com)
	 * 
	 * @param keysToSet
	 * @param valuesToSet
	 * @throws SQLException
	 *             void
	 */
	public void updateAll(Object[] keysToSet, Object[] valuesToSet) throws SQLException {

		StringBuilder sqlString = new StringBuilder("UPDATE " + tableName);
		StringBuilder setSubSql = new StringBuilder(" SET ");

		if (keysToSet.length != valuesToSet.length) {
			log.error("[CommonDAO.update(..)] update方法传入的Key和Value个数不相同！");
			return;
		}

		for (int i = 0; i < keysToSet.length; i++) {
			setSubSql.append(keysToSet[i] + " = ? ");

			if (i < keysToSet.length - 1) {
				setSubSql.append(", ");
			}
		}

		sqlString.append(setSubSql);

		execute(sqlString.toString(), valuesToSet);
	}

	public int getLastInsertedId() {
		String sql = "SELECT LAST_INSERT_ID()";
		QueryRunner queryRunner = new QueryRunner();
		try {
			return queryRunner.query(connection, sql, new ScalarHandler<BigInteger>()).intValue();
		}
		catch (SQLException | ClassCastException e) {
			e.printStackTrace();
			return -1;
		}

	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
