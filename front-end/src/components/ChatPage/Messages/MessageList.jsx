import React from 'react';
import Message from "./Message";
import MessageBuilder from './MessageBuilder'

function MessageList(props){

    return(
        <React.Fragment>
                    <div className="list-group-flush w-100 pl-3 pr-3">
                        {props.messages.map((message) => (
                            <Message message={message} key={message.timestamp + message.sender}></Message>
                        ))}
                    </div>
        </React.Fragment>
    );  
}

export default MessageList;
