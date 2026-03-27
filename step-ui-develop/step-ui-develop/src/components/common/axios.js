import axios from "axios";
const axiosInstance = axios.create({
  baseURL: "http://step.local",
  headers: {
    "Content-Type": "application/json",
  },
});

const getCookie = (name) => {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
  return null;
};

axiosInstance.interceptors.request.use(
  (config) => {
    const token = getCookie("jwtToken");
    console.log("Token from cookie:", token);
    if (token) {
      config.headers["Authorization"] = "Bearer " + token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response.status === 307) {
      window.location.href =
        error.response.data +
        "?next=" +
        window.location.pathname +
        window.location.search;
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
