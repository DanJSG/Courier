package com.jsg.courier.libs.nosql;

import java.util.ArrayList;
import java.util.List;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

public class MongoRepository<T extends JsonObject> implements NoSQLRepository<T>{
	
	private MongoClient connection;
	private MongoDatabase database;
	
	public MongoRepository(String connectionString, String databaseName) {
		MongoClientURI mongoConnectionString = new MongoClientURI(connectionString);
		this.connection = new MongoClient(mongoConnectionString);
		this.database = connection.getDatabase(databaseName);
	}
	
	@Override
	public void createCollection(String name) {
		database.createCollection(name);
	}

	@Override
	public void save(T item, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		Document document = Document.parse(item.writeValueAsString());
		collection.insertOne(document);
	}

	@Override
	public List<T> findAll(String collectionName, JsonObjectBuilder<T> builder) throws Exception {
		return findAll(collectionName, -1, builder);
	}

	@Override
	public List<T> findAll(String collectionName, int limit, JsonObjectBuilder<T> builder) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		FindIterable<Document> documents;
		if(limit > 0) {
			documents = collection.find().projection(Projections.excludeId()).limit(limit);
		} else {
			documents = collection.find().projection(Projections.excludeId());
		}
		List<T> results = new ArrayList<T>();
		for(Document document : documents) {
			results.add(builder.fromJson(document.toJson()));
		}
		return results;
	}

	@Override
	public <V> List<T> findAllWhereEquals(String field, V value, String collectionName, JsonObjectBuilder<T> builder) throws Exception {
		BasicDBObject query = new BasicDBObject();
		query.put(field, value);
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		FindIterable<Document> documents = collection.find(query);
		List<T> results = new ArrayList<T>();
		for(Document document : documents) {
			results.add(builder.fromJson(document.toJson()));
		}
		return results;
	}

	@Override
	public long count(String collectionName) {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		long count = collection.countDocuments();
		return count;
	}

	@Override
	public void delete(T item, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.putAll((BSONObject) BasicDBObject.parse(item.writeValueAsString()));
		collection.deleteOne(query);		
	}

	@Override
	public void delete(String id, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		collection.deleteOne(query);
	}

	@Override
	public void deleteAll(String collectionName) {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		collection.deleteMany(query);
	}

	@Override
	public Boolean exists(T item, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.putAll((BSONObject) BasicDBObject.parse(item.writeValueAsString()));
		if(collection.countDocuments(query) > 0) {
			return true;
		} 
		return false;
	}

	@Override
	public Boolean exists(String id, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id));
		if(collection.countDocuments(query) > 0) {
			return true;
		} 
		return false;
	}

	@Override
	public void closeConnection() throws Exception {
		this.connection.close();
	}

}
