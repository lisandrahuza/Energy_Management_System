// ClientRoute.js
import React, { useState, useEffect } from 'react';
import Client from './client/client-container';

const ClientRoute = (props) => {
    const { value } = props.match.params;
    const [isAllowed, setIsAllowed] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch(`http://localhost/users/user/check/${value}`)
            .then(response => response.json())
            .then(data => {
                setIsAllowed(data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
                setLoading(false);
            });
    }, [value]);

    if (loading) return <div>Loading...</div>;
    if (!isAllowed) return <Client />;
    return <div>Access Denied</div>;
};

export default ClientRoute;
