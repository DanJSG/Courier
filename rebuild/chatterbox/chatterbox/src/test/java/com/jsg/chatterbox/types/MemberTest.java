package com.jsg.chatterbox.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.chatterbox.Chatterbox;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = Chatterbox.class)
@TestPropertySource(locations = "/chatterbox.test.env")
public class MemberTest {

    @Test
    public void memberShouldBeCreatedWithIdAndUsername() {
        long id = 1;
        String username = "createMemberWithIdAndUsername";
        Member member = new Member(id, username);
        assert (member.getId() == id);
        assert (member.getUsername().contentEquals(username));
    }

    @Test
    public void memberShouldBeCreatedWithIdAndNullUsername() {
        long id = 1;
        Member member = new Member(id, null);
        assert (member.getId() == id);
        assert (member.getUsername() == null);
    }

    @Test
    public void memberShouldBeCreatedFromJson() {
        long id = 1;
        String username = "memberShouldBeCreatedFromJson";
        String json = "{\"id\":" + id + ",\"username\":\"" + username + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        Member member = null;
        try {
            member = mapper.readValue(json, Member.class);
        } catch (Exception e) {
            e.printStackTrace();
            assert(false);
        }
        assert (member != null);
        assert (member.getUsername().contentEquals(username));
        assert (member.getId() == id);
    }

    @Test
    public void memberShouldFailToBeCreatedFromJson() {
        String json = "{\"field\":\"value\"}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue(json, Member.class);
            assert (false);
        } catch (Exception e) {
            e.printStackTrace();
            assert(true);
        }
    }

    @Test
    public void memberShouldBeConvertedToJson() {
        long id = 1;
        String username = "memberShouldBeConvertedToJson";
        String json = "{\"id\":" + id + ",\"username\":\"" + username + "\"}";
        Member member = new Member(id, username);
        assert (member.writeValueAsString().contentEquals(json));
    }

    @Test
    public void memberShouldBeConvertedToSqlMap() {
        long id = 1;
        String username = "memberShouldBeConvertedToJson";
        UUID chatId = UUID.randomUUID();
        Map<SQLColumn, Object> map = new HashMap<>();
        map.put(SQLColumn.CHAT_ID, chatId.toString());
        map.put(SQLColumn.MEMBER_ID, id);
        map.put(SQLColumn.USERNAME, username);
        Member member = new Member(id, username);
        member.setAssociatedChatId(chatId);
        Map<SQLColumn, Object> sqlMap = member.toSqlMap();
        assert (sqlMap.equals(map));
    }

}
