import React from 'react';
import BackgroundImg from '../commons/images/backgroundLogIn.jpg';
import { Button, Form, FormGroup, Label, Input, Spinner } from 'reactstrap';
import * as API_LOGIN from './api/login-api';

const backgroundStyle = {
    backgroundPosition: 'center',
    backgroundSize: 'cover',
    backgroundRepeat: 'no-repeat',
    width: "100%",
    height: "100vh",
    backgroundImage: `url(${BackgroundImg})`,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    position: 'relative',
};

const overlayStyle = {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    background: 'rgba(0, 0, 0, 0.7)',
    zIndex: 1,
};

const formStyle = {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    padding: '40px',
    borderRadius: '15px',
    width: '400px',
    boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
    zIndex: 2,
    textAlign: 'center',
};

class LogIn extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            isLoading: false,
            errorMessage: null,
        };
    }

    handleInputChange = (e) => {
        this.setState({ [e.target.name]: e.target.value });
    };

    handleLogin = () => {
        const { username, password } = this.state;

        if (!username || !password) {
            this.setState({ errorMessage: 'Please enter both username and password.' });
            return;
        }

        const credentials = { username, password };
        this.setState({ isLoading: true, errorMessage: null });

        API_LOGIN.loginUser(credentials, (result, status, err) => {
            this.setState({ isLoading: false });

            if (result && status === 200) {
                const jwt = result.token;
                localStorage.setItem('jwt', jwt);

                const decodedJWT = this.decodeJWT(jwt);
                const roles = decodedJWT.role;

                // Redirecționare în funcție de rol
                if (roles.includes('ROLE_ADMIN')) {
                    window.location.href = "/admin/" + decodedJWT.sub;
                    console.log("admin");
                    console.log(decodedJWT.sub);
                } else if (roles.includes('ROLE_USER')) {
                    window.location.href = "/client/" + decodedJWT.sub;
                    console.log("user");
                    console.log(decodedJWT.sub);
                } else {
                    this.setState({ errorMessage: "Unauthorized role." });
                }
            } else {
                this.setState({ errorMessage: "Login failed. Please check your credentials." });
            }
        });
    };


    decodeJWT(token) {
        try {
            const parts = token.split('.');
            if (parts.length !== 3) throw new Error('Invalid token format');

            const payload = parts[1];
            const padding = '='.repeat((4 - (payload.length % 4)) % 4);
            const base64 = payload + padding;
            const decoded = atob(base64);
            return JSON.parse(decoded);
        } catch (error) {
            console.error('Failed to decode token:', error);
            return null;
        }
    }

    render() {
        const { username, password, isLoading, errorMessage } = this.state;

        return (
            <div style={backgroundStyle}>
                <div style={overlayStyle}></div>
                <Form style={formStyle}>
                    <h2>Login</h2>
                    {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
                    <FormGroup>
                        <Label for="username">Username</Label>
                        <Input
                            type="text"
                            name="username"
                            id="username"
                            placeholder="Enter your username"
                            value={username}
                            onChange={this.handleInputChange}
                        />
                    </FormGroup>
                    <FormGroup>
                        <Label for="password">Password</Label>
                        <Input
                            type="password"
                            name="password"
                            id="password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={this.handleInputChange}
                        />
                    </FormGroup>
                    <Button color="primary" onClick={this.handleLogin} disabled={isLoading}>
                        {isLoading ? <Spinner size="sm" /> : 'Login'}
                    </Button>
                </Form>
            </div>
        );
    }
}

export default LogIn;
