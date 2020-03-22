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
    this.closeConnection = this.closeConnection.bind(this);
    this.logOut = this.logOut.bind(this);
    this.getDisplayName = this.getDisplayName.bind(this);
    console.log("Display name props are: " + props.displayName);
    if(!props.displayName) {
      this.getDisplayName();
    }
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
    // window.addEventListener("beforeunload", this.closeConnection);
    this.handleConnect();
    console.log("Component mounted.");
  }

  componentWillUnmount() {
    this.closeConnection();
  }

  getDisplayName() {
    const xhr = new XMLHttpRequest()
    xhr.addEventListener("loadend", () => {
      if(xhr.status !== 200) {
        console.log("Couldn't get user info from API");
        return;
      }
      const userInfo = JSON.parse(xhr.responseText);
      this.props.updateDisplayName(userInfo.displayName);
    })
    xhr.open("POST", `http://localhost:8080/api/account/findUserInfoById?id=${this.props.id}`);
    xhr.send();
  }

  closeConnection() {
    this.handleSendMessage("Closing the connection...")
    if(this.state.wsConnection) {
      this.state.wsConnection.close();
    }
    return null;
  }

  handleConnect() {
    this.setState({wsConnection: new WebSocket("ws://localhost:8080/api/ws", [this.props.id])}, () => {
        this.addWebSocketEventListeners();
      });
  }

  addWebSocketEventListeners() {
    console.log(this.state.wsConnection);
    this.state.wsConnection.addEventListener("open", () => {
      console.log(`Connection opened with user ID ${this.props.id}`);
    });
    this.state.wsConnection.addEventListener("error", () => {
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

  handleSendMessage(messageText) {
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
