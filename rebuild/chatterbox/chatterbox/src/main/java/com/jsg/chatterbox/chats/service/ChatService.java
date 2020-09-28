package com.jsg.chatterbox.chats.service;

import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.chats.types.EmptyChat;
import com.jsg.chatterbox.chats.types.EmptyChatBuilder;
import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;
import com.jsg.chatterbox.types.Member;
import com.jsg.chatterbox.types.MemberBuilder;

import java.util.List;

public class ChatService {

    public static Chat getChat(String chatId) {
        SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        List<EmptyChat> emptyChats = chatRepo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new EmptyChatBuilder());
        List<Member> members = memberRepo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder());
        if (emptyChats == null || members == null)
            return null;
        EmptyChat emptyChat = emptyChats.get(0);
        return new Chat(emptyChat.getId(), emptyChat.getName(), members);
    }

    public static List<EmptyChat> getUsersChats(long userId) {
        SQLRepository<EmptyChat> repo = new MySQLRepository<>(SQLTable.CHATS);
        return repo.findWhereEqual(SQLColumn.MEMBER_ID, userId, new EmptyChatBuilder());
    }

    public static boolean saveChat(Chat chat) {
        SQLRepository<Chat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!chatRepo.save(chat) || !memberRepo.saveMany(chat.getMembers()))
            return false;
        return true;
    }

}
