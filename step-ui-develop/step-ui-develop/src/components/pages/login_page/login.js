import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  storeTokenInCookies,
  decodeToken,
  getTokenFromCookies,
  removeAllCookies,
} from "../../utils/auth";
import "../login_page/login.css";

const Login = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  if (window.location.pathname === "/logout") {
    removeAllCookies();
    navigate("/login");
  }

  if (window.location.pathname === "/session-expired") {
    removeAllCookies();
    navigate("/login");
  }

  useEffect(() => {
    const cookiesFromToken = getTokenFromCookies();
    if (cookiesFromToken) {
      const decoded = decodeToken(cookiesFromToken);
      if (decoded && ["ROLE_SA", "ROLE_P", "ROLE_U"].includes(decoded.role)) {
        navigate(
          decoded.role === "ROLE_SA"
            ? "/welcome"
            : decoded.role === "ROLE_P"
            ? "/welcome_p"
            : decoded.role === "ROLE_U"
            ? "/welcome_u"
            : "Unauthorized"
        );
      } else {
        setError("Unauthorized role or invalid token");
      }
    }

    const queryParams = new URLSearchParams(window.location.search);
    const token = queryParams.get("token");

    if (token) {
      storeTokenInCookies(token);
      console.log(token);
      const decoded = decodeToken(token);
      console.log("Decoded Token:", decoded);

      if (decoded && ["ROLE_SA", "ROLE_P", "ROLE_U"].includes(decoded.role)) {
        navigate(
          decoded.role === "ROLE_SA"
            ? "/welcome"
            : decoded.role === "ROLE_P"
            ? "/welcome_p"
            : decoded.role === "ROLE_U"
            ? "/welcome_u"
            : "Unauthorized"
        );
      } else {
        setError("Unauthorized role or invalid token");
      }
    }

    window.history.replaceState({}, document.title, window.location.pathname);
    setLoading(false);
  }, []);

  const handleLogin = () => {
    if (!sessionStorage.getItem("redirectUrl")) {
      sessionStorage.setItem("redirectUrl", window.location.pathname);
    }
    window.location.href = `http://step.local/oauth2/authorization/epam`;
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="wrapper">
      {error && <div className="error-message">{error}</div>}{" "}
      <div className="outer-container">
        <img
          className="outer-image"
          src="https://chat.lab.epam.com/api/themes/image/favicon"
          alt="Outer Image"
        />
        <button className="login-button" onClick={handleLogin}>
          <div className="inner-container">
            <img
              className="inner-image"
              src="https://authjs.dev/img/providers/keycloak.svg"
              alt="Small Image"
            />
            Sign In with SSO
          </div>
        </button>
      </div>
    </div>
  );
};

export default Login;
