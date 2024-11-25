import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../assets/banner.css';

const Banner = () => {
    const navigate = useNavigate();
    const isAuthenticated = !!localStorage.getItem('authToken');

    return (
        <div>
            {isAuthenticated ? (
                <div className="authenticated-content">
                    <h1>Bienvenido nuevamente a WayuuNext</h1>
                    <p>Continua tu progreso por donde lo dejaste</p>
                    <button onClick={() => navigate('/lessons')} className='cta-button'>Continuar progreso</button>
                </div>
            ) : (
                <div className='unauthenticated-content'>
                    <h1>Descubre el Poder de la Lengua Wayuu</h1>
                    <p>Sumérgete en una <strong>experiencia de aprendizaje única y explora la cultura wayuu</strong> a través de su idioma</p>
                    <button onClick={() => navigate('/login')} className='cta-button'>Comienza a aprender</button>
                </div>
            )}
        </div>
    );
};

export default Banner;
