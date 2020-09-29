package com.jsg.chatterbox.members.service;

import com.jsg.chatterbox.chats.service.ChatService;
import com.jsg.chatterbox.chats.types.Chat;
import com.jsg.chatterbox.libs.httpexceptions.HttpException;
import com.jsg.chatterbox.libs.httpexceptions.InternalServerErrorHttpException;
import com.jsg.chatterbox.libs.httpexceptions.NotFoundHttpException;
import com.jsg.chatterbox.libs.sql.MySQLRepository;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLRepository;
import com.jsg.chatterbox.libs.sql.SQLTable;
import com.jsg.chatterbox.members.types.Member;
import com.jsg.chatterbox.members.types.MemberBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberService {

    // TODO add HttpException to each of these functions and then handle the exceptions in the rest controllers

    public static Member getMember(long memberId) throws HttpException {
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        List<Member> members = memberSQLRepository.findWhereEqual(SQLColumn.MEMBER_ID, memberId, 0, new MemberBuilder());
        if (members == null)
            throw new NotFoundHttpException("Failed to find member with given ID.");
        return members.get(0);
    }

    public static Member updateMember(Member member) throws HttpException {
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberRepo.updateWhereEquals(SQLColumn.MEMBER_ID, member.getId(), member.toSqlMap()))
            throw new InternalServerErrorHttpException("An error occurred whilst trying to update user " + member.getUsername() + ".");
        return getMember(member.getId());
    }

    public static void deleteMember(long userId) throws HttpException {
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberSQLRepository.deleteWhereEquals(SQLColumn.MEMBER_ID, userId))
            throw new InternalServerErrorHttpException("An error occurred whilst trying to delete user from chat.");
    }

    public static List<Member> getChatMembers(String chatId) throws HttpException {
        SQLRepository<Member> repo = new MySQLRepository<>(SQLTable.CHATS);
        List<Member> members = repo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder());
        if (members == null)
            throw new NotFoundHttpException("Failed to find chat members.");
        return repo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder());
    }

    public static Chat addMemberToChat(String chatId, Member member) throws HttpException {
        member.setAssociatedChatId(chatId);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (memberRepo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder()) == null)
            throw new NotFoundHttpException("Failed to find chat to add member to.");
        if (!memberRepo.save(member))
            throw new InternalServerErrorHttpException("Failed to add chat member to existing chat.");
        return ChatService.getChat(chatId);
    }

    public static void removeMemberFromChat(String chatId, long userId) throws HttpException {
        Map<SQLColumn, Object> removalConditions = new HashMap<>();
        removalConditions.put(SQLColumn.CHAT_ID, chatId);
        removalConditions.put(SQLColumn.MEMBER_ID, userId);
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberSQLRepository.deleteWhereEquals(removalConditions))
            throw new InternalServerErrorHttpException("Failed to remove member from existing chat.");
    }

}
