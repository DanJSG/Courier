import React, {useEffect, useRef} from 'react';
import {Scrollbars} from 'react-custom-scrollbars'
import Message from "./Message";

function MessageList(props){

    const messageScrollbar = useRef(null);

    useEffect(() => {
        messageScrollbar.current.scrollToBottom();
    });

    return(
        <div className="h-100 w-100 mh-100 flex-grow-1 border-top" style={{backgroundColor: "rgba(228, 229, 233, 0.4)"}}>
            <Scrollbars ref={messageScrollbar}>
                <div className="bg-transparent list-group-flush w-100 pl-3 pr-3">
                    {props.messages.map((message) => {
                        return (<Message isSender={props.id === message.senderId} message={message} key={Math.random() * 10000}></Message>)
                    })}
                </div>
            </Scrollbars>
        </div>
    );  
}

export default MessageList;
