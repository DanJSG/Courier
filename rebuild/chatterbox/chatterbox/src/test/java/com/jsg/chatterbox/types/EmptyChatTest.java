package com.jsg.chatterbox.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsg.chatterbox.Chatterbox;
import com.jsg.chatterbox.chats.types.EmptyChat;
import com.jsg.chatterbox.libs.sql.SQLColumn;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(classes = Chatterbox.class)
@TestPropertySource(locations = "/chatterbox.test.env")
public class EmptyChatTest {

    @Test
    public void emptyChatShouldBeCreatedWithIdAndName() {
        UUID id = UUID.randomUUID();
        String name = "emptyChatShouldBeCreatedWithIdAndName";
        EmptyChat chat = new EmptyChat(id, name);
        assert (chat.getId().equals(id));
        assert (chat.getName().contentEquals(name));
    }

    @Test
    public void emptyChatWithIdAndNameShouldMapToJson() {
        UUID id = UUID.randomUUID();
        String name = "emptyChatWithIdAndNameShouldMapToJson";
        StringBuilder jsonBuilder = new StringBuilder("{\"id\":\"").append(id.toString());
        String json = jsonBuilder.append("\",\"name\":\"").append(name).append("\"}").toString();
        EmptyChat chat = new EmptyChat(id, name);
        assert (chat.writeValueAsString().contentEquals(json));
    }

    @Test
    public void emptyChatWithIdAndNameShouldMapToSqlMap() {
        UUID id = UUID.randomUUID();
        String name = "emptyChatWithIdAndNameShouldMapToSqlMap";
        Map<SQLColumn, Object> map = new HashMap<>(2);
        map.put(SQLColumn.CHAT_ID, id.toString());
        map.put(SQLColumn.NAME, name);
        EmptyChat chat = new EmptyChat(id, name);
        Map<SQLColumn, Object> sqlMap = chat.toSqlMap();
        assert (sqlMap.size() == map.size()) : "Differing numbers of elements in maps.";
        assert (sqlMap.equals(map));
    }

    @Test
    public void emptyChatShouldBeCreatedWithIdAndNullName() {
        UUID id = UUID.randomUUID();
        EmptyChat chat = new EmptyChat(id, null);
        assert (chat.getName() == null);
        assert (chat.getId().equals(id));
    }

    @Test
    public void emptyChatShouldBeCreatedWithGeneratedIdAndName() {
        String name = "emptyChatShouldBeCreatedWithGeneratedIdAndName";
        EmptyChat chat = new EmptyChat(null, name);
        assert (chat.getName().contentEquals(name));
        assert (chat.getId() != null);
    }

    @Test
    public void emptyChatShouldBeCreatedWithGeneratedIdAndNullName() {
        EmptyChat chat = new EmptyChat(null, null);
        assert (chat.getId() != null);
        assert (chat.getName() == null);
    }

    @Test
    public void chatShouldBeCreatedFromJson() {
        UUID id = UUID.randomUUID();
        String name = "chatShouldBeCreatedFromJson";
        StringBuilder jsonBuilder = new StringBuilder("{\"id\":\"").append(id.toString());
        String json = jsonBuilder.append("\",\"name\":\"").append(name).append("\"}").toString();
        ObjectMapper mapper = new ObjectMapper();
        EmptyChat chat = null;
        try {
            chat = mapper.readValue(json, EmptyChat.class);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
        assert (chat != null);
        assert (chat.getName().contentEquals(name));
        assert (chat.getId().equals(id));
    }

    @Test
    public void chatShouldFailToBeCreatedFromJson() {
        String json = "{\"field\":\"value\"}";
        ObjectMapper mapper = new ObjectMapper();
        EmptyChat chat = null;
        try {
            chat = mapper.readValue(json, EmptyChat.class);
            assert (false);
        } catch (Exception e) {
            e.printStackTrace();
            assert (true);
        }
    }

}
