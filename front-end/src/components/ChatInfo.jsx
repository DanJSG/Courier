import React from 'react';

function ChatInfo(props) {
    // <input type="text" name="username" onChange={(e) => props.handleUsernameChange(e.target.value)} />
    return(
        <div>
            <h1>{props.currentChat.name}</h1>
            <form onSubmit={props.handleConnect} style={{display: "grid", alignContent: "center"}}>
                <div>
                    Username: &nbsp;
                    <input type="text" name="username"/>
                </div>
                <button> Connect to chat...</button>
            </form>
            <h3>Members:</h3>
            <ul>
                {props.currentChat.members.map((member) => <li key={member.sessionId}>{member.sessionId}</li>)}
            </ul>
        </div>
    );
}

export default ChatInfo;