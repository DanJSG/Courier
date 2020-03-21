import React from 'react';
import {Link} from 'react-router-dom';
import '../css/index.css';
import '../css/SignUpPage.css';

class SignUpPage extends React.Component {

    constructor(props) {
        super(props);
        props.clearErrors();
        this.handleSignUp = this.handleSignUp.bind(this);
        this.sendSignUpRequest = this.sendSignUpRequest.bind(this);
        this.checkForm = this.checkForm.bind(this);
        this.state = {
            signUpError: null,
        }
    }

    sendSignUpRequest(email, password, displayName) {
        const xhr = new XMLHttpRequest();
        xhr.addEventListener("error", (ev) => {
            alert("Sign up request failed.");
            console.log(ev);
        });
        xhr.addEventListener("loadend", () => {
            if(xhr.status !== 200) {
                this.setState({signUpError: xhr.responseText});
                return;
            }
            console.log(JSON.parse(xhr.responseText));
            this.props.sendLoginRequest(email, password);
        });
        xhr.open("POST", "http://localhost:8080/api/account/create");
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(JSON.stringify({
            "email": email,
            "password": password,
            "displayName": displayName
        }));
    }

    checkForm(email, password, passwordRepeat, displayName) {
        if(email === undefined || email === null || email === "") {
            this.setState({signUpError: "Please enter an email address."})
            return false;
        }
        if(password !== passwordRepeat) {
            this.setState({signUpError: "Passwords do not match."})
            return false;
        }
        if(password.length < 8) {
            this.setState({signUpError: "Password not long enough."})
            return false;
        }
        if(displayName === undefined || displayName === null || displayName === "") {
            this.setState({signUpError: "Please enter a display name."})
            return false;
        }
        const symbols = /^[ a-z0-9A-Z]*$/;
        if(!symbols.test(displayName)) {
            console.log("STOP! YOU HAVE VIOLATED THE LAW! PAY THE COURT A FINE OR SERVE YOUR SENTENCE!")
            return false;
        }
        return true;
    }

    handleSignUp(e) {
        e.preventDefault();
        this.setState({signUpError: null})
        const email = e.target.elements.email.value.trim();
        const password = e.target.elements.password.value;
        const passwordRepeat = e.target.elements.passwordRepeat.value;
        const displayName = e.target.elements.displayName.value.trim();
        if(this.checkForm(email, password, passwordRepeat, displayName)) {
            this.sendSignUpRequest(email, password, displayName);
        }
    }

    render() {
        console.log("In SignUpPage: " + localStorage.getItem("loggedIn"));
        return(
            <div className="form-container">
                <form onSubmit={this.handleSignUp}>
                    Email: &nbsp;
                    <input type="email" name="email"></input>
                    <br/>
                    Display name: &nbsp;
                    <input type="text" name="displayName"></input>
                    <br/>
                    Password: &nbsp;
                    <input type="password" name="password"></input>
                    <br/>
                    Repeat password: &nbsp;
                    <input type="password" name="passwordRepeat"></input>
                    <br/>
                    <button>Sign Up</button>
                    <br/>
                    {this.state.signUpError ? this.state.signUpError : this.props.loginError}
                    <br/>
                    Already signed up? <Link to="/sign-in">Sign in here.</Link>
                </form>
            </div>
        );
    }

}

export default SignUpPage;
