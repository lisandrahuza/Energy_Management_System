// src/api/client-api.js

import { HOST } from '../../commons/hosts';
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    device: '/device/user/'
};
function getAuthToken() {
    return localStorage.getItem('jwt');
}
function getDevicesByUserId(userId, callback) {
    console.log("URL: " + HOST.backend_api_device);
    let request = new Request(HOST.backend_api_device + endpoint.device + userId, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        }
    });

    console.log("Fetching devices from URL: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    getDevicesByUserId
};
