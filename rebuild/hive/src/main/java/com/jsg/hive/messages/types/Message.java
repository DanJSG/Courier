package com.jsg.hive.messages.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jsg.hive.helpers.MongoDateDeserializer;
import com.jsg.hive.types.JsonObject;

import java.util.Date;
import java.util.UUID;

public class Message implements JsonObject {

    @JsonProperty
    private String messageText;

    @JsonProperty
    @JsonDeserialize(using = MongoDateDeserializer.class)
    private Date timestamp;

    @JsonProperty
    private long senderId;

    @JsonProperty
    private String sender;

    @JsonProperty
    private String receiver;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID chatId;

    @JsonCreator
    private Message() {}

    public UUID getChatId() {
        return this.chatId;
    }

    @Override
    public String toString() {
        return writeValueAsString();
    }

    @Override
    public String writeValueAsString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
