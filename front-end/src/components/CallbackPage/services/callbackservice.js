import {refreshAccessToken} from '../../../services/authprovider'

const requestRefreshToken = (params, state, code_verifier) => {
    const url = `http://local.courier.net:8090/api/v1/token` +
                `?client_id=${params.client_id}` + 
                `&state=${state}` +
                `&code=${params.code}` +
                `&redirect_uri=${params.redirect_uri}` +
                `&code_verifier=${code_verifier}` +
                `&grant_type=authorization_code`;
    fetch(url, {
        method: "POST",
        credentials: "include"
    })
    .then((response) => {
        if(response.status !== 200) {
            console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
            console.log(response);
            return;
        }
        return response.json();
    })
    .then((json) => {
        localStorage.setItem("ref.tok", json.token);
        requestAccessToken(params.client_id, json.token);
    })
    .catch((error) => {
        console.log(error);
    })
}

const requestAccessToken = async (client_id, refresh_token) => {
    const refreshed = await refreshAccessToken();
    if(refreshed === true) {
        window.location.href = "http://local.courier.net:3000";
    }
    // console.log("Requesting access token with: " + refresh_token);
    // const url = `http://local.courier.net:8090/api/v1/token` +
    //             `?client_id=${client_id}` + 
    //             `&refresh_token=${refresh_token}` + 
    //             `&grant_type=refresh_token`;
    // fetch(url, {
    //     method: "POST",
    //     credentials: "include"
    // })
    // .then((response) => {
    //     if(response.status !== 200) {
    //         console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
    //         console.log(response);
    //         return;
    //     }
    //     return response.json();
    // })
    // .then((json) => {
    //     localStorage.setItem("acc.tok", json.token);
    //     console.log("Redirecting to ")
    //     window.location.href = "http://local.courier.net:3000";
    // })
    // .catch((error) => {
    //     console.log(error);
    // })
}

export const requestTokens = (params, state, code_verifier) => {
    console.log(state, code_verifier);
    requestRefreshToken(params, state, code_verifier);
}
