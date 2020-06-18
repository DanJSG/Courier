import React from 'react';

function Message(props) {

    const senderAlignment = props.isSender ? "d-flex justify-content-end" : "";
    const margins = props.isSender ? {marginLeft: "35%", width: "65%"} : {marginRight: "35%", width: "65%"};

    return(
        <div className="list-group-item pt-2 pb-2 bg-transparent d-flex flex-row">
            <div className="w-100">
                <div className={senderAlignment} style={margins}>
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
