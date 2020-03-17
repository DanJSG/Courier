import React from 'react';
import {Link} from 'react-router-dom';
import '../css/index.css';
import '../css/SignUpPage.css';

class LoginPage extends React.Component {

    constructor(props) {
        super(props);
        props.clearErrors();
        this.checkForm = this.checkForm.bind(this);
        this.handleLogin = this.handleLogin.bind(this);
        this.state = {
            formError: null
        }
    }

    checkForm(email, password) {
        if(email === undefined || email === null || email === "") {
            this.setState({formError: "Please enter an email address."})
            return false;
        }
        if(password === undefined || password === null || password === "") {
            this.setState({formError: "Please enter a password."})
            return false;
        }
        return true;
    }

    handleLogin(e) {
        e.preventDefault();
        this.setState({formError: null})
        const email = e.target.elements.email.value.trim();
        const password = e.target.elements.password.value;
        if(this.checkForm(email, password)) {
            this.props.sendLoginRequest(email, password);
        }
    }

    render() {
        console.log("In LoginPage: " + localStorage.getItem("loggedIn"));
        return(
            <div className="form-container">
                <form onSubmit={this.handleLogin}>
                    Email: &nbsp;
                    <input type="email" name="email"></input>
                    <br/>
                    Password: &nbsp;
                    <input type="password" name="password"></input>
                    <br/>
                    <button>Sign In</button>
                    <br/>
                    {this.state.formError ? this.state.formError : this.props.loginError}
                    <br/>
                    Not got an account yet? <Link to="/sign-up">Sign up here.</Link>
                </form>
            </div>
        );
    }
}

export default LoginPage;
