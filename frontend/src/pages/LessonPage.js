import React, {useEffect, useState} from "react";
import { getUserProgress, updateProgress, markLevelComplete } from "../services/ProgressService";
import ProgressBar from '../components/ProgressBar';

const LessonPage = ({ userId, levelId }) => {
    const [progress, setProgress] = useState(0);

    // cargar el progreso inicial del usuario
    useEffect(() => {
        async function loadProgress() {
            try {
                const userProgress = await getUserProgress(userId);
                const levelProgress = userProgress.find(prog => prog.nivel.id === levelId);
                setProgress(levelProgress ? levelProgress.progressPercentage : 0);
            } catch (error) {
                console.error("Error  al cargar el progreso:", error);
            }
        }
        loadProgress();
    }, [userId, levelId]);

    // actualizar progreso
    const handleProgressUpdate = async (newProgress) => {
        setProgress(newProgress);
        await updateProgress(userId, levelId, newProgress);
        if (newProgress >= 100) {
            await markLevelComplete(userId, levelId);
        }
    };

    return (
        <div>
            <h1>Lección del nivel {levelId}</h1>
            <ProgressBar progress={progress} />
            <button onClick={() => handleProgressUpdate(progress + 20)}> 
                Siguiente
            </button>
        </div>
    );
};

export default LessonPage;
