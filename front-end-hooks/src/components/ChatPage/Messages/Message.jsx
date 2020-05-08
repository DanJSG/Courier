import React from 'react';

function Message(props) {
    return(
        <li>
            <b>{props.message.sender}:</b> {props.message.messageText} <small><i>at {props.message.timestamp}</i></small>
        </li>
    );
}

export default Message;