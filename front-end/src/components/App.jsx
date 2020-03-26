import React from 'react';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom'
import MainPage from './MainPage';
import SignUpPage from './SignUpPage';
import LoginPage from './LoginPage';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.initLoginXhr = this.initLoginXhr.bind(this);
    this.updateAuthorization = this.updateAuthorization.bind(this);
    this.updateDisplayName = this.updateDisplayName.bind(this);
    this.sendLoginRequest = this.sendLoginRequest.bind(this);
    this.clearErrors = this.clearErrors.bind(this);
    this.state = {
      authorized: this.checkAuthorization(),
      id: localStorage.getItem('id'),
      displayName: null,
      loginError: null,
      token: null
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
    if(!bool) {
      this.setState({token: null});
    }
  }

  updateDisplayName(displayName) {
    this.setState({displayName: displayName});
  }

  sendLoginRequest(email, password) {
    const xhr = this.initLoginXhr();
    xhr.open("POST", "http://localhost:8080/api/account/login");
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({
        "email": email,
        "password": password
    }));    
  }

  initLoginXhr() {
    const xhr = new XMLHttpRequest();
    xhr.addEventListener("error", (ev) => {
      this.setState({loginError: xhr.responseText})
      alert(xhr.responseText);
      console.log(ev);
    });
    xhr.addEventListener("loadend", () => {
        if(xhr.status !== 200) {
          this.setState({loginError: xhr.responseText});
          console.log(xhr.status);
          return xhr.responseText;
        }
        const userSession = JSON.parse(xhr.responseText);
        console.log(userSession);
        this.setState({id: userSession.id, displayName: userSession.displayName, token: userSession.token});
        localStorage.setItem("id", userSession.id);
        this.updateAuthorization(true);
    });
    return xhr;
  }

  clearErrors() {
    this.setState({loginError: null});
  }

  render() {
    return(
      <Router>
        <Switch>
          <Route exact path="/">
            {
              this.state.authorized ? 
              <MainPage updateAuthorization={this.updateAuthorization}
                        updateDisplayName={this.updateDisplayName}
                        email={this.state.email}
                        id={this.state.id}
                        displayName={this.state.displayName}
                        token={this.state.token}/>
              :
              <Redirect to="/sign-up"/>              
            }
          </Route>
          <Route exact path="/sign-up">
            {
              this.state.authorized ? 
              <Redirect to="/"/>
              :
              <SignUpPage email={this.state.email}
                          id={this.state.id}
                          loginError={this.state.loginError}
                          sendLoginRequest={this.sendLoginRequest}
                          clearErrors={this.clearErrors}/>
            }
          </Route>
          <Route exact path='/sign-in'>
            {
              this.state.authorized ?
              <Redirect to="/"/>
              :
              <LoginPage  email={this.state.email} 
                          id={this.state.id}
                          loginError={this.state.loginError}
                          sendLoginRequest={this.sendLoginRequest}
                          clearErrors={this.clearErrors}/>
            }
          </Route>
        </Switch>
      </Router>
    );
  }
}

export default App;
