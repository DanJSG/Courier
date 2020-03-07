import React from 'react';
import MessageList from './MessageList';
import ChatList from './ChatList';
import ChatInfo from './ChatInfo';
import hashCode from './utilities';
import '../css/App.css'

/* 
 * chats json structure:
 
 chats: [
    {
      name: "Chat 1",
      id: "SOMETHING I'VE NOT DECIDED ON YET",
      members: {
        sender: "SENDER",
        receivers: [
          "RECEIVER 1",
          "RECEIVER 2"
        ]
      }
    }
  ]

 * 
 * */
class App extends React.Component {

  constructor(props) {
    super(props);
    this.handleSendMessage = this.handleSendMessage.bind(this);
    this.handleConnect = this.handleConnect.bind(this);
    this.addWebSocketEventListeners = this.addWebSocketEventListeners.bind(this);
    this.state = {
      messages: [],
      wsConnection: null, //new WebSocket("ws://localhost:8080/api/ws"),
      username: null,
      sessionId: null,
      loadTime: null,
      seed: null,
      chats: [{
        name: "Chat 1",
        id: 'jkhdf',
        members: {
          sender: "SENDER",
          receivers: [
            "RECEIVER 1",
            "Receiver 2"
          ]}}, {
            name: "Group Chat",
            id: 'ijfhdgdf',
            members: {
              sender: "SENDER",
              receivers: [
                "RECEIVER 1",
                "Receiver 2"
              ]
            }
          }
      ],
    }
  }

  componentDidMount() {
    console.log("Component mounted.");
    this.setState({
      loadTime: new Date().toTimeString(),
      seed: Math.random() * 100
    });
  }

  componentWillUnmount() {
    if(this.state.wsConnection) {
      this.state.wsConnection.close();
    }
  }

  handleConnect(e) {
    e.preventDefault();
    const username = e.target.elements.username.value.trim();
    if(!username) {
      // TODO pass error back to ChatInfo component
      console.log("No username provided.")
      return "No username provided.";
    }
    const sessionId = (username + this.state.loadTime + this.state.seed + navigator.userAgent).hashCode().toString();
    this.setState((prevState) => {return {
      wsConnection: new WebSocket("ws://localhost:8080/api/ws", [sessionId]),
      username: username,
      sessionId: sessionId
    }},
      () => this.addWebSocketEventListeners()
    );
  }

  addWebSocketEventListeners() {
    this.state.wsConnection.addEventListener("open", () => {
      console.log(`Connection opened with protocol identifier ${this.state.sessionId}`);
    });
    this.state.wsConnection.addEventListener("message", (e) => {
      console.log(e.data);
      const receivedMessage = JSON.parse(e.data);
      this.setState((prevState) => prevState.messages.push(JSON.parse(e.data)));
      // if(receivedMessage.id !== this.state.sessionId) {
      //   this.setState((prevState) => prevState.messages.push(JSON.parse(e.data)));
      // }
    });
  }

  handleSendMessage(messageText) {
    if(!messageText) {
      return "You must enter a message to send.";
    }
    if(!this.state.username || !this.state.sessionId) {
      return "You must enter a username to start sending messages.";
    }
    const message = {
      messageText: messageText,
      timestamp: new Date().toTimeString(),
      id: this.state.sessionId,
      sender: this.state.username,
      receiver: "ALL"
    }
    console.log(JSON.stringify(message));
    this.setState((prevState) => prevState.messages.push(message));
    if(this.state.wsConnection) {
      this.state.wsConnection.send(JSON.stringify(message));
    }
  }

  render() {
    return (
      <div className="container">
        <ChatList chats={this.state.chats}></ChatList>
        <MessageList handleSendMessage={this.handleSendMessage} messages={this.state.messages}></MessageList>
        <ChatInfo handleConnect={this.handleConnect} chat={this.state.chats[0]} handleUsernameChange={this.handleUsernameChange}></ChatInfo>
      </div>
    );
  }

}

export default App;
