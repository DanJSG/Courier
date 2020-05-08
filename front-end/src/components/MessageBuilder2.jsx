import React from 'react';

function MessageBuilder2(props) {

    [error, setError] = useState(null);

    const handleSendMessage = (e) => {
        e.preventDefault();
        const messageText = e.target.elements.message.value.trim();
        e.target.elements.message.value = null;
        const errorMsg = props.handleSendMessage(messageText);
        setError(errorMsg);
        this.setState(() => ({errorMsg: errorMsg}));
        if(!errorMsg) {
            e.target.elements.message.value = '';
        }
    }

    return(
            <div className="message-builder">
                <form onSubmit={handleSendMessage}>
                    <input type="text" name="message"></input>
                    <button>Send</button>
                </form>
                {this.state.errorMsg}
            </div>
        );

}

export default MessageBuilder2;