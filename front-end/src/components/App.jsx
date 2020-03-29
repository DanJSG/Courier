import React from 'react';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom'
import MainPage from './MainPage';
import SignUpPage from './SignUpPage';
import LoginPage from './LoginPage';

class App extends React.Component {

  constructor(props) {
    super(props);
    this.initLoginXhr = this.initLoginXhr.bind(this);
    this.logout = this.logout.bind(this);
    this.updateDisplayName = this.updateDisplayName.bind(this);
    this.sendLoginRequest = this.sendLoginRequest.bind(this);
    this.clearErrors = this.clearErrors.bind(this);
    this.state = {
      authorized: false,
      id: localStorage.getItem('id'),
      displayName: null,
      loginError: null
    }
    this.checkAuthorization();
  }

  // componentDidMount() {
  //   this.checkAuthorization();
  // }

  checkAuthorization() {
    const xhr = new XMLHttpRequest();
    xhr.withCredentials = true;
    xhr.open("POST", `http://localhost:8080/api/account/verifyJwt?id=${this.state.id}`);
    xhr.addEventListener("error", () => {
      this.setState({authorized: false});
    })
    xhr.addEventListener("loadend", () => {
      console.log("JWT Verification:");
      console.log(xhr.responseText);
      if(xhr.responseText !== "true") {
        this.setState({authorized: false});
        return;
      }
      this.setState({authorized: true});
    })
    xhr.send();
  }

  logout() {
    this.setState({authorized: false});
    localStorage.clear();
  }

  updateDisplayName(displayName) {
    this.setState({displayName: displayName});
  }

  sendLoginRequest(email, password) {
    const xhr = this.initLoginXhr();
    xhr.open("POST", "http://localhost:8080/api/account/login");
    xhr.withCredentials = true;
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.send(JSON.stringify({
        "email": email,
        "password": password
    }));    
  }

  initLoginXhr() {
    const xhr = new XMLHttpRequest();
    xhr.addEventListener("error", (ev) => {
      this.setState({loginError: "An error occurred whilst contacting the server."});
      console.log(ev);
    });
    xhr.addEventListener("loadend", () => {
        if(xhr.status === 0) {
          this.setState({loginError: "Failed to connect to login server."});
          return;
        } else if(xhr.status !== 200) {
          this.setState({loginError: xhr.responseText});
          console.log(xhr.status);
          this.logout();
          return xhr.responseText;
        }
        const userSession = JSON.parse(xhr.responseText);
        console.log(userSession);
        this.setState({id: userSession.id, displayName: userSession.displayName});
        localStorage.setItem("id", userSession.id);
        this.checkAuthorization();
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
              <MainPage logout={this.logout}
                        updateDisplayName={this.updateDisplayName}
                        email={this.state.email}
                        id={this.state.id}
                        displayName={this.state.displayName}/>
              :
              <Redirect to="/sign-in"/>              
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
