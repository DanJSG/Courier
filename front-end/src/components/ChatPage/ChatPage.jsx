import React, {useState, useEffect, useRef} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';
import MessageBuilder from './Messages/MessageBuilder'
import {initializeWebSocket, removeWebSocketListeners, sendMessage} from './services/chatservice';
import {Scrollbars} from 'react-custom-scrollbars'

function ChatPage(props) {

    const messageScrollbar = useRef(null);
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

    useEffect(() => {
        // review whether this is a good idea due to blocking other elements loading
        messageScrollbar.current.scrollToBottom();
    })

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
                <div className="col-3 border pt-2 pl-0 pr-0">
                    <ChatList chats={chats}></ChatList>
                </div>
                <div className="col-7 border pt-2 mh-100 justify-content-between flex-column p-0">
                    <div className="d-flex flex-grow-1 h-100 mh-100 justify-content-between flex-column">
                        <h1 className="pl-3 pr-3 bg-light">{currentChat.name}</h1>
                            <div className="h-100 w-100 mh-100 flex-grow-1 border-top" style={{backgroundColor: "rgba(228, 229, 233, 0.4)"}}>
                                <Scrollbars ref={messageScrollbar}>
                                    <MessageList id={props.id} handleSendMessage={handleSendMessage} messages={messages} currentChat={currentChat}></MessageList>
                                </Scrollbars>
                            </div>
                        <MessageBuilder handleSendMessage={handleSendMessage}></MessageBuilder>
                    </div>
                </div>
                <div className="col-2 border pt-2">
                    <ChatInfo currentChat={currentChat}></ChatInfo>
                </div>
            </div>
        </div>
    </React.Fragment>
    );

} 

export default ChatPage;
