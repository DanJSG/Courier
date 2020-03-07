import React from 'react';
import MessageList from './MessageList';
import ChatList from './ChatList';
import ChatInfo from './ChatInfo';
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
    this.state = {
      messages: [],
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
      wsConnection: new WebSocket("ws://localhost:8080/api/ws")
    }
  }

  handleSendMessage(message) {
    if(!message.messageText) {
      return "You must enter a message to send.";
    }
    this.setState((prevState) => prevState.messages.push(message));
    if(!this.state.wsConnection) {
      return;
    }
    this.state.wsConnection.send(JSON.stringify(message));
  }

  componentDidMount() {
    console.log("Component mounted.");
    this.state.wsConnection.addEventListener("message", (e) => {
      console.log(e.data);
      this.setState((prevState) => prevState.messages.push(JSON.parse(e.data)));
    });
  }

  componentWillUnmount() {
    if(this.state.wsConnection) {
      this.state.wsConnection.close();
    }
  }

  render() {
    return (
      <div className="container">
        <ChatList chats={this.state.chats}></ChatList>
        <MessageList handleSendMessage={this.handleSendMessage} messages={this.state.messages}></MessageList>
        <ChatInfo chat={this.state.chats[0]}></ChatInfo>
      </div>
    );
  }

}

export default App;
