import React, { useEffect, useState } from "react"

const TestsList = () => {
    const [tests, setTests] = useState([]);

    useEffect(() => {
        fetch('http://localhost:8080/api/tests')
            .then((res) => res.json())
            .then((data) => setTests(data))
            .catch((error) => console.error('Error fetching tests:', error));
    }, []);

    return (
        <div className="container my-5">
            <h1 className="text-center mb-4">Tests disponibles</h1>
            <div className="d-flex flex-column gap-4">
                {tests.map((test, index) => (
                    <div className="card shadow-sm p-3 rounded" key={index} style={ {backgroundColor: "#f9f9f9", borderColor: "#ddd"} }>
                        <div className="d-flex justify-content-between align-items-center">
                            <div>
                                <h5 className="card-title text-primary mb-2">{test.titulo}</h5>
                                <p className="card-text text-muted mb-1">{test.descripcion}</p>
                                <p className="text-secondary fw-bold">{test.cantidadPreguntas} preguntas</p>
                            </div>
                            <button
                                className="btn btn-outline-primary"
                                onClick={() => window.location.href = `/test/${test.id}`}
                            >
                                Comenzar
                            </button>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default TestsList;
