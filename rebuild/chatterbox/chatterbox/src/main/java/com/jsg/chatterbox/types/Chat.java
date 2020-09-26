package com.jsg.chatterbox.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

public class Chat extends EmptyChat {

    @JsonProperty
    private List<Member> members;

    @JsonCreator
    public Chat(@JsonProperty @Nullable UUID id, @JsonProperty String name, @JsonProperty List<Member> members) {
        super(id, name);
        this.members = members;
        for(Member member : this.members)
            member.setAssociatedChatId(getId());
    }

    public List<Member> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(getId()).append("] ").append(getName()).append(": {").append(System.lineSeparator());
        int count = 0;
        for(Member member : members) {
            builder.append("\t").append(member.getUsername()).append(" (").append(member.getId()).append(")");
            builder.append(count == members.size() - 1 ? "" : ",").append(System.lineSeparator());
        }
        builder.append("}");
        return builder.toString();
    }
}
