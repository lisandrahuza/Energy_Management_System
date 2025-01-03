import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    login: '/validare/login'
};

function loginUser(credentials, callback) {
    const request = new Request(HOST.backend_api + endpoint.login, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
    });

    console.log("Login Request URL: " + request.url);

    RestApiClient.performRequest(request, (data, status, error) => {
        if (error) {
            console.error("Login API error:", error);
        } else if (status !== 200 || !data.token) {
            console.error("Login failed:", status, data);
        }
        callback(data, status, error);
    });
}

export { loginUser };
