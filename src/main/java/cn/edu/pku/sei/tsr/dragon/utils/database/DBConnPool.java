package cn.edu.pku.sei.tsr.dragon.utils.database;

import java.sql.Connection;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import cn.edu.pku.sei.tsr.dragon.utils.Config;

public class DBConnPool {
	private static final Logger	logger			= Logger.getLogger(DBConnPool.class);
	private static BoneCP		connectionPool	= null;

	// 96? connections at most???
	public static synchronized void initPool() {
		try {
			logger.info("Initiating DB connection pool....");

			// load the database driver (make sure this is in your classpath!)
			Class.forName(Config.getLocalDBJDBCDriverName());

			// setup the connection pool
			BoneCPConfig connPoolConfig = new BoneCPConfig();

			connPoolConfig.setJdbcUrl(Config.getLocalDBUrl());
			connPoolConfig.setUsername(Config.getLocalDBUserName());
			connPoolConfig.setPassword(Config.getLocalDBPassword());

			connPoolConfig.setMinConnectionsPerPartition(8); // 3
			connPoolConfig.setMaxConnectionsPerPartition(20); // 20
			connPoolConfig.setPartitionCount(4); // 4
			connPoolConfig.setAcquireIncrement(5); // 5
			connPoolConfig.setAcquireRetryAttempts(3);

			connPoolConfig.setAcquireRetryDelayInMs(500);

			// connPoolConfig.setConnectionTimeoutInMs(5000);

			connectionPool = new BoneCP(connPoolConfig);

			logger.info("DB connection pool initiated!");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void shutdownPool() {
		try {
			if (connectionPool == null) {
				logger.error("DB Connection Pool is not started");
				return;
			}
			connectionPool.shutdown();
			logger.info("DB Conn Pool has been shut down.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized Connection getConnection() {
		Connection connection = null;

		try {
			if (connectionPool == null)
				initPool();

			// fetch a connection
			connection = connectionPool.getConnection();
			return connection;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void closeConnection(Connection connection) {
		try {
			DbUtils.close(connection);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 128; i++) {
			System.out.println(i + "\t" + DBConnPool.getConnection());
		}

	}

}
