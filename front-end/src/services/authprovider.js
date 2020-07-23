const UNAUTHORIZED_JSON = {
    authorized: false,
    id: null,
    displayName: null
};

export const checkAuthorization = async () => {
    const lsToken = localStorage.getItem("acc.tok");
    if(!lsToken) {
        return UNAUTHORIZED_JSON;
    };
    return await fetch(`http://local.courier.net:8080/api/v1/authorize`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Authorization": `Bearer ${lsToken}`
        }
    })
    .then((response) => {
        console.log("Recieved response...");
        console.log(response);
        if(response.status !== 200) {
            console.log("Bad response");
            return null;
        }
        return response.json();
    })
    .then((json) => {
        if(json === null || json === undefined) {
            return UNAUTHORIZED_JSON;
        }
        return {
            authorized: true,
            id: json.id,
            displayName: json.displayName
        }
    })
    .catch((error) => {
        return UNAUTHORIZED_JSON;
    });
}

export const refreshAccessToken = async () => {
    const refreshToken = localStorage.getItem("ref.tok");
    if(!refreshToken) {
        return false;
    }
    const url = `http://local.courier.net:8090/api/v1/token` +
                `?client_id=${process.env.REACT_APP_OAUTH_CLIENT_ID}` + 
                `&refresh_token=${refreshToken}` + 
                `&grant_type=refresh_token`;
    return await fetch(url, {
        method: "POST",
        credentials: "include"
    })
    .then(response => {
        if(response.status !== 200) {
            return false;
        }
        return response.json();
    })
    .then(json => {
        if(json === null || json === undefined) {
            return false;
        }
        localStorage.setItem("acc.tok", json.token);
        return true;
    })
}

export const requestRefreshToken = async (params, state, code_verifier) => {
    const url = `http://local.courier.net:8090/api/v1/token` +
                `?client_id=${params.client_id}` + 
                `&state=${state}` +
                `&code=${params.code}` +
                `&redirect_uri=${params.redirect_uri}` +
                `&code_verifier=${code_verifier}` +
                `&grant_type=authorization_code`;
    return await fetch(url, {
        method: "POST",
        credentials: "include"
    })
    .then((response) => {
        if(response.status !== 200) {
            console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
            console.log(response);
            return false;
        }
        return response.json();
    })
    .then((json) => {
        if(!json) return false;
        localStorage.setItem("ref.tok", json.token);
        return true;
        // requestAccessToken(params.client_id, json.token);
    })
    .catch((error) => {
        console.log(error);
        return false;
    })
}
