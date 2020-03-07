import React from 'react';

function ChatInfo(props) {
    // <input type="text" name="username" onChange={(e) => props.handleUsernameChange(e.target.value)} />
    return(
        <div>
            <h1>AVATAR HERE</h1>
            <form onSubmit={props.handleConnect} style={{display: "grid", alignContent: "center"}}>
                <div>
                    Username: &nbsp;
                    <input type="text" name="username"/>
                </div>
                <button> Connect to chat...</button>
            </form>
            <h3>Members:</h3>
            <ul>
                <li key={props.chat.members.sender}>{props.chat.members.sender}</li>
                {props.chat.members.receivers.map((receiver) => <li key={receiver}>{receiver}</li>)}
            </ul>
        </div>
    );
}

export default ChatInfo;