import React from 'react';
import Message from "./Message";
import MessageBuilder from './MessageBuilder'

function MessageList(props){
    return(
        <div className="message-list">
            <h2>Messages</h2>
            <ul>
                {props.messages.map((message) => (
                    <Message message={message} key={message.timestamp + message.sender}></Message>
                ))}
            </ul>
            <MessageBuilder handleSendMessage={props.handleSendMessage}></MessageBuilder>
        </div>
    );  
}

export default MessageList;