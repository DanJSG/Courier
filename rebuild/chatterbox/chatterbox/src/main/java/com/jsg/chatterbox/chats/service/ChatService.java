package com.jsg.chatterbox.chats.service;

import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.chats.types.EmptyChat;
import com.jsg.chatterbox.chats.types.EmptyChatBuilder;
import com.jsg.chatterbox.libs.httpexceptions.HttpException;
import com.jsg.chatterbox.libs.httpexceptions.InternalServerErrorHttpException;
import com.jsg.chatterbox.libs.httpexceptions.NotFoundHttpException;
import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;
import com.jsg.chatterbox.members.types.Member;
import com.jsg.chatterbox.members.types.MemberBuilder;

import java.util.List;

public class ChatService {

    public static Chat getChat(String chatId) throws HttpException {
        SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        List<EmptyChat> emptyChats = chatRepo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new EmptyChatBuilder());
        List<Member> members = memberRepo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder());
        if (emptyChats == null || members == null)
            throw new NotFoundHttpException("The requested chat could not be found.");
        EmptyChat emptyChat = emptyChats.get(0);
        return new Chat(emptyChat.getId(), emptyChat.getName(), members);
    }

    public static List<EmptyChat> getUsersChats(long userId) throws HttpException {
        SQLRepository<EmptyChat> repo = new MySQLRepository<>(SQLTable.CHATS);
        List<EmptyChat> chats = repo.findWhereEqual(SQLColumn.MEMBER_ID, userId, new EmptyChatBuilder());
        if (chats == null)
            throw new NotFoundHttpException("Failed to find the chats belonging to the requested user.");
        return chats;
    }

    public static void saveChat(Chat chat) throws HttpException {
        SQLRepository<Chat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!chatRepo.save(chat) || !memberRepo.saveMany(chat.getMembers()))
            throw new InternalServerErrorHttpException("Failed to save chat.");
    }

    public static void renameExistingChat(EmptyChat chat) throws HttpException {
        SQLRepository<EmptyChat> repo = new MySQLRepository<>(SQLTable.DETAILS);
        if (!repo.updateWhereEquals(SQLColumn.CHAT_ID, chat.getId(), chat.toSqlMap()))
            throw new InternalServerErrorHttpException("Failed to update chat.");
    }

    public static void deleteExistingChat(String chatId) throws HttpException {
        SQLRepository<EmptyChat> chatRepo = new MySQLRepository<>(SQLTable.DETAILS);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!chatRepo.deleteWhereEquals(SQLColumn.CHAT_ID, chatId) || !memberRepo.deleteWhereEquals(SQLColumn.CHAT_ID, chatId))
            throw new InternalServerErrorHttpException("Failed to delete chat.");
    }

}
