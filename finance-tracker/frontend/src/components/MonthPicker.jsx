import { ChevronLeft, ChevronRight } from 'lucide-react';
import { MONTH_NAMES } from '../utils/format';
import './MonthPicker.css';

export default function MonthPicker({ month, year, onChange }) {
  const goToPrevious = () => {
    if (month === 1) onChange(12, year - 1);
    else onChange(month - 1, year);
  };

  const goToNext = () => {
    if (month === 12) onChange(1, year + 1);
    else onChange(month + 1, year);
  };

  return (
    <div className="month-picker">
      <button className="month-picker-btn" onClick={goToPrevious} aria-label="Previous month">
        <ChevronLeft size={16} />
      </button>
      <span className="month-picker-label">{MONTH_NAMES[month - 1]} {year}</span>
      <button className="month-picker-btn" onClick={goToNext} aria-label="Next month">
        <ChevronRight size={16} />
      </button>
    </div>
  );
}
