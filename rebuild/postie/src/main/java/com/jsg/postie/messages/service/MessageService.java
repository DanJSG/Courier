package com.jsg.postie.messages.service;

import com.jsg.postie.libs.httpexceptions.HttpException;
import com.jsg.postie.libs.httpexceptions.NotFoundHttpException;
import com.jsg.postie.libs.nosql.MongoRepository;
import com.jsg.postie.libs.nosql.NoSQLRepository;
import com.jsg.postie.messages.types.Message;
import com.jsg.postie.messages.types.MessageBuilder;

import java.util.List;

public class MessageService {

    public static List<Message> getMessages(String chatId, Integer limit) throws HttpException {
        if (limit == null)
            limit = 25;
        NoSQLRepository<Message> repository = new MongoRepository<>();
        List<Message> messages = repository.findAll(chatId, limit, new MessageBuilder());
        if (messages == null || messages.size() == 0)
            throw new NotFoundHttpException("Could not find any messages belonging to a chat with the given ID.");
        return messages;
    }

    public static void saveMessage(String chatId, Message message) {
        NoSQLRepository<Message> repository = new MongoRepository<>();
        repository.save(message, chatId.toString());
    }

}
