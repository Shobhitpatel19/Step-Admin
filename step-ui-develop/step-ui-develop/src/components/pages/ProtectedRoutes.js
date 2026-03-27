import React, { useContext } from 'react'
import { Outlet, Navigate } from 'react-router-dom';
import { AuthContext } from './AuthContext';

const ProtectedRoutes = () => {
    const role = useContext(AuthContext);
    return  role === "SA" ? <Outlet/> : <Navigate to='/error'/>
}

export default ProtectedRoutes;
