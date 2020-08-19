import React from 'react';

function ChatInfo(props) {
    return(
        <div className="col-2 border pt-2">
            <h2 className="text-center">Members:</h2>
            <div className="list-group-flush">
                {
                    props.currentChat.members.map((member) => {
                        let onlineIcon = null;
                        if(props.activeMembers != null && props.activeMembers.has(member.id)) {
                            onlineIcon = <small><i className="text-success fa fa-check-circle "></i></small>;
                        }
                        return <div className="list-group-item" key={member.id}>{member.displayName}&nbsp;{onlineIcon}</div>;
                    })
                }
            </div>
        </div>
    );
}

export default ChatInfo;
