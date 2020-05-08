import React, {useState, useEffect} from 'react';
import MessageList from './MessageList';
import ChatList from './ChatList';
import ChatInfo from './ChatInfo';
import './utilities';
import '../css/MainPage.css';

function MainPage2(props) {

    [messages, setMessages] = useState([]);
    [wsConnection, setWsConnection] = useState(null);
    [chats, setChats] = useState([]);
    [currentChat, setCurrentChat] = useState({
        name: "",
        members: []
    });

    // called once on mount
    useEffect(() => handleConnect(), []);

    // called on unmount
    useEffect(() => {
        return function cleanup() {
            if(wsConnection) {
                new wsConnection.close();
            }
        }
    });

    //called whenever wsConnection changes
    useEffect(() => {
        addWebSocketEventListeners();
    }, [wsConnection]);

    const handleConnect = () => {
        setWsConnection(new WebSocket("ws://localhost:8080/api/ws", [this.props.id, this.props.token]));
    }

    addWebSocketEventListeners() {
        this.state.wsConnection.addEventListener("open", () => {
        console.log(`Connection opened with user ID ${this.props.id}`);
        });
        this.state.wsConnection.addEventListener("error", () => {
        console.log(this.state.wsConnection);
        alert("Failed to connect to chat room server.")
        })
        this.state.wsConnection.addEventListener("message", (e) => {
        if(e.data.charAt(0) !== "`") {
            const receivedMessage = JSON.parse(e.data);
            // console.log("Received message over websocket:")
            // console.log(receivedMessage);
            receivedMessage.timestamp = new Date(receivedMessage.timestamp).toUTCString();
            this.setState((prevState) => prevState.messages.push(receivedMessage));
            return;
        }
        const chatMembers = JSON.parse(e.data.slice(1, e.data.length));
        // if session ID is currently null, find entry with 
        console.log(chatMembers);
        this.setState((prevState) => {
            return {
            currentChat: {
                name: prevState.currentChat.name,
                members: chatMembers
            }
            }
        }) 
        });
    }

    const handleSendMessage = (messageText) => {
        if(!messageText) {
            return "You must enter a message to send.";
        }
        if(!this.props.id) {
            return "You must enter a email to start sending messages.";
        }
        if(this.state.wsConnection.readyState !== this.state.wsConnection.OPEN) {
            return "You are not connected to the chat room server.";
        }
        const message = {
            messageText: messageText,
            timestamp: new Date().toUTCString(),
            senderId: this.props.id,
            sender: this.props.displayName,
            receiver: "ALL"
        }
        this.setState((prevState) => prevState.messages.push(message));
        if(this.state.wsConnection) {
            this.state.wsConnection.send(JSON.stringify(message));
        }
    }

    return (
      <React.Fragment>
        <div className="container">
          <ChatList chats={chats}></ChatList>
          <MessageList handleSendMessage={handleSendMessage} messages={messages}></MessageList>
          <ChatInfo currentChat={this.state.currentChat}></ChatInfo>
        </div>
      </React.Fragment>
    );

}

export default MainPage2;
