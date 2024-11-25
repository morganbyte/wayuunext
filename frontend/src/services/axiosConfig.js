import axios from 'axios';
const API_URL = "http://localhost:8080/api";

const axiosInstance = axios.create({
    baseURL: API_URL
});

// interceptor para agregar el token a cada request
axiosInstance.interceptors.request.use(
    (config) => {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user && user.token) {
            config.headers.Authorization = 'Bearer ${user.token}';
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosInstance;
