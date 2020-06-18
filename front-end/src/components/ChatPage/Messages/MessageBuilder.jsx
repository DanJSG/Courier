import React, {useState, useRef} from 'react';

function MessageBuilder(props) {

    const [error, setError] = useState("");
    const formRef = useRef(null);

    const onClickSendMessage = (e) => {
        e.preventDefault();
        sendMessage();
    }

    const onEnterSendMessage = (e) => {
        if(e.keyCode !== 13 || e.shiftKey === true) return;
        e.preventDefault();
        sendMessage();
    }

    const sendMessage = () => {
        const messageText = formRef.current.elements.message.value.trim();
        formRef.current.elements.message.value = null;
        const errorMsg = props.handleSendMessage(messageText);
        setError(errorMsg);
        if(!errorMsg) {
            formRef.current.elements.message.value = '';
        }
    }

    return(
        <React.Fragment>    
            <form className="input-group p-0" ref={formRef} onSubmit={onClickSendMessage}>
                <textarea onKeyDown={onEnterSendMessage}
                          className="form-control rounded-0" 
                          type="text" 
                          name="message" 
                          style={{resize: "none"}} />
                <div className="input-group-append">
                    <button className="btn btn-outline-secondary pl-4 pr-4 rounded-0">Send</button>
                </div>
            </form>
            {error && <label>{error}</label>}
        </React.Fragment>
        );
}

export default MessageBuilder;
