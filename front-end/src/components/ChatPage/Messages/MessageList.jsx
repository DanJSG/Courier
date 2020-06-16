import React from 'react';
import Message from "./Message";
import MessageBuilder from './MessageBuilder'

function MessageList(props){

    return(
        <React.Fragment>
            
                    <div className="list-group-flush w-100 p-3">
                        {props.messages.map((message) => (
                            <Message message={message} key={message.timestamp + message.sender}></Message>
                        ))}
                    </div>

        </React.Fragment>
    );  
}

// <h1 className="row">{props.currentChat.name}</h1>

// <div className="row align-items-end">
// <MessageBuilder displayName={props.displayName} handleSendMessage={props.handleSendMessage}></MessageBuilder>
// </div>
export default MessageList;