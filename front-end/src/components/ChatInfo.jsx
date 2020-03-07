import React from 'react';

function ChatInfo(props) {
    return(
        <div>
            <h1>AVATAR HERE</h1>
            Username: &nbsp;
            <input type="text" name="username" onChange={(e) => props.handleUsernameChange(e.target.value)} />
            <h3>Members:</h3>
            <ul>
                <li key={props.chat.members.sender}>{props.chat.members.sender}</li>
                {props.chat.members.receivers.map((receiver) => <li key={receiver}>{receiver}</li>)}
            </ul>
        </div>
    );
}

export default ChatInfo;