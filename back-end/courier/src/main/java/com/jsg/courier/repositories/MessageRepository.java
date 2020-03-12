package com.jsg.courier.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.Message;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@Repository
public class MessageRepository implements MongoRepository<Message>{

	private MongoClient connection;
	private MongoDatabase database;
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public MessageRepository() {
		this.connection = new MongoClient("localhost", 27017);
		this.database = connection.getDatabase("courier");
	}
	
	@Override
	public void createCollection(String name) {
		database.createCollection(name);
	}
	
	@Override
	public void save(Message item, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		Document document = Document.parse(objectMapper.writeValueAsString(item));
		collection.insertOne(document);
	}

	@Override
	public List<Message> findAll(String collectionName) throws Exception {
		return findAll(collectionName, -1);
	}

	@Override
	public List<Message> findAll(String collectionName, int limit) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		FindIterable<Document> documents;
		if(limit >= 0) {
			documents = collection.find().projection(Projections.excludeId()).limit(limit);
		} else {
			documents = collection.find().projection(Projections.excludeId());
		}
		List<Message> results = new ArrayList<Message>();
		for(Document document : documents) {
			results.add(objectMapper.readValue(document.toJson(), Message.class));
		}
		return results;
	}

	@Override
	public <V>List<Message> findAllWhereEquals(String field, V value, String collectionName) throws Exception {
		BasicDBObject query = new BasicDBObject();
		query.put(field, value);
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		FindIterable<Document> documents = collection.find(query);
		List<Message> results = new ArrayList<Message>();
		for(Document document : documents) {
			results.add(objectMapper.readValue(document.toJson(), Message.class));
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
	public void delete(Message item, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.putAll((BSONObject) BasicDBObject.parse(objectMapper.writeValueAsString(item)));
		collection.deleteOne(query);
	}

	@Override
	public void delete(String id, String collectionName) {
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
	public Boolean exists(Message item, String collectionName) throws Exception {
		MongoCollection<Document> collection = this.database.getCollection(collectionName);
		BasicDBObject query = new BasicDBObject();
		query.putAll((BSONObject) BasicDBObject.parse(objectMapper.writeValueAsString(item)));
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
	
}
