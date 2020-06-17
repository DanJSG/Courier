import React from 'react';

function Message(props) {
    return(
        <div className="list-group-item p-2">
            <b>{props.message.sender}:</b> {props.message.messageText} <small><i>at {props.message.timestamp}</i></small>
        </div>
    );
}

export default Message;
