package com.jsg.hive.messages.services;

import com.jsg.hive.libs.http.HttpRequestBuilder;
import com.jsg.hive.libs.http.HttpResponse;
import com.jsg.hive.messages.types.Message;
import org.springframework.http.HttpMethod;

public class MessageService {

    public static boolean saveMessage(Message message) {
        // TODO put the URL here into an environment variable
        // TODO add some kind of authorization in here
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder("http://postie:8083/api/v1/chat/chatId/messages.save");
        requestBuilder.setRequestMethod(HttpMethod.POST);
        requestBuilder.setBody(message.writeValueAsString());
        HttpResponse response;
        try {
            response = new HttpResponse(requestBuilder.toHttpURLConnection());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (response.getStatus() != 200)
            return false;
        return true;
    }

}
