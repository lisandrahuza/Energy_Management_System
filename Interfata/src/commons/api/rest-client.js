function performRequest(request, callback) {
    fetch(request)
        .then(response => {
            if (response.ok) {
                if (response.status === 204) {
                    callback(null, response.status, null);
                } else {
                    response.json()
                        .then(json => callback(json, response.status, null))
                        .catch(() => callback(null, response.status, { message: 'Invalid JSON response' }));
                }
            } else {
                response.json()
                    .then(err => callback(null, response.status, err))
                    .catch(() => callback(null, response.status, { message: 'Unknown error occurred' }));
            }
        })
        .catch(err => {
            callback(null, 1, { message: err.message || 'Network error occurred' });
        });
}

module.exports = {
    performRequest
};


module.exports = {
    performRequest
};
