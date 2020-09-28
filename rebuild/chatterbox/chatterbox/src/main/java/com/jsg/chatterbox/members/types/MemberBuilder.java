package com.jsg.chatterbox.members.types;

import com.jsg.chatterbox.libs.sql.SQLColumn;
import com.jsg.chatterbox.libs.sql.SQLEntityBuilder;

import java.sql.ResultSet;

public class MemberBuilder implements SQLEntityBuilder<Member> {

    @Override
    public Member fromResultSet(ResultSet sqlResults) {
        try {
            long id = sqlResults.getLong(SQLColumn.MEMBER_ID.toString());
            String username = sqlResults.getString(SQLColumn.USERNAME.toString());
            return new Member(id, username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
