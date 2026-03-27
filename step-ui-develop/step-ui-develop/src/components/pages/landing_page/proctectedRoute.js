import React from "react";
import { Navigate, Outlet ,useSearchParams} from "react-router-dom";
import { decodeToken, getTokenFromCookies } from "../../utils/auth";


const ProtectedRoute = ({ allowedRoles }) => {
  const token = getTokenFromCookies();
  const userRole = token ? decodeToken(token)?.role : null;

  console.log("in route ",token)

  if (!userRole) {
    localStorage.setItem("redirectUri",window.location.pathname+window.location.search)
    return <Navigate to="/" replace />;
  }

  if (!allowedRoles.includes(userRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
};
export default ProtectedRoute;
