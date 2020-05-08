import React from 'react';
import {BrowserRouter as Router, Switch, Route} from 'react-router-dom';
import ChatPage from './components/ChatPage/ChatPage';
import './App.scss';

function App() {
  return (
    <Router>
      <Switch>
        <Route exact path="/">
          <ChatPage id={13} displayName="Dan" token="9p8ucnyc893ryux3nc"/>
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
