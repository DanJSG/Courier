import React, {useState, useEffect, useRef} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';
import MessageBuilder from './Messages/MessageBuilder'
import {initializeWebSocket, removeWebSocketListeners, sendMessage, broadcastChats} from './services/chatservice';
import {Scrollbars} from 'react-custom-scrollbars'

function ChatPage(props) {

    const messageScrollbar = useRef(null);
    const [messages, setMessages] = useState([]);
    const [wsConnection, setWsConnection] = useState(null);
    const [chats, setChats] = useState([]);
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

    const changeCurrentChat = (id) => {
        // TODO review if we can optimise -> linear search not ideal
        // stop using an array and start using json like a hash table -> best case O(1) access time
        chats.forEach(currChat => {
            if(currChat.id === id) {
                // modify with code to lookup chat members
                currChat.members = [{id: props.id, displayName: props.displayName}];
                setCurrentChat(currChat);
            }
        });
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
        setChats(prevChats => [newChat, ...prevChats]);
        setNameChatInProgress(true);
        setAddChatMembersInProgress(true);
        setCurrentChat(newChat);
        setMessages([]);
    }

    const setChatName = (name, id) => {
        if(!nameChatInProgress) return;
        setCurrentChat(existingChat => {
            existingChat.name = name;
            existingChat.id = id;
            return existingChat;
        })
        // TODO review optimisation -> linear search currently used
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
        saveChat(name);
    }

    // TODO refactor into separate file
    // possibly move existing chat service into something such as
    // message service and create a separate service file for this stuff
    const saveChat = (name) => {
        const chat = {
            id: null,
            name: name,
            members: currentChat.members.map(member => member.id),
        }
        fetch("http://local.courier.net:8080/api/v1/chat/create", {
            method: "POST",
            credentials: "include",
            headers: {
                "Authorization": `Bearer ${props.token}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(chat)
        })
        .then(response => {
            if(response.status !== 200) {
                return null;
            }
            return response.json();
        })
        .then(json => {
            if(!json) {
                return null;
            }
            setCurrentChat(existingChat => {
                let newChat = {...existingChat};
                newChat.id = json.id;
                return newChat;
            })
        })
        .catch(error => {
            console.log(error);
        })
        
    }

    // TODO refactor into separate file
    const loadAllChats = () => {
        const url = `http://local.courier.net:8080/api/v1/chat/getAll?id=${props.id}`;
        fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Authorization": `Bearer ${props.token}`,
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if(response.status !== 200) {
                return null;
            }
            return response.json();
        })
        .then(json => {
            if(!json) {
                return null;
            }
            const loadedChats = json.map(receivedChat => {
                return {
                    id: receivedChat.id,
                    name: receivedChat.name,
                    created: true
                }
            });
            setCurrentChat(() => {
                return {
                    id: loadedChats[0].id,
                    name: loadedChats[0].name,
                    members:[]
                };
            })
            setChats(loadedChats);
        })
        .catch(error => {
            console.log(error);
        })
    }

    // TODO refactor into separate file
    const loadCurrentChatHistory = () => {
        const url = `http://local.courier.net:8080/api/v1/messages/getAll?chatId=${currentChat.id}`;
        fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Authorization": `Bearer ${props.token}`,
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if(response.status === 204) {
                setChats([]);
                return null;
            } else if(response.status !== 200) {
                return null;
            }
            return response.json();
        })
        .then(json => {
            if(!json) {
                return null;
            }
            const dateFixedJson = json.map(message => {
                message.timestamp = (new Date(message.timestamp)).toUTCString();
                return message;
            })
            setMessages(dateFixedJson);
        })
        .catch(error => {
            console.log(error);
        })
    }

    // TODO refactor into separate file
    const loadCurrentChatMembers = () => {
        const url = `http://local.courier.net:8080/api/v1/chat/getMembers?chatId=${currentChat.id}`;
        fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Authorization": `Bearer ${props.token}`,
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if(response.status !== 200) {
                return null;
            }
            return response.json();
        })
        .then(json => {
            if(!json) {
                return null;
            }
            setCurrentChat(prevChat => {
                return {
                    id: prevChat.id,
                    name: prevChat.name,
                    members: json
                }
            })
        })
    }

    const updateCurrentChatCallback = (members) => {
        // old method for loading users
        // TODO modify and reuse to determine which users are currently online
        // setCurrentChat(prevChat => {return {
        //     name: prevChat.name,
        //     id: prevChat.id,
        //     members: members
        // }});
    }

    const updateMessagesCallback = (message) => {
        setReceivedMessage(message);
    }

    const logErrorCallback = (error) => {
        console.log(error);
    }

    useEffect(() => {
        if(currentChat.id != null) {
            if(!currentChatIsLoaded) {
                setCurrentChatIsLoaded(true);
            }
            if(!chatHistoryIsLoaded) {
                loadCurrentChatHistory();
                setChatHistoryIsLoaded(true);
            }
            if(!chatMembersAreLoaded) {
                loadCurrentChatMembers();
                setChatMembersAreLoaded(true);
            }
        }
    }, [currentChat]);

    useEffect(() => {
        if(!wsConnection) return;
        if(!chats || chats.length === 0) return;
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
        setWsConnection(
            initializeWebSocket(
                "ws://local.courier.net:8080/api/v1/ws", 
                props.id, 
                props.token,
                updateMessagesCallback,
                updateCurrentChatCallback,
                logErrorCallback
        ));
        loadAllChats();
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
                    <ChatInfo currentChat={currentChat}></ChatInfo>
                </div>
            </div>
        </div>
    }
    </React.Fragment>
    );

} 

export default ChatPage;
