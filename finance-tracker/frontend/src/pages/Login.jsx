import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BookOpen } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import '../components/ui/ui.css';
import './Auth.css';

export default function Login() {
  const { login, loading, error } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '' });

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = async (e) => {
    e.preventDefault();
    const result = await login(form);
    if (result.success) navigate('/');
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-brand">
          <div className="brand-mark"><BookOpen size={20} strokeWidth={2.25} /></div>
          <span>Ledger</span>
        </div>

        <h1 className="auth-title">Welcome back</h1>
        <p className="auth-subtitle">Sign in to see where your money went.</p>

        {error && <div className="auth-error">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="field">
            <label className="field-label" htmlFor="username">Username</label>
            <input
              id="username"
              name="username"
              className="field-input"
              value={form.username}
              onChange={handleChange}
              placeholder="jane_doe"
              autoComplete="username"
              required
            />
          </div>
          <div className="field">
            <label className="field-label" htmlFor="password">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              className="field-input"
              value={form.password}
              onChange={handleChange}
              placeholder="••••••••"
              autoComplete="current-password"
              required
            />
          </div>
          <button className="btn btn-primary btn-block" type="submit" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>

        <div className="auth-switch">
          New to Ledger? <Link to="/signup">Create an account</Link>
        </div>
      </div>
    </div>
  );
}
