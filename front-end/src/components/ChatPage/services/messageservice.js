const wsOpenListener = () => {
    console.log(`WebSocket connection opened.`);
}

const wsErrorListener = (errorCallback) => {
    if(errorCallback === null || errorCallback === undefined) {
        return;
    }
    alert("Failed to connect to chat room server.");
    errorCallback("wsErrorListener");
}

export const authorizeWebsocket = (ws, token) => {
    if(!ws) return false;
    ws.send('#' + token);
    return true;
}

const wsMessageListener = (e, messageCallback, chatCallback, authCallback) => {
    if(messageCallback === null || messageCallback === undefined ||
        chatCallback === null || chatCallback === undefined) {
        return;
    }
    if(e.data.charAt(0) === "`") {
        const chatMembers = JSON.parse(e.data.slice(1, e.data.length));
        chatCallback(chatMembers);
        return;
    } else if(e.data.charAt(0) === "#") {
        authCallback();
        return;
    }
    const receivedMessage = JSON.parse(e.data);
    receivedMessage.timestamp = new Date(receivedMessage.timestamp).toUTCString();
    messageCallback(receivedMessage);
    return;
}

export const initializeWebSocket = (url, messageCallback, chatCallback, errorCallback, authCallback) => {
    const ws = new WebSocket(url);
    ws.addEventListener("open", () => wsOpenListener());
    ws.addEventListener("error", () => wsErrorListener(errorCallback));
    ws.addEventListener("message", (e) => wsMessageListener(e, messageCallback, chatCallback, authCallback));
    return ws;
}

export const removeWebSocketListeners = (ws, messageCallback, chatCallback, errorCallback) => {
    ws.removeEventListener("open", () => wsOpenListener());
    ws.removeEventListener("error", () => wsErrorListener(errorCallback));
    ws.removeEventListener("message", (e) => wsMessageListener(e, messageCallback, chatCallback));
}

export const sendMessage = (wsConnection, messageText, id, displayName, chatId) => {
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
            chatId: chatId,
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
