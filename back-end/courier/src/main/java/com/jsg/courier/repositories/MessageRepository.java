package com.jsg.courier.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.courier.datatypes.Message;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

@Repository
public class MessageRepository implements CustomRepository<Message>{

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
	public List<Message> findAllWhere(String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count(String collectionName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Message item, String collectionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String id, String collectionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(String collectionName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean exists(Message item, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
