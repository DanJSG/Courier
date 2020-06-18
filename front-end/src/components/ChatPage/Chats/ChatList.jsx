import React from 'react';
import ChatPicker from './ChatPicker';

function ChatList(props) {
    return (
        <div>
            <div className="text-center">
                <h1 className="">Chats</h1>
            </div>
            <ul className="list-group">
                {props.chats.map((chat) => (
                    <ChatPicker name={chat.name} key={chat.id}></ChatPicker>
                ))}
            </ul>
        </div>
    );
}

export default ChatList;
