import React from 'react';
import {CLIENT_ID, REDIRECT_URI, SCOPE, AUDIENCE, RESPONSE_TYPE} from '../../services/oauth.config';
import {generateCodeChallenge, generateState} from '../../services/codeprovider';

function LandingPage() {
    
    const redirectToSignIn = () => {
        console.log("Redirecting to sign in...");
        const state = generateState();
        const codeChallenge = generateCodeChallenge();
        sessionStorage.setItem("auth.state", state);
        sessionStorage.setItem("auth.code_verifier", codeChallenge.code_verifier);
        window.location.href = `http://local.courier.net:3010/oauth2/authorize` +
                                `?audience=${AUDIENCE}` + 
                                `&scope=${SCOPE}` + 
                                `&response_type=${RESPONSE_TYPE}` + 
                                `&client_id=${CLIENT_ID}` + 
                                `&redirect_uri=${REDIRECT_URI}` +
                                `&state=${state}` + 
                                `&code_challenge=${codeChallenge.code_challenge}` + 
                                `&code_challenge_method=${codeChallenge.code_challenge_method}`;
    }

    return(
        <div>
            <button onClick={redirectToSignIn} className="btn btn-primary">Sign In</button>
        </div>
    );
}

export default LandingPage;