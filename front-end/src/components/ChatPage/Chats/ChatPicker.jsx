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

    const onChatNamed = (e) => {
        e.preventDefault();
        const chatName = e.target.elements.name.value.trim();
        if(chatName != null && chatName !== "") {
            props.setChatName(chatName, props.id);
        }
    }

    return(
        <React.Fragment>
            {props.created 
                ?
            <li onClick={onChatClick} className={chatStyle}>{props.name}</li>
                :
            <form onSubmit={onChatNamed} className="list-group-item border-left border-right border-light rounded-0">
                <input name="name" className="border-0 w-100" style={{fontStyle: "italic"}} placeholder="New Chat" autoFocus/>
            </form>
            }
        </React.Fragment>
    );
}

export default ChatPicker;