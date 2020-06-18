import React from 'react';

function Message(props) {

    const senderAlignment = props.isSender ? "d-flex justify-content-end w-100" : "";

    return(
        <div className="list-group-item p-2 bg-transparent d-flex flex-row">
            <div className="w-100">
                <div className={senderAlignment}>
                    <b>{props.message.sender}:</b>&nbsp;{props.message.messageText} 
                </div>
                <div className={senderAlignment}>
                    <small>
                        <i>{props.message.timestamp}</i>
                    </small>
                </div>
            </div>
        </div>
    );
}

export default Message;
