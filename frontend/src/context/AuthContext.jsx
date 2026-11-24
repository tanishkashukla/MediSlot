import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('medislot_token');
    const savedUser = localStorage.getItem('medislot_user');
    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('medislot_token');
        localStorage.removeItem('medislot_user');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { email, password });
    const data = response.data;
    localStorage.setItem('medislot_token', data.token);
    const userData = {
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
      profileId: data.profileId,
    };
    localStorage.setItem('medislot_user', JSON.stringify(userData));
    setToken(data.token);
    setUser(userData);
    return userData;
  };

  const register = async (registerData) => {
    const response = await api.post('/auth/register', registerData);
    const data = response.data;
    localStorage.setItem('medislot_token', data.token);
    const userData = {
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
      profileId: data.profileId,
    };
    localStorage.setItem('medislot_user', JSON.stringify(userData));
    setToken(data.token);
    setUser(userData);
    return userData;
  };

  const logout = () => {
    localStorage.removeItem('medislot_token');
    localStorage.removeItem('medislot_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
