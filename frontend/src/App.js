import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { useState } from 'react';
import Home from './pages/Home';
import Login from './pages/Login';
import Profile from './pages/Profile';
import Register from './pages/Register';
import Header from './components/Header';
import LandingPage from './components/LandingPage';
import TestsList from './components/TestsList';
import TestPage from './components/TestPage';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(!!localStorage.getItem('authToken'));
  
  return (
    <Router>
      {isAuthenticated && <Header />}
      <Routes>
        <Route path="/" element={isAuthenticated ? <Navigate to="/home" replace /> : <LandingPage />} />
        <Route path="/home" element={isAuthenticated ? <Home /> : <Navigate to="/login" replace />} />
        <Route path="/login" element={isAuthenticated ? <Navigate to="/home" replace /> : <Login setIsAuthenticated={setIsAuthenticated} />} />
        <Route path="/register" element={isAuthenticated ? <Navigate to="/home" replace /> : <Register setIsAuthenticated={setIsAuthenticated} />} />
        <Route path="/profile" element={isAuthenticated ? <Profile /> : <Navigate to="/login" replace />} />
        <Route path="/tests" element={isAuthenticated ? <TestsList /> : <Navigate to="/login" replace />} />
        <Route path="/test/:testId" element={isAuthenticated ? <TestPage /> : <Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
