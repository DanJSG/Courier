import React, {useState, useEffect} from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import ChatPage from './components/ChatPage/ChatPage';
import LandingPage from './components/LandingPage/LandingPage';
import CallbackPage from './components/CallbackPage/CallbackPage';
import './App.scss';

function App() {

  const [isAuthorized, setIsAuthorized] = useState(false);

  const checkAuthorization = async () => {
    const lsToken = localStorage.getItem("acc.tok");
    if(!lsToken) {
      return false;
    };
    return await fetch(`http://local.courier.net:8080/api/v1/verifyJwt?id=3`, {
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
        return false;
      }
      return true;
    })
    .catch((error) => {
      console.log("fetch error...");
      console.log(error);
      return false;
    })
  }

  const awaitAuthCheck = async () => {
    const result = await checkAuthorization();
    setIsAuthorized(result);
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
            <ChatPage id={3} displayName="Dan" token="9p8ucnyc893ryux3nc"/>
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
