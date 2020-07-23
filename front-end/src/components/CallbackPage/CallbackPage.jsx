import {useState, useEffect} from 'react';
import {useLocation} from 'react-router-dom';
// import {requestTokens} from './services/callbackservice';
import {getQueryStringAsJson} from '../../services/querystringmanipulator';
import {requestAccessToken, requestRefreshToken} from '../../services/authprovider'

function CallbackPage() {

    const location = useLocation();
    const [params] = useState(getQueryStringAsJson(location));

    const requestTokens = async (params, state, code_verifier) => {
        const fetchedRefreshToken = await requestRefreshToken(params, state, code_verifier);
        if(!fetchedRefreshToken) {
            // TODO add redirect to 401 page or something similar
            return;
        }
        const fetchedAccessToken = await requestAccessToken();
        if(!fetchedAccessToken) {
            // TODO add redirect to 401 page or something similar
            return;
        }
        window.location.href = "http://local.courier.net:3000";
    }

    useEffect(() => {
        const state = sessionStorage.getItem("auth.state");
        const code_verifier = sessionStorage.getItem("auth.code_verifier");
        sessionStorage.removeItem("auth.state");
        sessionStorage.removeItem("auth.code_verifier");
        requestTokens(params, state, code_verifier);
    })

    return null;

}

export default CallbackPage;