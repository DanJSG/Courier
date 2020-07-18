export const broadcastChats = (ws, chats) => {
    if(!ws) {
        console.log("no websocket connection yet");
        return false;
    }
    const chatArray = [...chats.values()];
    const chatIds = chatArray.map(chat => {
        return {
            id: chat.id
        }
    })
    ws.send('@' + JSON.stringify(chatIds));
    return true;
}

export const loadAllChats = async(userId, token) => {
    const url = `http://local.courier.net:8080/api/v1/chat/getAll?id=${userId}`;
    return await fetch(url, {
        method: "GET",
        credentials: "include",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if(response.status !== 200) return null;
        return response.json();
    })
    .then(json => {
        if(!json) return null;
        const loadedChats = json.map(receivedChat => {
            return {
                id: receivedChat.id,
                name: receivedChat.name,
                created: true
            }
        });
        return loadedChats;
    })
    .catch(error => {
        console.log(error);
    });
}

export const loadChatHistory = async(chatId, token) => {
    const url = `http://local.courier.net:8080/api/v1/message/getAll?chatId=${chatId}`;
    return await fetch(url, {
        method: "GET",
        credentials: "include",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if(response.status === 204) {
            return [];
        } else if(response.status !== 200) {
            return null;
        }
        return response.json();
    })
    .then(json => {
        if(!json) return null;
        const dateFixedJson = json.map(message => {
            message.timestamp = (new Date(message.timestamp)).toUTCString();
            return message;
        })
        return dateFixedJson;
    })
    .catch(error => {
        console.log(error);
    })
}

export const saveChat = async(chatName, chatMembers, token) => {
    const chat = {
        id: null,
        name: chatName,
        members: chatMembers.map(member => member.id),
    }
    return await fetch("http://local.courier.net:8080/api/v1/chat/create", {
        method: "POST",
        credentials: "include",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(chat)
    })
    .then(response => {
        if(response.status !== 200) return null;
        return response.json();
    })
    .then(json => {
        if(!json) return null;
        chat.id = json.id;
        return chat;
    })
    .catch(error => {
        console.log(error);
    })
}

export const loadChatMembers = async(chatId, token) => {
    const url = `http://local.courier.net:8080/api/v1/chat/getMembers?chatId=${chatId}`;
    return await fetch(url, {
        method: "GET",
        credentials: "include",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if(response.status === 204) return [];
        if(response.status !== 200) return null;
        return response.json();
    })
    .then(members => {
        if(!members) return null;
        return members;
    })
    .catch(error => {
        console.log(error);
    })
}
