import React, {useState, useEffect} from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import ChatPage from './components/ChatPage/ChatPage';
import LandingPage from './components/LandingPage/LandingPage';
import CallbackPage from './components/CallbackPage/CallbackPage';
import {checkAuthorization, requestAccessToken} from './services/authprovider';
import './App.scss';

function App() {

    const [isAuthorized, setIsAuthorized] = useState(false);
    const [id, setId] = useState(null);
    const [displayName, setDisplayName] = useState(null);

    const checkAuth = async () => {
        let result = await checkAuthorization();
        setId(result.id);
        setDisplayName(result.displayName);
        setIsAuthorized(result.authorized);
        if(result.authorized === true) {
            return true;
        }
        localStorage.removeItem("acc.tok");
        const refreshToken = localStorage.getItem("ref.tok");
        if(refreshToken == null || refreshToken === undefined) {
            return false;
        }
        const refreshed = await requestAccessToken();
        if(refreshed === false) {
            return false;
        }
        result = await checkAuthorization();
        setId(result.id);
        setDisplayName(result.displayName);
        setIsAuthorized(result.authorized);
        if(isAuthorized) {
            return true;
        }
        return false;
    }

    useEffect(() => {
        checkAuth();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    return (
        <Router>
        <Switch>
            <Route exact path="/">
            {
                isAuthorized
                ?
                <ChatPage checkAuth={checkAuth} id={id} displayName={displayName} token={localStorage.getItem("acc.tok")}/>
                :
                <LandingPage />
            }
            </Route>
            <Route exact path="/oauth2/auth_callback">
            <CallbackPage />
            </Route>
        </Switch>
        </Router>
    );
}

export default App;
