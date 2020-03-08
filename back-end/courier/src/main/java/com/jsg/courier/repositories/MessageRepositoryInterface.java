package com.jsg.courier.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.jsg.courier.datatypes.Message;

@Repository
public interface MessageRepositoryInterface extends MongoRepository<Message, String>{
		
}
