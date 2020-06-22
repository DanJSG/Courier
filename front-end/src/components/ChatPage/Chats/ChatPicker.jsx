import React from 'react';

function ChatPicker(props) {

    const onChatClick = () => {
        console.log(props.name);
        // switch chats when this chat is clicked
    }

    return(
        <li onClick={onChatClick} className="list-group-item border-left border-right border-light rounded-0 chat-hover">{props.name}</li>
    );
}

export default ChatPicker;