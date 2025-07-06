import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [accessToken, setAccessToken] = useState(localStorage.getItem('accessToken'));
  const [refreshToken, setRefreshToken] = useState(localStorage.getItem('refreshToken'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (accessToken) {
      // Verify token validity
      authService.verifyToken(accessToken)
        .then(() => {
          setUser({ token: accessToken });
        })
        .catch(() => {
          // Token is invalid, try to refresh
          if (refreshToken) {
            refreshAccessToken();
          } else {
            logout();
          }
        })
        .finally(() => {
          setLoading(false);
        });
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (username, password) => {
    try {
      const response = await authService.signIn(username, password);
      const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response;
      
      setAccessToken(newAccessToken);
      setRefreshToken(newRefreshToken);
      setUser({ token: newAccessToken });
      
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
      
      return { success: true };
    } catch (error) {
      return { success: false, error: error.message };
    }
  };

  const signup = async (username, password) => {
    try {
      const response = await authService.signUp(username, password);
      const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response;
      
      setAccessToken(newAccessToken);
      setRefreshToken(newRefreshToken);
      setUser({ token: newAccessToken });
      
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
      
      return { success: true };
    } catch (error) {
      return { success: false, error: error.message };
    }
  };

  const logout = () => {
    setUser(null);
    setAccessToken(null);
    setRefreshToken(null);
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  };

  const refreshAccessToken = async () => {
    try {
      const response = await authService.refreshToken(refreshToken);
      const { accessToken: newAccessToken, refreshToken: newRefreshToken } = response;
      
      setAccessToken(newAccessToken);
      setRefreshToken(newRefreshToken);
      setUser({ token: newAccessToken });
      
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
      
      return newAccessToken;
    } catch (error) {
      logout();
      throw error;
    }
  };

  const value = {
    user,
    accessToken,
    login,
    signup,
    logout,
    refreshAccessToken,
    loading
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}; 