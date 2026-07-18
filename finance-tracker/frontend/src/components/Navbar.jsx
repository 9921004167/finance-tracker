import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, ArrowLeftRight, PiggyBank, FileDown, LogOut, BookOpen } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import './Navbar.css';

const links = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/transactions', label: 'Transactions', icon: ArrowLeftRight },
  { to: '/budgets', label: 'Budgets', icon: PiggyBank },
  { to: '/reports', label: 'Reports', icon: FileDown },
];

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const initials = (user?.fullName || user?.username || '?')
    .split(' ')
    .map((s) => s[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <div className="brand-mark"><BookOpen size={18} strokeWidth={2.25} /></div>
        <span className="brand-name">Ledger</span>
      </div>

      <div className="navbar-links">
        {links.map(({ to, label, icon: Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) => 'nav-link' + (isActive ? ' active' : '')}
          >
            <Icon size={17} strokeWidth={2} />
            <span>{label}</span>
          </NavLink>
        ))}
      </div>

      <div className="navbar-footer">
        <div className="user-chip">
          <div className="user-avatar">{initials}</div>
          <div className="user-meta">
            <span className="user-name">{user?.fullName || user?.username}</span>
            <span className="user-email">{user?.email}</span>
          </div>
        </div>
        <button className="logout-btn" onClick={handleLogout} aria-label="Log out">
          <LogOut size={16} strokeWidth={2} />
          <span>Log out</span>
        </button>
      </div>
    </nav>
  );
}
