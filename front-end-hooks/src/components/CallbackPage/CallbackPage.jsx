import {useState, useEffect} from 'react';
import {useLocation} from 'react-router-dom';
import {requestTokens} from './services/callbackservice';
import {getQueryStringAsJson} from '../../services/querystringmanipulator';

function CallbackPage() {

    const location = useLocation();
    const [params] = useState(getQueryStringAsJson(location));

    useEffect(() => {
        const state = localStorage.getItem("auth.state");
        const code_verifier = localStorage.getItem("auth.code_verifier");
        requestTokens(params, state, code_verifier);
    })

    return null;

}

export default CallbackPage;