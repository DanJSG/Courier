import React, {useState} from 'react';

function MessageBuilder(props) {

    const [error, setError] = useState("");

    const handleSendMessage = (e) => {
        e.preventDefault();
        const messageText = e.target.elements.message.value.trim();
        e.target.elements.message.value = null;
        const errorMsg = props.handleSendMessage(messageText);
        setError(errorMsg);
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
                {error && <label>{error}</label>}
            </div>
        );

}

export default MessageBuilder;
