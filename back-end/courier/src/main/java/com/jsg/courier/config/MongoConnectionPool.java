package com.jsg.courier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

@Component
public final class MongoConnectionPool {

	private static MongoClient connection;
	private static String databaseName;
	
	private MongoConnectionPool(@Value("${mongo.connectionstring}") String mongoConnectionString,
								@Value("${mongo.database.name}") String mongoDbName) {
		Builder options = new MongoClientOptions.Builder();
		options.minConnectionsPerHost(5);
		options.connectionsPerHost(200);
		MongoClientURI connectionString = new MongoClientURI(mongoConnectionString, options);
		connection = new MongoClient(connectionString);
		databaseName = mongoDbName;
	}
	
	public static MongoDatabase getDatabase() {
		return connection.getDatabase(databaseName);
	}
}
