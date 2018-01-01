package cn.edu.pku.sei.tsr.dragon.outdated;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;


public class DbFactory {

	private Connection conn = null;

	private void getConnection() {
		
		conn = null;
		
		Properties property = new Properties();
		
		String dbUser = Config.getDbUser();
		String dbPass = Config.getDbPass();
		//local database
		property.put("user", dbUser);
		property.put("password",dbPass);
		
		String host = Config.getHost();
		String port = Config.getPort();
		String databaseName = Config.getDatabaseName();
		
		String connectionString = String.format("jdbc:mysql://%s:%s/%s",host,port,databaseName);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(connectionString, property);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DbFactory db = new DbFactory();
		db.getConnection();
		System.out.println(db.conn);
	}

	public ResultSet execSqlWithResult(String sql, Object[] params) {
		ResultSet rs = null;
		try {
			if (conn == null)
				getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			if(params != null)
			{
				//set params
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}
			}
			
			rs = pstmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public boolean execSqlWithoutResult(String sql, Object[] params) {

		int nCount = 0;

		try {
			if (conn == null)
				getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			if(params != null)
			{
				for (int i = 0; i < params.length; i++) {
					pstmt.setObject(i + 1, params[i]);
				}	
			}
			
			nCount = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (nCount > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
