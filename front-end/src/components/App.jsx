import React from 'react';
import {BrowserRouter as Router, Switch, Route, Link} from 'react-router-dom'
import MainPage from './MainPage';
import SignUpPage from './SignUpPage';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.updateAuthorization = this.updateAuthorization.bind(this);
    this.updateUser = this.updateUser.bind(this);
    this.state = {
      authorized: this.checkAuthorization(),
      email: null,
      sessionId: null
    }
  }

  checkAuthorization() {
    if(localStorage.getItem("loggedIn") === "true") {
      return true;
    }
    return false;
  }

  updateAuthorization(bool) {
    this.setState({authorized: bool});
    localStorage.setItem("loggedIn", `${bool}`);
  }

  updateUser(email, sessionId) {
    this.setState({
      email: email,
      sessionId: sessionId
    });
    localStorage.setItem("email", `${email}`);
    localStorage.setItem("sessionId", `${sessionId}`);
  }

  componentWillUnmount() {
    localStorage.clear();
  }

  render() {

    console.log("Authorization: " + this.checkAuthorization());
    console.log("In App: " + localStorage.getItem("loggedIn"));

    return(
      this.state.authorized ? 
      <MainPage updateAuthorization={this.updateAuthorization}
                updateUser={this.updateUser}
                email={this.state.email}
                sessionId={this.state.sessionId}/>
      :
      <SignUpPage updateAuthorization={this.updateAuthorization}
                  updateUser={this.updateUser}
                  email={this.state.email}
                  sessionId={this.state.sessionId}/>
    );
  }

}

export default App;
