// src/api/admin-api.js

import { HOST } from '../../commons/hosts';  // Adjust the import path as needed
import RestApiClient from "../../commons/api/rest-client"; // Adjust the import path as needed

const endpoint = {
    users: '/user'
};
function getAuthToken() {
    return localStorage.getItem('jwt');
}
function getAdmins(callback) {
    let request = new Request(HOST.backend_api + endpoint.users+"/admins", {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
    });

    console.log("Fetching users from: " + request.url);
    RestApiClient.performRequest(request, callback);
}



export {
    getAdmins
};
