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
    const [chats, setChats] = useState([{name:"Test", id:"5c0f317d-53f9-435e-b537-5a9c48629a83", created: true}]);
    const [currentChat, setCurrentChat] = useState({
        name: "Test",
        id: "5c0f317d-53f9-435e-b537-5a9c48629a83",
        members: [],
    });
    const [nameChatInProgress, setNameChatInProgress] = useState(false);
    const [addChatMembersInProgress, setAddChatMembersInProgress] = useState(false);

    const changeCurrentChat = (id) => {
        // review if we can optimise this -> linear search not ideal
        // stop using an array and start using json like a hash table -> best case O(1) access time
        chats.forEach(currChat => {
            if(currChat.id === id) {
                // modify with code to lookup chat members
                currChat.members = [{id: props.id, displayName: props.displayName}];
                setCurrentChat(currChat);
            }
        })
    }

    const addMembers = (e) => {
        if(!addChatMembersInProgress) {
            return;
        }
        e.preventDefault();
        const membersText = e.target.elements.members.value.trim().split(",");
        const members = membersText.map(member => {return {id: parseInt(member), displayName: member.trim()}});
        console.log(currentChat.id);
        setCurrentChat(existingChat => {
            let newChat = {...existingChat};
            newChat.members = [{id: props.id, displayName: props.displayName}, ...members];
            return newChat;
        });
        setAddChatMembersInProgress(false);
    }

    const createChat = () => {
        if(nameChatInProgress) {
            return;
        }
        const newChat = {
            name: "",
            id: Math.floor(Math.random() * 1000).toString(),
            members: [{id: props.id, displayName: props.displayName}],
            created: false
        };
        setChats(prevChats => [newChat, ...prevChats]);
        setNameChatInProgress(true);
        setAddChatMembersInProgress(true);
        setCurrentChat(newChat);
        setMessages([]);
    }

    const setChatName = (name, id) => {
        if(!nameChatInProgress) {
            return;
        }
        setCurrentChat(existingChat => {
            existingChat.name = name;
            existingChat.id = id;
            return existingChat;
        })
        for(let i=0; i < chats.length; i++) {
            if(chats[i].id === currentChat.id) {
                setChats(prevChats => {
                    let newChats = prevChats;
                    newChats[i].name = name;
                    newChats[i].created = true;
                    return newChats;
                })
            }
        }
        setAddChatMembersInProgress(false);
        setNameChatInProgress(false);
    }

    const updateCurrentChatCallback = (members) => {
        setCurrentChat(prevChat => {return {
            name: prevChat.name,
            id: prevChat.id,
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
                <div className="col-3 border pt-2 pl-0 pr-0 mh-100">
                    <ChatList setChatName={setChatName} changeCurrentChat={changeCurrentChat} currentChat={currentChat} createChat={createChat} chats={chats}></ChatList>
                </div>
                <div className="col-7 border pt-2 mh-100 justify-content-between flex-column p-0">
                    <div className="d-flex flex-grow-1 h-100 mh-100 justify-content-between flex-column">
                        {
                            addChatMembersInProgress
                            ?
                            <form className="list-group-item border-0 rounded-0" onSubmit={addMembers}>
                                <label>To:&nbsp;</label>
                                <input name="members" className="border-0"/>
                            </form>
                            :
                            <h1 className="pl-3 pr-3">{currentChat.name}</h1>
                        }
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
