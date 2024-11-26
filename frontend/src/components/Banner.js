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
                    <section className="hero">
                <div className="container text-center py-5">
                    <h1 className="display-4">Bienvenido a WayuuNext: tests de aprendizaje</h1>
                    <p className="lead">
                        Descubre sobre la cultura de la cultura Wayuu a partir de diversos tests de aprendizaje categorizados por diferentes sectores de la cultura.
                    </p>
                    <button className="btn btn-primary btn-lg" onClick={() => navigate('/login')}>Inicia ahora</button>
                </div>
            </section>

            {/* Features Section */}
            <section className="features py-5 bg-light">
                <div className="container">
                    <h2 className="text-center mb-4">Nuestras características</h2>
                    <div className="row">
                        <div className="col-md-4 text-center">
                            <i className="bi bi-laptop fs-1"></i>
                            <h3 className="mt-3">Aprendizaje flexible</h3>
                            <p>Aprende a tu tiempo desde la comodidad de tu casa</p>
                        </div>
                        <div className="col-md-4 text-center">
                            <i className="bi bi-clock fs-1"></i>
                            <h3 className="mt-3">Tests interactivos</h3>
                            <p>Llenos de lecciones diseñadas por expertos</p>
                        </div>
                        <div className="col-md-4 text-center">
                            <i className="bi bi-people fs-1"></i>
                            <h3 className="mt-3">Soporte comunitario</h3>
                            <p>Conéctese con una vibrante comunidad de aprendizaje relacionado</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* About Section */}
            <section className="about py-5">
                <div className="container">
                    <h2 className="text-center">Sobre nosotros</h2>
                    <p className="text-center mt-3">
                        En WayuuNext, nos dedicamos a brindar contenido educativo de alta calidad sobre esta cultura. ¡Únase a nosotros para comenzar su viaje de aprendizaje!
                        Muy pronto mayor contenido y con seguimiento de aprendizaje dínamico.
                    </p>
                </div>
            </section>

            {/* Contact Section */}
            <section className="contact py-5 bg-light">
                <div className="container">
                    <h2 className="text-center">Contáctenos</h2>
                    <form className="mt-4">
                        <div className="row mb-3">
                            <div className="col-md-6">
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Su nombre completo"
                                    required
                                />
                            </div>
                            <div className="col-md-6">
                                <input
                                    type="email"
                                    className="form-control"
                                    placeholder="Su correo eléctronico"
                                    required
                                />
                            </div>
                        </div>
                        <textarea
                            className="form-control mb-3"
                            rows="4"
                            placeholder="Algo que quieras aportar"
                            required
                        ></textarea>
                        <div className="text-center">
                            <button type="submit" className="btn btn-primary">
                                Enviar 
                            </button>
                        </div>
                    </form>
                </div>
            </section>

            {/* Footer */}
            <footer className="text-center py-3 bg-dark text-white">
                <p>&copy; 2024 WayuuNext. All Rights Reserved.</p>
            </footer>
                </div>
            )}
        </div>
    );
};

export default Banner;
