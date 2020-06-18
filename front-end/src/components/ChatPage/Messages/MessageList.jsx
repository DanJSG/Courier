import React from 'react';
import Message from "./Message";
import MessageBuilder from './MessageBuilder'

function MessageList(props){

    return(
        <React.Fragment>
                    <div className="bg-transparent list-group-flush w-100 pl-3 pr-3">
                        {props.messages.map((message) => {
                            return (<Message isSender={props.id === message.senderId} message={message} key={Math.random() * 10000}></Message>)
                        })}
                    </div>
        </React.Fragment>
    );  
}

export default MessageList;
