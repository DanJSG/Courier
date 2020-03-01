import React from 'react';
import Message from "./Message";

function MessageList(props) {
    return(
        <div>
            <h2>Messages</h2>
            <ul>
                {props.messages.map((message) => (
                    <Message message={message} key={message.timestamp + message.sender}></Message>
                ))}
            </ul>
        </div>
    );
}

export default MessageList;