package com.jsg.chatterbox.api;

import com.jsg.chatterbox.types.Chat;
import com.jsg.chatterbox.types.Member;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ChatControllerTest {

    @Test
    public void tenChatsShouldBeCreated() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String name = new StringBuilder("JUnit Test User ").append(i).toString();
            members.add(new Member(i, name));
        }
        for (int i = 0; i < 10; i++) {
            String name = new StringBuilder("JUnit Test Chat ").append(i).toString();
            Chat chat = new Chat(null, name, members);
            ChatController.create(chat);
        }
    }

}
