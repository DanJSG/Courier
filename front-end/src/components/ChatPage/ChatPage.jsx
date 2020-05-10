import React, {useState, useEffect} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';
import {initializeWebSocket, removeWebSocketListeners, sendMessage} from './services/chatservice';

function ChatPage(props) {

    const [messages, setMessages] = useState([]);
    const [wsConnection, setWsConnection] = useState(null);
    const [chats] = useState([]);
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
            initializeWebSocket("ws://local.courier.net:8080/api/ws", 
            props.id, 
            props.token,
            updateMessagesCallback,
            updateCurrentChatCallback,
            logErrorCallback
        ));
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
        setMessages(prevMessages => [...prevMessages, response.message]);
    }

    return (
    <React.Fragment>
        <div className="container">
        <ChatList chats={chats}></ChatList>
        <MessageList handleSendMessage={handleSendMessage} messages={messages}></MessageList>
        <ChatInfo currentChat={currentChat}></ChatInfo>
        </div>
    </React.Fragment>
    );

} 

export default ChatPage;
