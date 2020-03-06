import React from 'react';
import ChatPicker from './ChatPicker'

function ChatList(props) {
    return (
        <div>
            <h2>Chats</h2>
            <ul>
                {props.chats.map((chat) => (
                    <ChatPicker name={chat.name} key={chat.id}></ChatPicker>
                ))}
            </ul>
        </div>
    );
}

export default ChatList;