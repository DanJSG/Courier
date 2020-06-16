import React from 'react';

function ChatInfo(props) {
    return(
        <div>
            <h3>Members:</h3>
            <div className="list-group-flush">
                {props.currentChat.members.map((member) => <div className="list-group-item" key={member.id}>{member.displayName} </div>)}
            </div>
        </div>
    );
}

export default ChatInfo;