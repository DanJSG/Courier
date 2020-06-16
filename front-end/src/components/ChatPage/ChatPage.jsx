import React, {useState, useEffect} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';
import MessageBuilder from './Messages/MessageBuilder'
import {initializeWebSocket, removeWebSocketListeners, sendMessage} from './services/chatservice';

function ChatPage(props) {

    const [messages, setMessages] = useState([]);
    const [wsConnection, setWsConnection] = useState(null);
    const [chats, setChats] = useState([{name:"Test", id:"5c0f317d-53f9-435e-b537-5a9c48629a83"}]);
    const [currentChat, setCurrentChat] = useState({
        name: "Test",
        members: []
    });

    const updateCurrentChatCallback = (members) => {
        setCurrentChat(prevChat => {return {
            name: prevChat.name,
            members: members
        }});
    }

    const updateMessagesCallback = (message) => {
        setMessages(prevMessages => [...prevMessages, message]);
    }

    const logErrorCallback = (error) => {
        console.log(error);
    }

    // called once on mount
    useEffect(() => {
        setWsConnection(
            initializeWebSocket("ws://local.courier.net:8080/api/v1/ws", 
            props.id, 
            props.token,
            updateMessagesCallback,
            updateCurrentChatCallback,
            logErrorCallback
        ));
        // setChats(prevChats => [...prevChats, {name: "Test", id: "5c0f317d-53f9-435e-b537-5a9c48629a83"}]);
        return () => {
            removeWebSocketListeners(
                wsConnection, 
                props.id, 
                updateMessagesCallback, 
                updateCurrentChatCallback, 
                logErrorCallback);
            wsConnection.close();
        }
    }, []);

    const handleSendMessage = (messageText) => {
        const response = sendMessage(wsConnection, messageText, props.id, props.displayName);
        if(!response) {
            return "An unhandled error occurred";
        }
        if(response.error) {
            return response.error;
        }
        setMessages(prevMessages => [...prevMessages, response.message]);
    }

    return (
    <React.Fragment>
        <div className="container-fluid inherit-height mh-100">
            <div className="row justify-content-center inherit-height">
                <div className="col-2 border">
                    <ChatList chats={chats}></ChatList>
                </div>
                <div className="col-8 border mh-100 inherit-height">
                    <div className="row h-auto ml-3">
                        <h1 className="row">{currentChat.name}</h1>
                    </div>
                    <div className="row mh-100 h-auto overflow-auto">
                        <MessageList handleSendMessage={handleSendMessage} messages={messages} currentChat={currentChat}></MessageList>
                    </div>
                    <div className="row h-auto align-items-end">
                        <MessageBuilder handleSendMessage={handleSendMessage}></MessageBuilder>
                    </div>
                </div>
                <div className="col-2 border">
                    <ChatInfo currentChat={currentChat}></ChatInfo>
                </div>
            </div>
        </div>
    </React.Fragment>
    );

} 

export default ChatPage;
