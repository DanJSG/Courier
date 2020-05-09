import React, {useState, useEffect} from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import ChatPage from './components/ChatPage/ChatPage';
import LandingPage from './components/LandingPage/LandingPage';
import CallbackPage from './components/CallbackPage/CallbackPage';
import './App.scss';

function App() {

  const [isAuthorized, setIsAuthorized] = useState(false);
  const [id, setId] = useState(null);
  const [displayName, setDisplayName] = useState(null);

  const checkAuthorization = async () => {
    const lsToken = localStorage.getItem("acc.tok");
    if(!lsToken) {
      return {
        authorized: false,
        id: null,
        displayName: null
      };
    };
    return await fetch(`http://local.courier.net:8080/api/v1/verifyJwt`, {
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
        return {
          authorized: false,
          id: null,
          displayName: null
        };
      }
      return response.json();
    })
    .then((json) => {
      return {
        authorized: true,
        id: json.id,
        displayName: json.displayName
      }
    })
    .catch((error) => {
      console.log("fetch error...");
      console.log(error);
      return false;
    })
  }

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
