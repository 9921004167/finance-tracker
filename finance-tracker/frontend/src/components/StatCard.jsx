import { formatCurrency } from '../utils/format';
import './StatCard.css';

export default function StatCard({ label, value, icon: Icon, tone = 'default' }) {
  return (
    <div className={`stat-card stat-${tone}`}>
      <div className="stat-card-top">
        <span className="stat-card-label">{label}</span>
        {Icon && <span className="stat-card-icon"><Icon size={16} /></span>}
      </div>
      <div className="stat-card-value mono">{formatCurrency(value)}</div>
    </div>
  );
}
