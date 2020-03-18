import React from 'react';

function ChatInfo(props) {
    return(
        <div>
            <h1>{props.currentChat.name}</h1>
            <h3>Members:</h3>
            <ul>
                {props.currentChat.members.map((member) => <li key={member.sessionId}>{member.id} [{member.sessionId}]</li>)}
            </ul>
        </div>
    );
}

export default ChatInfo;