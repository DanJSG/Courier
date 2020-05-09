import React, {useState, useEffect} from 'react';
import ChatList from './Chats/ChatList';
import MessageList from './Messages/MessageList';
import ChatInfo from './Chats/ChatInfo';

function ChatPage(props) {

    const [messages, setMessages] = useState([]);
    const [wsConnection, setWsConnection] = useState(null);
    const [chats, setChats] = useState([]);
    const [currentChat, setCurrentChat] = useState({
        name: "Test",
        members: []
    });

    // called once on mount
    useEffect(() => {
        console.log(props);
        handleConnect()
    }, []);

    // // called on unmount
    // useEffect(() => {
    //     return function cleanup() {
    //         if(wsConnection) {
    //             wsConnection.close();
    //         }
    //     }
    // });

    //called whenever wsConnection changes
    useEffect(() => {
        if(wsConnection) {
            addWebSocketEventListeners();
        }
    }, [wsConnection]);

    const handleConnect = () => {
        console.log(props.id);
        setWsConnection(new WebSocket("ws://local.courier.net:8080/api/ws", [props.id, props.token]));
        console.log("Would now connect");
    }

    const addWebSocketEventListeners = () => {
        console.log(props.id);
        wsConnection.addEventListener("open", () => {
            console.log(`Connection opened with user ID ${props.id}`);
        });
        wsConnection.addEventListener("error", () => {
            console.log(wsConnection);
            alert("Failed to connect to chat room server.")
        })
        wsConnection.addEventListener("message", (e) => {
            if(e.data.charAt(0) !== "`") {
                const receivedMessage = JSON.parse(e.data);
                // console.log("Received message over websocket:")
                // console.log(receivedMessage);
                receivedMessage.timestamp = new Date(receivedMessage.timestamp).toUTCString();
                setMessages(prevMessages => [...prevMessages, receivedMessage]);
                return;
            }
            console.log(e.data);
            const chatMembers = JSON.parse(e.data.slice(1, e.data.length));
            // if session ID is currently null, find entry with 
            setCurrentChat(prevChat => {return {
                name: prevChat.name,
                members: chatMembers
            }});
        });
    }

    const handleSendMessage = (messageText) => {
        if(!wsConnection) {
            return "Could not connect to the chat server.";
        }
        if(!messageText) {
            return "You must enter a message to send.";
        }
        if(!props.id) {
            return "You must enter a email to start sending messages.";
        }
        if(wsConnection.readyState !== wsConnection.OPEN) {
            return "You are not connected to the chat room server.";
        }
        const message = {
            messageText: messageText,
            timestamp: new Date().toUTCString(),
            senderId: props.id,
            sender: props.displayName,
            receiver: "ALL"
        }
        setMessages(prevMessages => [...prevMessages, message]);
        // setMessages(prevMessages => prevMessages.push(message));
        if(wsConnection) {
            wsConnection.send(JSON.stringify(message));
        }
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
