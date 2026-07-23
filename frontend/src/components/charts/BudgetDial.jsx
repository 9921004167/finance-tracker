import { formatCurrency } from '../../utils/format';

// A 270-degree arc gauge, styled like an analog meter, going from mint (safe)
// through amber (nearing limit) to coral (over budget). This is the app's signature
// visual element used throughout the Budgets page.
export default function BudgetDial({ label, spent, limit, percentUsed, overBudget, color }) {
  const size = 148;
  const strokeWidth = 12;
  const radius = (size - strokeWidth) / 2;
  const center = size / 2;

  const startAngle = -225; // degrees
  const sweep = 270;
  const clampedPercent = Math.min(percentUsed, 100);
  const filledSweep = (clampedPercent / 100) * sweep;

  const polarToCartesian = (angleDeg) => {
    const angleRad = (angleDeg * Math.PI) / 180;
    return {
      x: center + radius * Math.cos(angleRad),
      y: center + radius * Math.sin(angleRad),
    };
  };

  const describeArc = (startDeg, endDeg) => {
    const start = polarToCartesian(startDeg);
    const end = polarToCartesian(endDeg);
    const largeArcFlag = endDeg - startDeg <= 180 ? 0 : 1;
    return `M ${start.x} ${start.y} A ${radius} ${radius} 0 ${largeArcFlag} 1 ${end.x} ${end.y}`;
  };

  const trackPath = describeArc(startAngle, startAngle + sweep);
  const fillPath = describeArc(startAngle, startAngle + filledSweep);

  const dialColor = overBudget ? 'var(--coral)' : percentUsed >= 80 ? 'var(--amber)' : (color || 'var(--mint)');

  return (
    <div className="budget-dial">
      <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`}>
        <path d={trackPath} fill="none" stroke="var(--surface-raised)" strokeWidth={strokeWidth} strokeLinecap="round" />
        {filledSweep > 0 && (
          <path
            d={fillPath}
            fill="none"
            stroke={dialColor}
            strokeWidth={strokeWidth}
            strokeLinecap="round"
            style={{ transition: 'stroke-dashoffset 0.4s ease' }}
          />
        )}
        <text x={center} y={center - 6} textAnchor="middle" className="dial-percent" fill={dialColor}>
          {Math.round(percentUsed)}%
        </text>
        <text x={center} y={center + 16} textAnchor="middle" className="dial-caption">
          of limit
        </text>
      </svg>
      <div className="budget-dial-meta">
        <span className="budget-dial-label">{label}</span>
        <span className="budget-dial-amounts mono">
          {formatCurrency(spent)} <span className="dim">/ {formatCurrency(limit)}</span>
        </span>
      </div>
    </div>
  );
}
