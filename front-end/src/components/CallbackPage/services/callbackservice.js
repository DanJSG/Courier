import {refreshAccessToken, requestRefreshToken} from '../../../services/authprovider'

// const requestRefreshToken = async (params, state, code_verifier) => {
//     const url = `http://local.courier.net:8090/api/v1/token` +
//                 `?client_id=${params.client_id}` + 
//                 `&state=${state}` +
//                 `&code=${params.code}` +
//                 `&redirect_uri=${params.redirect_uri}` +
//                 `&code_verifier=${code_verifier}` +
//                 `&grant_type=authorization_code`;
//     return await fetch(url, {
//         method: "POST",
//         credentials: "include"
//     })
//     .then((response) => {
//         if(response.status !== 200) {
//             console.log(`Request failed. Returned status code ${response.status}. Response object logged below.`);
//             console.log(response);
//             return false;
//         }
//         return response.json();
//     })
//     .then((json) => {
//         if(!json) return false;
//         localStorage.setItem("ref.tok", json.token);
//         return true;
//         // requestAccessToken(params.client_id, json.token);
//     })
//     .catch((error) => {
//         console.log(error);
//         return false;
//     })
// }

// const requestAccessToken = async (client_id, refresh_token) => {
    // const refreshed = await refreshAccessToken();
    // if(refreshed === true) {
    //     window.location.href = "http://local.courier.net:3000";
    // }
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
// }

export const requestTokens = async (params, state, code_verifier) => {
    const fetchedRefreshToken = await requestRefreshToken(params, state, code_verifier);
    if(!fetchedRefreshToken) {
        // TODO add redirect to 401 page or something similar
        return;
    }
    const fetchedAccessToken = await refreshAccessToken();
    if(!fetchedAccessToken) {
        // TODO add redirect to 401 page or something similar
        return;
    }
    window.location.href = "http://local.courier.net:3000";
    // console.log(state, code_verifier);
    // requestRefreshToken(params, state, code_verifier);
}
