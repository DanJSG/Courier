import React from 'react';
import MessageList from './MessageList';
import MessageBuilder from './MessageBuilder';
import ChatList from './ChatList';
import ChatInfo from './ChatInfo';

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
      chats: [{name: "Chat 1", id: 'jkhdf', members: {sender: "SENDER", receivers: ["RECEIVER 1", "Receiver 2"]}}, {name: "Group Chat", id: 'ijfhdgdf', members: {sender: "SENDER", receivers: ["RECEIVER 1", "Receiver 2"]}}]
    }
  }

  handleSendMessage(message) {
    if(!message) {
      return "You must enter a message to send.";
    }
    this.setState((prevState) => prevState.messages.push(message));
  }

  render() {
    return (
      <div>
        <ChatList chats={this.state.chats}></ChatList>
        <ChatInfo chat={this.state.chats[0]}></ChatInfo>
        <MessageList messages={this.state.messages}></MessageList>
        <MessageBuilder handleSendMessage={this.handleSendMessage}></MessageBuilder>
      </div>
    );
  }

}

export default App;
