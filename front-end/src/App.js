import React, {useState, useEffect} from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import ChatPage from './components/ChatPage/ChatPage';
import LandingPage from './components/LandingPage/LandingPage';
import CallbackPage from './components/CallbackPage/CallbackPage';
import {checkAuthorization} from './services/authprovider';
import './App.scss';

function App() {

    const [isAuthorized, setIsAuthorized] = useState(false);
    const [id, setId] = useState(null);
    const [displayName, setDisplayName] = useState(null);

    const awaitAuthCheck = async () => {
        const result = await checkAuthorization();
        setId(result.id);
        setDisplayName(result.displayName);
        setIsAuthorized(result.authorized);
    }

    useEffect(() => {
        awaitAuthCheck();
    }, [])

    return (
        <Router>
        <Switch>
            <Route exact path="/">
            {
                isAuthorized
                ?
                <ChatPage id={id} displayName={displayName} token={localStorage.getItem("acc.tok")}/>
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
