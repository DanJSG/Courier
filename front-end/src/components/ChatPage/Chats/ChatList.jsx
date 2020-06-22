import React from 'react';
import ChatPicker from './ChatPicker';
import SearchBar from '../Search/SearchBar'

function ChatList(props) {
    return (
        <div>
            <div className="text-center">
                <h1 className="">Chats</h1>
                <SearchBar/>
            </div>
            <ul className="list-group border-top">
                {props.chats.map((chat) => (
                    <ChatPicker name={chat.name} key={chat.id}></ChatPicker>
                ))}
            </ul>
        </div>
    );
}

export default ChatList;
