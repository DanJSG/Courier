import React, {useRef} from 'react';

function MessageBuilder(props) {

    // commenting out whilst not used, might be reimplemented later
    // const [error, setError] = useState("");
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
        // setError(errorMsg);
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
                          style={{resize: "none"}} 
                          placeholder="Type your message here..."/>
                <div className="input-group-append">
                    <button className="btn btn-outline-secondary pl-4 pr-4 rounded-0">Send&nbsp;&nbsp;<i className="fa fa-paper-plane-o" style={{fontSize: "16px"}}/></button>
                </div>
            </form>
        </React.Fragment>
        );
}

export default MessageBuilder;
