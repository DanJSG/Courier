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
    this.handleUsernameChange = this.handleUsernameChange.bind(this);
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
    this.setState({loadTime: new Date().toTimeString(), seed: Math.random() * 100});

    this.setState({wsConnection: new WebSocket("ws://localhost:8080/api/ws")}, () => {
      this.state.wsConnection.addEventListener("open", () => {
        console.log("Connection opened...")
      })
      this.state.wsConnection.addEventListener("message", (e) => {
        console.log(e.data);
        const receivedMessage = JSON.parse(e.data);
        if(receivedMessage.id !== this.state.sessionId) {
          this.setState((prevState) => prevState.messages.push(JSON.parse(e.data)));
        }
      });
    })

    // this.state.wsConnection.addEventListener("open", () => {
    //   console.log("Connection opened...")
    // })
    // this.state.wsConnection.addEventListener("message", (e) => {
    //   console.log(e.data);
    //   const receivedMessage = JSON.parse(e.data);
    //   if(receivedMessage.id !== this.state.sessionId) {
    //     this.setState((prevState) => prevState.messages.push(JSON.parse(e.data)));
    //   }
    // });
  }

  componentWillUnmount() {
    if(this.state.wsConnection) {
      this.state.wsConnection.close();
    }
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

  handleUsernameChange(username) {
    this.setState((prevState) => {
      return {
        username: username,
        sessionId: (username + prevState.loadTime + prevState.seed + navigator.userAgent).hashCode().toString()
      }
    });

  }

  render() {
    return (
      <div className="container">
        <ChatList chats={this.state.chats}></ChatList>
        <MessageList handleSendMessage={this.handleSendMessage} messages={this.state.messages}></MessageList>
        <ChatInfo chat={this.state.chats[0]} handleUsernameChange={this.handleUsernameChange}></ChatInfo>
      </div>
    );
  }

}

export default App;
