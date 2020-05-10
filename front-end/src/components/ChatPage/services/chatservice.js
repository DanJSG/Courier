const wsOpenListener = (id) => {
    console.log(`WebSocket connection opened for user with ID ${id}.` )
}

const wsErrorListener = (errorCallback) => {
    if(errorCallback === null || errorCallback === undefined) {
        return;
    }
    alert("Failed to connect to chat room server.");
    errorCallback("wsErrorListener");
}

const wsMessageListener = (e, messageCallback, chatCallback) => {
    if(messageCallback === null || messageCallback === undefined ||
        chatCallback === null || chatCallback === undefined) {
        return;
    }
    if(e.data.charAt(0) !== "`") {
        const receivedMessage = JSON.parse(e.data);
        receivedMessage.timestamp = new Date(receivedMessage.timestamp).toUTCString();
        messageCallback(receivedMessage);
        return;
    }
    console.log(e.data);
    const chatMembers = JSON.parse(e.data.slice(1, e.data.length));
    chatCallback(chatMembers);
}

export const initializeWebSocket = (url, id, token, messageCallback, chatCallback, errorCallback) => {
    const ws = new WebSocket(url, [id, token]);
    ws.addEventListener("open", () => wsOpenListener(id));
    ws.addEventListener("error", () => wsErrorListener(errorCallback));
    ws.addEventListener("message", (e) => wsMessageListener(e, messageCallback, chatCallback));
    return ws;
}

export const removeWebSocketListeners = (ws, id, messageCallback, chatCallback, errorCallback) => {
    ws.removeEventListener("open", () => wsOpenListener(id));
    ws.removeEventListener("error", () => wsErrorListener(errorCallback));
    ws.removeEventListener("message", (e) => wsMessageListener(e, messageCallback, chatCallback));
}

export const sendMessage = (wsConnection, messageText, id, displayName) => {
    if(!wsConnection) {
        return {
            message: null,
            error: "Could not connect to the chat server."
        };
    }
    if(!messageText) {
        return {
            message: null,
            error: "You must enter a message to send."
        };
    }
    if(!id) {
        return {
            message: null,
            error: "An authorization error occurred. Please refresh your page."
        };
    }
    if(wsConnection.readyState !== wsConnection.OPEN) {
        return {
            message: null,
            error: "You are not connected to the chat room server."
        };
    }
    const message = {
            messageText: messageText,
            timestamp: new Date().toUTCString(),
            senderId: id,
            sender: displayName,
            receiver: "ALL"
    }
    wsConnection.send(JSON.stringify(message));
    return {
        message: message,
        error: null
    }
}
