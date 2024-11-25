import React, { useState } from 'react';
import { login } from '../services/AuthService';
import '../assets/login.css';
import { FaApple } from 'react-icons/fa';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        try {
            const response = await login(username, password);
            if (response.authToken && response.userId) {
                localStorage.setItem('authToken', response.authToken);
                localStorage.setItem('userId', response.userId);

                setSuccess('Inicio de sesión exitoso.');
                console.log('Usuario autenticado correctamente.');

                window.location.href = "/home";
            } else {
                setError('Error al iniciar sesión: Datos incompletos en la respuesta.');
                console.error('Error al iniciar sesión: Datos incompletos en la respuesta.');
            }
        } catch (error) {
            setError("Credenciales inválidas");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container-fluid login-page d-flex justify-content-center align-items-center">
            <div className="login-container shadow-lg rounded overflow-hidden">
                <div className="login-image-section">
                    <img src="/images/student-image.jpg" alt="Estudiante" className="login-image"/>
                </div>
                <div className="login-form-section p-5">
                    <h2 className="login-title">Inicia Ya</h2>
                    <p className="login-subtitle">Inicia de otra manera</p>
                    <div className="social-icons d-flex justify-content-around my-3">
                        <button className="btn btn-google"><img src="/images/google-logo.png" alt="Google Logo" className="google-logo-icon" /> Google</button>
                        <button className="btn btn-outline-dark"><FaApple /> Apple ID</button>
                    </div>
                    <div className='separator-line'></div>
                    <p className="login-subtext">O continua con tu usuario</p>
                    <form onSubmit={handleLogin}>
                        <div className="form-group mb-3">
                            <div className='input-icon'>
                                <i className='fas fa-user'></i>
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Nombre de usuario"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="form-group mb-3">
                            <div className='input-icon'>
                                <i className='fas fa-lock'></i>
                                <input
                                    type="password"
                                    className="form-control"
                                    placeholder="Contraseña"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </div>
                        </div>
                        <button type="submit" className="btn btn-primary w-100">Empieza ahora</button>
                        {error && <p className="text-danger mt-3">{error}</p>}
                        {success && <p className="text-success mt-3">{success}</p>}
                        <p className="text-center mt-3">
                            ¿Aún no tienes cuenta? <span onClick={() => window.location.href = '/register'} className="register-link">Registrarse</span>
                        </p>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Login;
