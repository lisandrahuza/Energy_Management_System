import { HOST } from '../../commons/hosts';  // Adjust the import path as needed
import RestApiClient from "../../commons/api/rest-client";

const endpoint = {
    users: '/user',
    devices: '/device',
    userReference: '/userReference'
};

// Helper function to get JWT token
function getAuthToken() {
    return localStorage.getItem('jwt');
}

function fetchUsers(callback) {
    let request = new Request(HOST.backend_api + endpoint.users, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
    });
    console.log(getAuthToken());
    console.log("Fetching users from: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function getUsers(callback) {
    let request = new Request(HOST.backend_api + endpoint.users + "/users", {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
    });
    console.log(getAuthToken());
    console.log("Fetching users from: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function createUsers(user, callback) {
    let request = new Request(HOST.backend_api + endpoint.users, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({
            username: user.username,
            name: user.name,
            isAdmin: user.isAdmin,
            password: user.password
        })
    });
    console.log(user.isAdmin);
    console.log("Creating user at: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function createDevices(device, callback) {
    let request = new Request(HOST.backend_api_device + endpoint.devices, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({
            description: device.description,
            address: device.address,
            maxenergy: device.maxPower
        })
    });
    console.log(device);
    console.log("Creating device at: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function updateUsers(user, callback) {
    let request = new Request(HOST.backend_api + endpoint.users + "/update", {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({
            id: user.id,
            username: user.username,
            name: user.name,
            isAdmin: user.isAdmin,
            password: user.password
        })
    });

    console.log("Updating user at: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function updateDevices(device, callback) {
    let request = new Request(HOST.backend_api_device + endpoint.devices + "/update", {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify({
            id: device.id,
            description: device.description,
            address: device.address,
            maxenergy: device.maxPower
        })
    });

    console.log("Updating device at: " + request.url);
    RestApiClient.performRequest(request, callback);
}

function deleteUsers(user, callback) {
    let request = new Request(`${HOST.backend_api}${endpoint.users}/${user.username}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
    });

    RestApiClient.performRequest(request, callback);
}

function deleteDevices(device, callback) {
    console.log(device.id);
    let request = new Request(`${HOST.backend_api_device}${endpoint.devices}/${device.id}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
    });

    RestApiClient.performRequest(request, callback);
}

function deleteMapping(mapping, callback) {
    let request = new Request(`${HOST.backend_api_device}${endpoint.devices}/deleteMapping/${mapping.id_device}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        }
    });

    RestApiClient.performRequest(request, callback);
}

function getUserReference(mapping, callback) {
    let request = new Request(`${HOST.backend_api_device}${endpoint.userReference}/${mapping.id_user}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },

    });
    RestApiClient.performRequest(request, callback);
}

function createMapping(response, id_device, callback) {
    console.log(response);
    console.log(id_device);
    let request = new Request(`${HOST.backend_api_device}${endpoint.devices}/${id_device}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
        body: JSON.stringify(response)
    });

    RestApiClient.performRequest(request, callback);
}

function fetchDevices(callback) {
    let request = new Request(HOST.backend_api_device + endpoint.devices, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${getAuthToken()}`
        },
    });

    console.log("Fetching devices from: " + request.url);
    RestApiClient.performRequest(request, callback);
}

export {
    fetchUsers,
    getUsers,
    createUsers,
    createDevices,
    deleteUsers,
    deleteDevices,
    fetchDevices,
    updateUsers,
    createMapping,
    getUserReference,
    deleteMapping,
    updateDevices
};
