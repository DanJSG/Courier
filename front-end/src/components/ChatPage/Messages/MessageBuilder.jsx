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
        <React.Fragment>    
            <form className="input-group p-0" onSubmit={handleSendMessage}>
                <input className="form-control" type="text" name="message"></input>
                <div className="input-group-append">
                    <button className="btn btn-outline-secondary">Send</button>
                </div>
            </form>
            {error && <label>{error}</label>}
        </React.Fragment>
        );

}

export default MessageBuilder;
