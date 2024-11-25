import axios from 'axios';

const API_URL_AUTH = "http://localhost:8080/api/auth";
const API_URL_USERS = "http://localhost:8080/api/users";

export const register = (username, password, email) => {
    return axios.post(`${API_URL_USERS}/register`, { username, password, email });
};

export const login = (username, password) => {
    return axios.post(`${API_URL_AUTH}/login`, { username, password })
        .then(response => {
            if (response.data.token) {
                localStorage.setItem("authToken", response.data.token);
            }
            return response.data;
        });
};

export const logout = () => {
    localStorage.removeItem("authtoken");
    window.location.href = "/login";
}
