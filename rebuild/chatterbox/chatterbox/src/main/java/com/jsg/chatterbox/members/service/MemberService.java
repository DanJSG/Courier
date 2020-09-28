package com.jsg.chatterbox.members.service;

import com.jsg.chatterbox.chats.service.ChatService;
import com.jsg.chatterbox.chats.types.Chat;
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

    public static Member getMember(long memberId) {
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        List<Member> members = memberSQLRepository.findWhereEqual(SQLColumn.MEMBER_ID, memberId, 0, new MemberBuilder());
        return members == null ? null : members.get(0);
    }

    public static Member updateMember(Member member) {
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (!memberRepo.updateWhereEquals(SQLColumn.MEMBER_ID, member.getId(), member.toSqlMap()))
            // TODO start using custom exceptions for handling these
            return null;
        return getMember(member.getId());
    }

    public static boolean deleteMember(long userId) {
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        return memberSQLRepository.deleteWhereEquals(SQLColumn.MEMBER_ID, userId);
    }

    public static List<Member> getChatMembers(String chatId) {
        SQLRepository<Member> repo = new MySQLRepository<>(SQLTable.CHATS);
        return repo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder());
    }

    public static Chat addMemberToChat(String chatId, Member member) {
        member.setAssociatedChatId(chatId);
        SQLRepository<Member> memberRepo = new MySQLRepository<>(SQLTable.MEMBERS);
        if (memberRepo.findWhereEqual(SQLColumn.CHAT_ID, chatId, new MemberBuilder()) == null)
            // TODO start using custom exceptions for handling these
            // 404 not found
            return null;
        if (!memberRepo.save(member))
            // TODO start using custom exceptions for handling these
            // 500 internal server error
            return null;
        return ChatService.getChat(chatId);
    }

    public static boolean removeMemberFromChat(String chatId, long userId) {
        Map<SQLColumn, Object> removalConditions = new HashMap<>();
        removalConditions.put(SQLColumn.CHAT_ID, chatId);
        removalConditions.put(SQLColumn.MEMBER_ID, userId);
        SQLRepository<Member> memberSQLRepository = new MySQLRepository<>(SQLTable.MEMBERS);
        return memberSQLRepository.deleteWhereEquals(removalConditions);
    }

}
