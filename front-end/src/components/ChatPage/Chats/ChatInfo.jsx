import React from 'react';

function ChatInfo(props) {
    return(
        <div>
            <h2 className="text-center">Members:</h2>
            <div className="list-group-flush">
                {
                    props.currentChat.members.map((member) => {
                        let userDisplay = member.displayName;
                        if((props.activeMembers != null || props.activeMembers != undefined) && props.activeMembers.has(member.id)) {
                            userDisplay += " [Online]";
                        }
                        return <div className="list-group-item" key={member.id}>{userDisplay}</div>;
                    })
                }
            </div>
        </div>
    );
}

export default ChatInfo;
