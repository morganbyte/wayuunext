import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../assets/header.css';

const Header = () => {
    const navigate = useNavigate();

    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('authToken'));

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        setIsLoggedIn(false);
        navigate('/login', { replace: true });
        window.location.reload();
    };

    useEffect(() => {
        setIsLoggedIn(!!localStorage.getItem('authToken'));
    }, []);

    return (
        <header className='header'>
            <div className='logo-nav-container'>
                <div className='logo'>
                    <Link to="/home">
                        <img src='/images/logo.svg' alt='Logo' className='logo-img' />
                    </Link>
                </div>
                <nav className='nav'>
                    {isLoggedIn ? (
                        <>
                            <Link to="/home">Inicio</Link>
                            <Link to="/tests">Tests</Link>
                        </>
                    ) : (
                        <>
                            <a href='#about'>Sobre nosotros</a>
                            <a href='#idea'>Nuestra idea</a>
                        </>
                    )}
                </nav>
            </div>
            <div className='auth-buttons'>
                {isLoggedIn ? (
                    <button onClick={handleLogout} className='logout-btn'>Cerrar sesión</button>
                ) : (
                    <>
                        <button onClick={() => navigate('/login')} className='login-btn'>Iniciar sesión</button>
                        <button onClick={() => navigate('/register')} className='signup-btn'>Crear cuenta</button>
                    </>
                )}
            </div>
        </header>
    );
};

export default Header;
