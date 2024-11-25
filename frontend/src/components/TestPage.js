import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

const TestPage = () => {
    const { testId } = useParams();
    const userId = localStorage.getItem('userId');
    const [preguntas, setPreguntas] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [respuestas, setRespuestas] = useState([]);
    const [showResult, setShowResult] = useState(false);
    const [resultado, setResultado] = useState(null);
    
    if (!userId) {
        console.error("userId no está definido. Asegúrate de que el usuario haya iniciado sesión.");
    }

    // cargar preguntas del test seleccionado
    useEffect(() => {
        if (userId) {
            fetch(`http://localhost:8080/api/tests/${testId}/preguntas`)
                .then((res) => res.json())
                .then((data) => {
                    setPreguntas(data);
                })
                .catch((error) => console.error("Error al cargar preguntas:", error));
        }
    }, [testId], userId);

    // manejar respuesta seleccionada
    const handleAnswer = (selectedIndex) => {
        if (selectedIndex === undefined) {
            console.error("El id de la respuesta es undefined.");
            return;
        }

        const nuevaRespuesta = {
            preguntaId: preguntas[currentIndex].id,
            respuesta: selectedIndex,
        };
        
        setRespuestas((prevRespuestas) => {
            const nuevasRespuestas = [...prevRespuestas, nuevaRespuesta];
            console.log("Respuestas actualizadas:", nuevasRespuestas);

            if (nuevasRespuestas.length === preguntas.length) {
                setShowResult(true);
            }

            return nuevasRespuestas;
        });

        if (currentIndex + 1 < preguntas.length) {
            setCurrentIndex(currentIndex + 1);
        } else {
            console.log("Última pregunta respondida, enviando resultados.");
            fetch(`http://localhost:8080/api/tests/${testId}/evaluar`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify([...respuestas, nuevaRespuesta]),
            })
                .then((res) => res.json())
                .then((resultado) => {
                    console.log("Resultado del test:", resultado);
                    setResultado(resultado);
            })
            .catch((error) => {
                console.error("Error al evaluar test:", error);
            });
        }
    };

    console.log("Respuestas enviadas al backend:", respuestas);

    // enviar respuestas al backend
    const enviarRespuestas = () => {
        fetch(`http://localhost:8080/api/tests/${testId}/evaluar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(respuestas),
        })
            .then((res) => res.json())
            .then((resultado) => {
                console.log("Resultado del test:", resultado);
                if (resultado.puntuacion >= 70) {
                    alert(`¡Test aprobado! Puntuación: ${resultado.puntuacion}%`);
                } else {
                    alert(`Test no aprobado. Puntuación: ${resultado.puntuacion}%`);
                }
                fetch(`http://localhost:8080/api/progress/update?userId=${userId}`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json", "Authorization": `Bearer ${localStorage.getItem('authToken')}` },
                    body: JSON.stringify({ testId, puntuacion: resultado.puntuacion }),
                })
                    .then((res) => res.json())
                    .then((data) => {
                        console.log("Progreso actualizado: ", data);
                    })
                    .catch((error) => console.error("Error al actualizar progreso:", error));
            })
            .catch((error) => console.error("Error al evaluar test", error));
    };

    useEffect(() => {
        if (showResult && !resultado) {
            enviarRespuestas();
        }
    }, [showResult, resultado, enviarRespuestas]);

    return (
        <div>
            {showResult ? (
                resultado ? (
                    <div className="container mt-5">
                        <h1 className="text-primary mb-4">Resultados</h1>
                        <h4 className="mb-4">Puntuación: <span className="text-success">{resultado.puntuacion}%</span></h4>
                        <ul className="list-group">
                            {resultado.detalle.map((detalle, index) => (
                                <li key={index} className={`list-group-item ${detalle.esCorrecta ? 'list-group-item-success' : 'list-group-item-danger'}`}>
                                    <h5>{detalle.pregunta}</h5>
                                    <p className="mb-1"><strong>Tu respuesta:</strong> {detalle.respuestaUsuario}</p>
                                    <p className="mb-1"><strong>Respuesta correcta:</strong> {detalle.respuestaCorrecta}</p>
                                    <p><strong>{detalle.esCorrecta ? "Correcto" : "Incorrecto"}</strong></p>
                                </li>
                            ))}
                        </ul>
                    </div>
                ) : (
                    <p>Calculando resultados...</p>
                )
            ) : preguntas.length > 0 ? (
                <div className="container mt-5">
                    <h1 className="mb-4 text-primary">Test</h1>
                    <div className="card p-4 shadow-sm">
                        <h4 className="mb-3">Pregunta {currentIndex + 1} de {preguntas.length}</h4>
                        <p className="fs-5 mb-4">{preguntas[currentIndex]?.texto}</p>
                        <div className="d-grid gap-3">
                            {preguntas[currentIndex]?.opciones.map((opcion) => (
                                <button
                                    key={opcion.indice}
                                    className="btn btn-outline-primary btn-lg text-start"
                                    onClick={() => handleAnswer(opcion.indice)}
                                >
                                    {opcion.texto}
                                </button>
                            ))}
                        </div>
                    </div>
                </div>
            ) : (
                <p>Cargando preguntas...</p>
            )}
        </div>
    );
};

export default TestPage;
/*
<div>
    <h3>
        Pregunta {currentIndex + 1} de {preguntas.length}
    </h3>
    <p>{preguntas[currentIndex].texto}</p>
    <div>
        {preguntas[currentIndex].opciones.map((opcion) => {
            return (
                <div key={opcion.indice}>
                    <button onClick={() => handleAnswer(opcion.indice)}>{opcion.texto}</button>
                </div>
        );
    })}
    </div>
</div> */