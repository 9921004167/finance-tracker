import { createContext, useContext, useState, useCallback } from 'react';
import { authApi } from '../api/authApi';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('ledger_user');
    return stored ? JSON.parse(stored) : null;
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const persistSession = (authResponse) => {
    const { token, ...userInfo } = authResponse;
    localStorage.setItem('ledger_token', token);
    localStorage.setItem('ledger_user', JSON.stringify(userInfo));
    setUser(userInfo);
  };

  const login = useCallback(async (credentials) => {
    setLoading(true);
    setError(null);
    try {
      const data = await authApi.login(credentials);
      persistSession(data);
      return { success: true };
    } catch (err) {
      const message = err.response?.data?.message || 'Invalid username or password';
      setError(message);
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  }, []);

  const signup = useCallback(async (payload) => {
    setLoading(true);
    setError(null);
    try {
      const data = await authApi.signup(payload);
      persistSession(data);
      return { success: true };
    } catch (err) {
      const fieldErrors = err.response?.data?.fieldErrors;
      const message = fieldErrors
        ? Object.values(fieldErrors)[0]
        : err.response?.data?.message || 'Could not create account';
      setError(message);
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('ledger_token');
    localStorage.removeItem('ledger_user');
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, error, login, signup, logout, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
  return ctx;
}
