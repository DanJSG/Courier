import React from 'react';

function ChatInfo(props) {
    return(
        <div>
            <h1 className="text-center">{props.currentChat.name}</h1>
            <h3>Members:</h3>
            <ul>
                {props.currentChat.members.map((member) => <li key={member.id}>{member.displayName} </li>)}
            </ul>
        </div>
    );
}

export default ChatInfo;