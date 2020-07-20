import React, {useState, useEffect, useRef} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';
import MessageBuilder from './Messages/MessageBuilder'
import {initializeWebSocket, removeWebSocketListeners, sendMessage} from './services/messageservice';
import {loadAllChats, loadChatHistory, broadcastChats, saveChat, loadChatMembers, broadcastActiveChat} from './services/chatservice'
import {Scrollbars} from 'react-custom-scrollbars'

function ChatPage(props) {

    const messageScrollbar = useRef(null);
    const [messages, setMessages] = useState([]);
    const [wsConnection, setWsConnection] = useState(null);
    const [chats, setChats] = useState(new Map());
    const [currentChat, setCurrentChat] = useState({
        id: null,
        name: null,
        members: []
    });
    const [nameChatInProgress, setNameChatInProgress] = useState(false);
    const [addChatMembersInProgress, setAddChatMembersInProgress] = useState(false);
    const [currentChatIsLoaded, setCurrentChatIsLoaded] = useState(false);
    const [chatMembersAreLoaded, setChatMembersAreLoaded] = useState(false);
    const [chatHistoryIsLoaded, setChatHistoryIsLoaded] = useState(false);
    const [receivedMessage, setReceivedMessage] = useState(null);
    const [activeMembers, setActiveMembers] = useState(null);

    const changeCurrentChat = (id) => {
        let chat = chats.get(id);
        chat.members = [{id: props.id, displayName: props.displayName}];
        setCurrentChat(chat);
        setChatMembersAreLoaded(false);
        setChatHistoryIsLoaded(false);
    }

    const addMembers = (e) => {
        if(!addChatMembersInProgress) return;
        e.preventDefault();
        const membersText = e.target.elements.members.value.trim().split(",");
        const members = membersText.map(member => {return {id: parseInt(member), displayName: member.trim()}});
        setCurrentChat(existingChat => {
            let newChat = {...existingChat};
            newChat.members = [{id: props.id, displayName: props.displayName}, ...members];
            return newChat;
        });
        setAddChatMembersInProgress(false);
    }

    const createChat = () => {
        if(nameChatInProgress) return;
        const newChat = {
            name: "",
            id: Math.floor(Math.random() * 1000).toString(),
            members: [{id: props.id, displayName: props.displayName}],
            created: false
        };
        setChats(prevChats => {
            prevChats.set(newChat.id, newChat);
            return prevChats;
        })
        setNameChatInProgress(true);
        setAddChatMembersInProgress(true);
        setCurrentChat(newChat);
        setMessages([]);
    }

    const setChatName = async(name, id) => {
        if(!nameChatInProgress) return;
        setCurrentChat(existingChat => {
            existingChat.name = name;
            existingChat.id = id;
            return existingChat;
        })
        let updatable = chats.get(id);
        updatable.name = name;
        updatable.created = true;
        chats.set(id, updatable);
        setAddChatMembersInProgress(false);
        setNameChatInProgress(false);
        const chatWithId = await saveChat(name, currentChat.members, props.token);
        if(chatWithId !== null && chatWithId.unauthorized !== null && chatWithId.unauthorized === true) {
            // TODO check for refresh token and fetch new refresh token -> new method in authprovider + app.js
            // TODO if no refresh token, call method in parent (app.js) to logout
        }
        updatable = chats.get(id);
        updatable.id = chatWithId.id;
        chats.delete(id);
        chats.set(updatable.id, updatable);
        setChatMembersAreLoaded(false);
        setCurrentChat(chatWithId);
    }

    const updateCurrentChatCallback = (members) => {
        let activeUserMap = new Map();
        members.forEach(member => {
            activeUserMap.set(member.id, null);
        })
        setActiveMembers(activeUserMap);
    }

    const updateMessagesCallback = (message) => {
        setReceivedMessage(message);
    }

    const logErrorCallback = (error) => {
        console.log(error);
    }

    useEffect(() => {
        async function fetchHistory() {
            const messages = await loadChatHistory(currentChat.id, props.token);
            if(messages !== null && messages.unauthorized !== null && messages.unauthorized === true) {
                // TODO check for refresh token and fetch new refresh token -> new method in authprovider + app.js
                // TODO if no refresh token, call method in parent (app.js) to logout
            }
            setMessages(messages);
        }
        async function fetchMembers() {
            const members = await loadChatMembers(currentChat.id, props.token);
            if(members !== null && members.unauthorized !== null && members.unauthorized === true) {
                // TODO check for refresh token and fetch new refresh token -> new method in authprovider + app.js
                // TODO if no refresh token, call method in parent (app.js) to logout
            }
            if(members == null) {
                console.log("Members is null...");
                console.log(members);
            }
            setCurrentChat(prevChat => {
                return {
                    id: prevChat.id,
                    name: prevChat.name,
                    members: members
                };
            });
        }
        if(currentChat.id != null) {
            if(!currentChatIsLoaded) {
                setCurrentChatIsLoaded(true);
            }
            if(!chatHistoryIsLoaded) {
                fetchHistory();
                setChatHistoryIsLoaded(true);
            }
            if(!chatMembersAreLoaded) {
                broadcastActiveChat(wsConnection, currentChat.id);
                fetchMembers();
                setChatMembersAreLoaded(true);
            }
        }
    }, [currentChat]);

    useEffect(() => {
        if(!wsConnection) return;
        if(!chats || chats.length === 0) return;
        if(currentChat.id != null && !currentChat.id.includes("-")) return;
        broadcastChats(wsConnection, chats);
    }, [chats]);

    useEffect(() => {
        if(!receivedMessage) return;
        if(!currentChat) return;
        if(receivedMessage.chatId === currentChat.id) {
            setMessages(prevMessages => [...prevMessages, receivedMessage]);
        }
        setReceivedMessage(null);
    }, [receivedMessage])

    // called once on mount
    useEffect(() => {
        async function loadChats() {
            const loadedChats = await loadAllChats(props.id, props.token);
            if(messages !== null && messages.unauthorized !== null && messages.unauthorized === true) {
                // TODO check for refresh token and fetch new refresh token -> new method in authprovider + app.js
                // TODO if no refresh token, call method in parent (app.js) to logout
            }
            if(loadedChats == null || loadedChats.length == 0) {
                setCurrentChatIsLoaded(true);
                return;
            }
            let chatsMap = new Map();
            loadedChats.forEach(chat => {
                chatsMap.set(chat.id, chat);
            })
            setChats(chatsMap);
            setCurrentChat(() => {
                return {
                    id: loadedChats[0].id,
                    name: loadedChats[0].name,
                    members: []
                }
            });
        }
        loadChats();
        const ws = initializeWebSocket("ws://local.courier.net:8080/api/v1/ws", props.id, props.token,
                                        updateMessagesCallback, updateCurrentChatCallback, logErrorCallback);
        setWsConnection(ws);
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
        // TODO review whether this is a good idea due to blocking other elements loading
        if(currentChatIsLoaded) {
            messageScrollbar.current.scrollToBottom();
        }
    });

    const handleSendMessage = (messageText) => {
        const response = sendMessage(wsConnection, messageText, props.id, props.displayName, currentChat.id);
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
        {
            !currentChatIsLoaded
            ?
            <div>
                <p>Loading...</p>
            </div>
            :
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
                                <h1 className="pl-3 pr-3">
                                    {currentChat.name === "" || currentChat.name == null ? <i className="text-muted">New Chat</i>: currentChat.name}
                                </h1>
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
                        <ChatInfo activeMembers={activeMembers} currentChat={currentChat}></ChatInfo>
                    </div>
                </div>
            </div>
        }
        </React.Fragment>
    );

} 

export default ChatPage;
