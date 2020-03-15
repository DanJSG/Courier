import React from 'react';
import {BrowserRouter as Router, Switch, Route, Link} from 'react-router-dom'
import MainPage from './MainPage';
import SignUpPage from './SignUpPage';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.updateAuthorization = this.updateAuthorization.bind(this);
    this.state = {
      authorized: this.checkAuthorization()
    }
  }

  checkAuthorization() {
    if(localStorage.getItem("loggedIn") === "true") {
      return true;
    }
    return false;
  }

  updateAuthorization(bool) {
    localStorage.setItem("loggedIn", `${bool}`);
    this.setState({authorized: bool});
  }

  render() {

    console.log("Authorization: " + this.checkAuthorization());
    console.log("In App: " + localStorage.getItem("loggedIn"));

    return(
      this.state.authorized ? <MainPage updateAuthorization={this.updateAuthorization}/> : <SignUpPage updateAuthorization={this.updateAuthorization}/>
    );
  }

}

export default App;
