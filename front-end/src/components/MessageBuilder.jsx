import React from 'react';

class MessageBuilder extends React.Component {

    constructor(props) {
        super(props);
        this.handleSendMessage = this.handleSendMessage.bind(this);
        this.state = {
            errorMsg: undefined
        }
    }

    handleSendMessage(e) {
        e.preventDefault();
        const messageText = e.target.elements.message.value.trim();
        // const message = {
        //     messageText: messageText,
        //     timestamp: new Date().toTimeString(),
        //     sender: "SENDER",
        //     receiver: "RECEIVER"
        // }
        e.target.elements.message.value = null;
        const errorMsg = this.props.handleSendMessage(messageText);
        this.setState(() => ({errorMsg: errorMsg}));
        if(!errorMsg) {
            e.target.elements.message.value = '';
        }
    }

    render() {
        return(
            <div className="message-builder">
                <form onSubmit={this.handleSendMessage}>
                    <input type="text" name="message"></input>
                    <button>Send</button>
                </form>
                {this.state.errorMsg}
            </div>
        );
    }
}

export default MessageBuilder;