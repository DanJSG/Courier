import React from 'react';

function ChatPicker(props) {
    return(
        <li className="list-group-item border-left border-right border-light rounded-0 chat-hover">{props.name}</li>
    );
}

export default ChatPicker;