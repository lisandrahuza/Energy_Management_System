
import React, { useState, useEffect } from 'react';
import Admin from './admin/admin-container';

const AdminRoute = (props) => {
    const { value } = props.match.params;
    const [isAuthorized, setIsAuthorized] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch(`http://localhost/users/user/check/${value}`)
            .then(response => response.json())
            .then(data => {
                console.log(data);
                setIsAuthorized(data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error:', error);
                setLoading(false);
            });
    }, [value]);

    if (loading) return <div>Loading...</div>;
    if (isAuthorized) return <Admin />;
    return <div>Access Denied</div>;
};

export default AdminRoute;
