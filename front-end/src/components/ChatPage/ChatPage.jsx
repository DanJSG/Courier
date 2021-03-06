import React, {useState, useEffect} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';
import MessageBuilder from './Messages/MessageBuilder';
import ChatHeader from './Chats/ChatHeader';
import {initializeWebSocket, removeWebSocketListeners, sendMessage, authorizeWebsocket} from './services/messageservice';
import {loadAllChats, loadChatHistory, broadcastChats, saveChat, loadChatMembers, broadcastActiveChat} from './services/chatservice'
import {checkAuthorization, requestAccessToken} from '../../services/authprovider';

function ChatPage(props) {
    
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
    const [websocketAuthorized, setWebsocketAuthorized] = useState(false);

    const reAuthorize = async () => {
        let result = await checkAuthorization();
        if(result.authorized === true) {
            return true;
        }
        const refreshToken = localStorage.getItem("ref.tok");
        if(refreshToken == null || refreshToken === undefined) {
            return false;
        }
        const refreshed = await requestAccessToken();
        if(refreshed === false) {
            return false;
        }
        result = await checkAuthorization();
        return result.authorized;
    }

    const changeCurrentChat = (id) => {
        let chat = chats.get(id);
        chat.members = [{id: props.id, displayName: props.displayName}];
        setCurrentChat(chat);
        setChatMembersAreLoaded(false);
        setChatHistoryIsLoaded(false);
    }

    const addMembers = (members) => {
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
        let chatWithId = await saveChat(name, currentChat.members, props.token);
        if(chatWithId != null && chatWithId.unauthorized != null && chatWithId.unauthorized === true) {
            let reAuthorized = await reAuthorize();
            if(reAuthorized) {
                chatWithId = await saveChat(name, currentChat.members, props.token);
            }
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

    const authorizeWebsocketCallback = () => {
        setWebsocketAuthorized(true);
    }

    const logErrorCallback = (error) => {
        console.log(error);
    }

    useEffect(() => {
        async function fetchHistory() {
            let messages = await loadChatHistory(currentChat.id, props.token);
            if((messages !== null && messages.unauthorized !== null) && messages.unauthorized === true) {
                let reAuthorized = await reAuthorize();
                if(reAuthorized) {
                    messages = await loadChatHistory(currentChat.id, props.token);
                }
                if(!reAuthorized || messages == null || (messages.unauthorized != null && messages.unauthorized === true)) {
                    messages = [];
                }
            }
            setMessages(messages);
        }
        async function fetchMembers() {
            let members = await loadChatMembers(currentChat.id, props.token);
            if(members == null) {
                let reAuthorized = await reAuthorize();
                if(reAuthorized) {
                    members = await loadChatMembers(currentChat.id, props.token);
                }
                if(!reAuthorized || members == null) {
                    members = [];
                }
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
            if(!chatMembersAreLoaded && wsConnection.readyState === 1) {
                if(!websocketAuthorized) {
                    authorizeWebsocket(wsConnection, props.token);
                }
                broadcastActiveChat(wsConnection, currentChat.id);
                fetchMembers();
                setChatMembersAreLoaded(true);
            }
        }
    }, [currentChat, websocketAuthorized]);

    useEffect(() => {
        if(!wsConnection) return;
        if(!chats || chats.length === 0) return;
        if(currentChat.id != null && !currentChat.id.includes("-")) return;
        if(wsConnection.readyState !== 1) return;
        if(!websocketAuthorized) return;
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
            if(loadedChats == null || loadedChats.length === 0) {
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
        const ws = initializeWebSocket("ws://local.courier.net:8080/api/v1/ws",
                                       updateMessagesCallback, updateCurrentChatCallback,
                                       logErrorCallback, authorizeWebsocketCallback);
        setWsConnection(ws);
        return () => {
            if(wsConnection) {
                removeWebSocketListeners(
                    wsConnection, 
                    updateMessagesCallback, 
                    updateCurrentChatCallback, 
                    logErrorCallback);
                wsConnection.close();
            }
        }
    }, []);

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
                    <ChatList setChatName={setChatName} changeCurrentChat={changeCurrentChat} currentChat={currentChat} createChat={createChat} chats={chats}></ChatList>
                    <div className="col-7 border pt-2 mh-100 justify-content-between flex-column p-0">
                        <div className="d-flex flex-grow-1 h-100 mh-100 justify-content-between flex-column">
                            <ChatHeader id={props.id} addMembers={addMembers} isAddingMembers={addChatMembersInProgress} chatName={currentChat.name} token={props.token}></ChatHeader>
                            <MessageList id={props.id} handleSendMessage={handleSendMessage} messages={messages} currentChat={currentChat}></MessageList>
                            <MessageBuilder handleSendMessage={handleSendMessage}></MessageBuilder>
                        </div>
                    </div>
                    <ChatInfo activeMembers={activeMembers} currentChat={currentChat}></ChatInfo>
                </div>
            </div>
        }
        </React.Fragment>
    );

} 

export default ChatPage;
