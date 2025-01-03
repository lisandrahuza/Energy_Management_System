// App.js
import { BrowserRouter as Router, Redirect, Route, Switch } from 'react-router-dom';
import NavigationBar from './navigation-bar';
import React from 'react';
import ErrorPage from './commons/errorhandling/error-page';
import LogIn from "./logIn/login-container";
import ClientRoute from "./clientRoute";
import AdminRoute from "./adminRoute";
import styles from './commons/styles/project-style.css';

class App extends React.Component {
    render() {
        return (
            <div className={styles.back}>
                <Router>
                    <div>
                        <NavigationBar />
                        <Switch>
                            <Route exact path="/">
                                <Redirect to="/login" />
                            </Route>
                            <Route exact path='/login' component={LogIn} />
                            <Route exact path='/client/:value' component={ClientRoute} />
                            <Route exact path='/admin/:value' component={AdminRoute} />
                            <Route exact path='/error' component={ErrorPage} />
                            <Route component={ErrorPage} />
                        </Switch>
                    </div>
                </Router>
            </div>
        )
    }
}

export default App;
