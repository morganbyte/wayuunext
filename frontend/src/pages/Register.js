import React, { useState } from 'react';
import { register } from '../services/AuthService';
import { useNavigate } from 'react-router-dom';
import '../assets/register.css';
import { FaApple } from 'react-icons/fa';

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        try {
            await register(username, password, email);
            setSuccess("Registro exitoso. Redirigiendo al inicio de sesión.");
            setTimeout(() => navigate('/login'), 2000);
        } catch (error) {
            setError("Error en el registro. Por favor, intenta de nuevo.");
        }
    };

    return (
        <div className="container-fluid register-page d-flex justify-content-center align-items-center">
            <div className="register-container shadow-lg rounded overflow-hidden">
                <div className="register-image-section">
                    <img src="/images/student-image.jpg" alt="Estudiante" className="register-image"/>
                </div>
                <div className="register-form-section p-5">
                    <h2 className="register-title">Únete Hoy</h2>
                    <p className="register-subtitle">Registrarse de otra manera</p>
                    <div className="social-icons d-flex justify-content-around my-3">
                        <button className="btn btn-google"><img src="/images/google-logo.png" alt="Google Logo" className="google-logo-icon" /> Google</button>
                        <button className="btn btn-outline-dark"><FaApple /> Apple ID</button>
                    </div>
                    <div className='separator-line'></div>
                    <p className="register-subtext">O continua con tu correo eléctronico</p>
                    <form onSubmit={handleRegister}>
                        <div className="form-group mb-3">
                            <div className='input-icon'>
                                <i className='fas fa-envelope'></i>
                                <input
                                    type="email"
                                    className="form-control"
                                    placeholder="email@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                />
                            </div>
                        </div>
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
                            ¿Ya tienes cuenta? <span onClick={() => navigate('/login')} className="login-link">Inicia sesión</span>
                        </p>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Register;
