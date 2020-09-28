package com.jsg.chatterbox.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.chatterbox.Chatterbox;
import com.jsg.chatterbox.types.Chat;
import com.jsg.chatterbox.types.Member;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SpringBootTest(classes = Chatterbox.class)
@TestPropertySource(locations = "/chatterbox.test.env")
public class ChatControllerTest {

    @Test
    public void chatShouldBeCreatedWithFiveUsers() {
        List<Member> members = new ArrayList<>();
        String chatName = Calendar.getInstance().toInstant().toString();
        for (int i = 0; i < 5; i++) {
            String name = "JUnit Test User " + i;
            members.add(new Member(i, name));
        }
        Chat chat = new Chat(null, chatName, members);
        ResponseEntity<String> response = ChatController.create(chat);
        assert (response.getStatusCode() == HttpStatus.OK) : "HTTP status code is not 200 OK.";
        assert (response.hasBody()) : "HTTP response does not have a body.";
        Chat responseChat = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            responseChat = mapper.readValue(response.getBody(), Chat.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failure whilst deserializing: " + response.getBody());
            assert (false);
        }
        assert (responseChat != null) : "Response body does not contain a valid chat.";
        assert (responseChat.getId() != null) : "Received chat does not have an ID.";
        assert (responseChat.getName().equals(chatName)) : "Received chat's name is not equal to the saved name.";
    }

    @Test
    public void chatShouldFailToCreateWithNoBody() {
        ResponseEntity<String> response = ChatController.create(null);
        assert (response.getStatusCode() == HttpStatus.BAD_REQUEST) : "HTTP status code is not 400 Bad Request.";
    }

    @Test
    public void chatShouldFailToCreateWithNoMembers() {
        Chat chat = new Chat(null, "chatShouldFailToCreateWithNoMembers", null);
        ResponseEntity<String> response = ChatController.create(chat);
        assert (response.getStatusCode() == HttpStatus.BAD_REQUEST) : "HTTP status code is not 400 Bad Request.";
    }

    @Test
    public void chatShouldFailToCreateDueToNameLength() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String name = "JUnit Test User " + i;
            members.add(new Member(i, name));
        }
        Chat chat = new Chat(null, RandomString.make(260), members);
        ResponseEntity<String> response = ChatController.create(chat);
        assert (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) : "The request succeeded when it should not have.";
    }

}
