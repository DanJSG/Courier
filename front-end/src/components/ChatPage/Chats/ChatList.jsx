import React from 'react';
import ChatPicker from './ChatPicker';
import SearchBar from '../Search/SearchBar'

function ChatList(props) {

    const onCreateChatClicked = () => {
        props.createChat();
    }

    return (
        <div>
            <div className="text-center">
                <h1 className="">Chats</h1>
                <div className="row pb-2 justify-content-center">
                    <SearchBar/>
                    <button onClick={onCreateChatClicked} className="btn rounded-circle ml-1 mr-1 pl-2 pr-2"><i className="fa fa-plus"/></button>
                </div>
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
