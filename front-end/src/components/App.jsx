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
      ]
    }
  }

  handleSendMessage(message) {
    if(!message.messageText) {
      return "You must enter a message to send.";
    }
    this.setState((prevState) => prevState.messages.push(message));
    const request = new XMLHttpRequest();
    request.open("POST", "http://localhost:8080/api/send");
    request.setRequestHeader("token", message.sender);
    request.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    request.send(JSON.stringify(message));
    // console.log(JSON.stringify(message));
    // console.log(message);
    const response = request.response;
    console.log(response);
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
