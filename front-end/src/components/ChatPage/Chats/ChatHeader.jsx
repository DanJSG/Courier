import React, {useState} from 'react';

function ChatHeader(props) {
    
    const [fetchedUsers, setFetchedUsers] = useState([]);
    const [suggestions, setSuggestions] = useState([]);

    const addMembers = (e) => {
        if(!props.isAddingMembers) return;
        e.preventDefault();
        const membersText = e.target.elements.members.value.trim().split(",");
        const members = membersText.map(member => {return {id: parseInt(member), displayName: member.trim()}});
        props.addMembers(members);
    }

    const somethingTyped = async (e) => {
        if((e.keyCode > 15 && e.keyCode < 19) || e.keyCode === 27 || e.keyCode === 20 || e.keyCode === 91) {
            return;
        }
        const searchText = e.target.value.trim();
        if(searchText == null || searchText === undefined || searchText === '') {
            setSuggestions([]);
            return;
        }
        const users = await fetchUsers(searchText);
        if(users != null && users !== []) {
            setFetchedUsers(users);
        }
        setSuggestions(() => {
            const regex = new RegExp(`^${searchText}`, 'gi');
            const filtered = fetchedUsers.filter(val => val.displayName.match(regex));
            return filtered;
        })
    }

    const fetchUsers = async (search) => {
        const url = `http://local.courier.net:8080/api/v1/search/searchUsers?searchTerm=${search}`;
        return await fetch(url, {
            method: "GET",
            credentials: "include",
            headers: {
                "Authorization": `Bearer ${props.token}`,
                "Content-Type": "application/json"
            }
        })
        .then(response => {
            if(response.status !== 200) return [];
            return response.json();
        })
        .then(json => {
            console.log(json);
            return json;
        })
        .catch(error => {
            console.log(error);
            return [];
        })
    }

    const suggestionClicked = (e) => {
        e.preventDefault();
        console.log(JSON.parse(e.target.user.value));
    }

    return(
        props.isAddingMembers
        ?
        <React.Fragment>
            <form className="list-group-item border-0 rounded-0" onSubmit={addMembers}>
                <label>To:&nbsp;</label>
                <input onChange={somethingTyped} name="members" className="border-0"/>
            </form>
            <div className='flex-grow-1' style={{
                zIndex: 1,
                position: 'absolute',
                backgroundColor: 'white',
                width: '85%',
                maxWidth: '85%',
                marginTop: 52
            }}>
                {
                    suggestions.map(val => {
                        return (
                            <form onSubmit={suggestionClicked} key={val.id}>
                                <input name="user" readOnly style={{display: 'None'}} value={JSON.stringify(val)}></input>
                                <button className="w-100 text-left chat-hover card card-body mb-1">{val.displayName}</button>
                            </form>
                            )
                    })
                }
            </div>
        </React.Fragment>
        :
        <h1 className="pl-3 pr-3">
            {props.chatName === "" || props.chatName == null ? <i className="text-muted">New Chat</i>: props.chatName}
        </h1>
    );

}

export default ChatHeader;