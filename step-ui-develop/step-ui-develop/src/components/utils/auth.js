import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";

export const getTokenFromCookies = () => {
  const cookieString = document.cookie;
  const cookies = cookieString.split("; ").reduce((acc, cookie) => {
    const [key, value] = cookie.split("=");
    acc[key] = value;
    return acc;
  }, {});
  return cookies.jwtToken || null;
};

export const removeAllCookies = () => {
  const allCookies = Cookies.get();
  Object.keys(allCookies).forEach((cookieName) =>
    Cookies.remove(cookieName, { path: "/" })
  );

  console.log("All cookies have been removed!");
};
export const storeTokenInCookies = (token) => {
  if (!token) {
    console.error("Invalid token. Cannot store in cookies.");
    return;
  }
  document.cookie = `jwtToken=${token}; path=/; Secure; SameSite=Lax;`;
};

export const removeTokenFromCookies = () => {
  console.log("Removing token from cookies");
  Cookies.remove("jwtToken");
};

export const decodeToken = (token) => {
  try {
    if (!token) {
      throw new Error("Invalid token. Cannot decode.");
    }
    const decoded = jwtDecode(token);
    const fullName = decoded?.name || "Guest";
    const firstName = fullName.split(" ")[0];
    const picture = decoded?.picture || null;
    const email = decoded?.email || null;
    const role = decoded?.role;

    const isDelegate = decoded?.isDelegate || false;

    return { decoded, firstName, picture, email, role, isDelegate };
  } catch (error) {
    console.error("Error decoding token:", error.message);
    return { decoded: null, firstName: "Guest" };
  }
};

export const isAuthenticated = () => {
  const token = sessionStorage.getItem("token");
  return !!token;
};
