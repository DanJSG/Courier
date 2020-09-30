package com.jsg.postie.libs.nosql;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class MongoConnectionPool {

	private static MongoClient connection;
	private static String databaseName;
	
	private MongoConnectionPool(@Value("${MONGO_CONNECTION_STRING}") String mongoConnectionString,
								@Value("${MONGO_DATABASE_NAME}") String mongoDbName) {
		Builder options = new Builder();
		options.minConnectionsPerHost(5);
		options.connectionsPerHost(200);
		MongoClientURI connectionString = new MongoClientURI(mongoConnectionString, options);
		connection = new MongoClient(connectionString);
		databaseName = mongoDbName;
	}
	
	public static MongoDatabase getDatabase() {
		return connection.getDatabase(databaseName);
	}
	
	public static MongoClient getConnection() {
		return connection;
	}
}
