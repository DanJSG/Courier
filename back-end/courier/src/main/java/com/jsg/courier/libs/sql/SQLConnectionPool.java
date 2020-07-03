package com.jsg.courier.libs.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class SQLConnectionPool {
	
	private static BasicDataSource dataSource = new BasicDataSource();
	
	@Autowired
	private SQLConnectionPool(@Value("${sql.username}") String sqlUsername,
							  @Value("${sql.password}") String sqlPassword,
							  @Value("${sql.connectionstring}") String sqlConnectionString) {		
		dataSource.setUrl(sqlConnectionString);
		dataSource.setUsername(sqlUsername);
		dataSource.setPassword(sqlPassword);
		dataSource.setMaxActive(200);
		dataSource.setMaxIdle(50);
		dataSource.setMinIdle(5);
		dataSource.setMaxOpenPreparedStatements(200);
	}
	
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
}
