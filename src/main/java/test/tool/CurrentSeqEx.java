package test.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.rdb.dbcp.DBCPConfig;

public class CurrentSeqEx {

	private List<String> messages = new ArrayList<String>();

	public CurrentSeqEx() {
		
		init();

	}

	public void init() {
		
		String DB_URL = DBCPConfig.getProperty("pool.factory.localDB01.url");
		String DB_USER = DBCPConfig.getProperty("pool.factory.localDB01.user");
		String DB_PASS = DBCPConfig.getProperty("pool.factory.localDB01.password");
		Connection conn = null;
		Statement stmt = null;
		ResultSet resultSet = null;
		String symbol = ",";
		try {
			conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			conn.setAutoCommit(true);
			stmt = conn.createStatement();
			StringBuilder query = new StringBuilder();
			query.append(" SELECT * FROM USER_SEQUENCES ORDER BY SEQUENCE_NAME ");
			List<Map <String, String>> seqNames = new ArrayList<Map<String, String>>();
			resultSet = stmt.executeQuery(query.toString());
			while (resultSet.next()) {
				Map <String, String> seqName = new HashMap<String, String>();
				seqName.put(resultSet.getString("SEQUENCE_NAME"), resultSet.getString("LAST_NUMBER"));	
				seqNames.add(seqName);
			}
			resultSet.close();

					
			for (Map <String, String> seqName : seqNames) {
				StringBuilder res = new StringBuilder();	
				for (String key : seqName.keySet()) {
					query = new StringBuilder();
					query.append(" SELECT " + key + ".CURRVAL as CURRENT_VALUE from dual ");
					try {
						resultSet = stmt.executeQuery(query.toString());
						if(resultSet.next()) {
							res.append(key).append(symbol)
								.append(Double.valueOf(resultSet.getString("CURRENT_VALUE")).longValue());
						}
						resultSet.close();
					} catch (Exception e) {
						res.append(key).append(symbol)
							.append(Double.valueOf(seqName.get(key)).longValue() - 1);

					}
					
					messages.add(res.toString());
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public List<String> getMessages() {
		return messages;
	}

}
