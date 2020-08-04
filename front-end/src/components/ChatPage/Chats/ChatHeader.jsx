import React from 'react';

function ChatHeader(props) {
    
    const addMembers = (e) => {
        if(!props.isAddingMembers) return;
        e.preventDefault();
        const membersText = e.target.elements.members.value.trim().split(",");
        const members = membersText.map(member => {return {id: parseInt(member), displayName: member.trim()}});
        props.addMembers(members);
    }

    return(
        props.isAddingMembers
        ?
        <form className="list-group-item border-0 rounded-0" onSubmit={addMembers}>
            <label>To:&nbsp;</label>
            <input name="members" className="border-0"/>
        </form>
        :
        <h1 className="pl-3 pr-3">
            {props.chatName === "" || props.chatName == null ? <i className="text-muted">New Chat</i>: props.chatName}
        </h1>
    );

}

export default ChatHeader;