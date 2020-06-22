import React from 'react';

function ChatPicker(props) {

    const defaultChatStyle = "list-group-item border-left border-right border-light rounded-0 chat-hover";
    const chatStyle = props.isCurrentChat ? defaultChatStyle + " chat-selected" : defaultChatStyle;
    
    const onChatClick = () => {
        console.log(props.name);
        console.log(props.id);
        props.changeCurrentChat(props.id);
        // switch chats when this chat is clicked
    }

    return(
        <li onClick={onChatClick} className={chatStyle}>{props.name}</li>
    );
}

export default ChatPicker;