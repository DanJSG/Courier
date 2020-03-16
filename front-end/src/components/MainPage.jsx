import React from 'react';
import MessageList from './MessageList';
import ChatList from './ChatList';
import ChatInfo from './ChatInfo';
import './utilities';
import '../css/MainPage.css';

class MainPage extends React.Component {

  constructor(props) {
    super(props);
    this.handleSendMessage = this.handleSendMessage.bind(this);
    this.handleConnect = this.handleConnect.bind(this);
    this.addWebSocketEventListeners = this.addWebSocketEventListeners.bind(this);
    this.logOut = this.logOut.bind(this);
    this.state = {
      messages: [],
      wsConnection: null,
      currentChat: {
        name: "Chat Room",
        members: []
      },
      chats: [],
    }
  }

  componentDidMount() {
    this.handleConnect();
    console.log("Component mounted.");
  }

  componentWillUnmount() {
    if(this.state.wsConnection) {
      this.state.wsConnection.close();
    }
  }

  handleConnect() {
    this.setState({wsConnection: new WebSocket("ws://localhost:8080/api/ws", [this.props.sessionId, this.props.email])}, () => {
      this.addWebSocketEventListeners();
    });
  }

  addWebSocketEventListeners() {
    console.log(this.state.wsConnection);
    this.state.wsConnection.addEventListener("open", () => {
      console.log(`Connection opened with protocol identifier ${this.props.sessionId}`);
    });
    this.state.wsConnection.addEventListener("error", () => {
      alert("Failed to connect to chat room server.")
    })
    this.state.wsConnection.addEventListener("message", (e) => {
      if(e.data.charAt(0) !== "`") {
        const receivedMessage = JSON.parse(e.data);
        console.log("Received message over websocket:")
        console.log(receivedMessage);
        receivedMessage.timestamp = new Date(receivedMessage.timestamp).toUTCString();
        this.setState((prevState) => prevState.messages.push(receivedMessage));
        return;
      }
      const chatMembers = JSON.parse(e.data.slice(1, e.data.length));
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

  handleSendMessage(messageText) {
    if(!messageText) {
      return "You must enter a message to send.";
    }
    if(!this.props.email || !this.props.sessionId) {
      return "You must enter a email to start sending messages.";
    }
    if(this.state.wsConnection.readyState !== this.state.wsConnection.OPEN) {
      return "You are not connected to the chat room server.";
    }
    const message = {
      messageText: messageText,
      timestamp: new Date().toUTCString(),
      sessionId: this.props.sessionId,
      sender: this.props.email,
      receiver: "ALL"
    }
    this.setState((prevState) => prevState.messages.push(message));
    if(this.state.wsConnection) {
      this.state.wsConnection.send(JSON.stringify(message));
    }
  }

  logOut() {
    this.props.updateAuthorization(false);
    localStorage.clear();
  }

  render() {
    console.log("In MainPage: " + localStorage.getItem("loggedIn"));
    return (
      <React.Fragment>
        <div className="container">
          <ChatList chats={this.state.chats}></ChatList>
          <MessageList handleSendMessage={this.handleSendMessage} messages={this.state.messages}></MessageList>
          <ChatInfo currentChat={this.state.currentChat}></ChatInfo>
        </div>
        <footer className="footer">
          <button onClick={this.logOut} style={{width: "20%", justifyContent: "middle"}}>Log Out</button>
        </footer>
      </React.Fragment>
    );
  }
}

export default MainPage;
