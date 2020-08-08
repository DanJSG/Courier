import React, {useState, useRef} from 'react';
import {searchUsers, stripBadChars} from '../services/searchservice'

function ChatHeader(props) {
    
    const [fetchedUsers, setFetchedUsers] = useState([]);
    const [suggestions, setSuggestions] = useState([]);
    const [prevSearches] = useState(new Map());
    const [usersToAdd, setUsersToAdd] = useState([]);
    const searchBox = useRef(null);

    const addMembers = (e) => {
        if(!props.isAddingMembers) return;
        e.preventDefault();
        props.addMembers(usersToAdd);
        setUsersToAdd([]);
    }

    const somethingTyped = async (e) => {
        if((e.keyCode > 15 && e.keyCode < 19) || e.keyCode === 27 || e.keyCode === 20 || e.keyCode === 91) {
            return;
        }
        const searchText = stripBadChars(e.target.value.trim().toLowerCase());
        if(searchText == null || searchText === undefined || searchText === '') {
            return setSuggestions([]);
        }
        let subSearch = searchText.substring(0, searchText.length - 1);
        let users;
        if(prevSearches.has(searchText)) {
            users = prevSearches.get(searchText);
        } else if(prevSearches.has(subSearch)) {
            users = prevSearches.get(subSearch)
        } else {
            users = await searchUsers(searchText, props.token);
        }
        if(users != null && users !== []) {
            setFetchedUsers(users);
        }
        setSuggestions(() => {
            const regex = new RegExp(`^${searchText}`, 'gi');
            const filtered = fetchedUsers.filter(val => val.id === props.id ? false : val.displayName.match(regex));
            return filtered;
        })
        if(!prevSearches.has(searchText)) {
            prevSearches.set(searchText, users);
        }
    }

    const suggestionClicked = (e) => {
        e.preventDefault();
        const user = JSON.parse(e.target.user.value);
        let duplicateFound = false;
        usersToAdd.forEach(currUser => {
            if(currUser.id === user.id) {
                duplicateFound = true;
            }
        });
        if(duplicateFound) return;
        setUsersToAdd(prev => [...prev, user]);
        searchBox.current.value = '';
        searchBox.current.focus();
        setSuggestions([]);
    }

    return(
        props.isAddingMembers
        ?
        <React.Fragment>
            <form className="list-group-item border-0 rounded-0" onSubmit={addMembers}>
                <label>To:&nbsp;</label>
                {
                    usersToAdd.map(user => 
                        <label key={user.id}>{user.displayName},&nbsp;</label>
                    )
                }
                <input ref={searchBox} onChange={somethingTyped} name="members" className="border-0"/>
            </form>
            {
                // TODO refactor this CSS into a class
            }
            <div className='flex-grow-1' style={{
                zIndex: 1,
                position: 'absolute',
                backgroundColor: 'white',
                width: '85%',
                maxWidth: '85%',
                maxHeight: '50%',
                marginTop: 52,
                overflowY: 'scroll'
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
