import { useState, useEffect } from 'react';
import { MONTH_NAMES } from '../utils/format';

export default function BudgetForm({ initial, categories, defaultMonth, defaultYear, onSubmit, onCancel, submitting }) {
  const [form, setForm] = useState({ categoryId: '', limitAmount: '', month: defaultMonth, year: defaultYear });
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initial) {
      setForm({
        categoryId: initial.categoryId,
        limitAmount: initial.limitAmount,
        month: initial.month,
        year: initial.year,
      });
    } else {
      setForm({ categoryId: '', limitAmount: '', month: defaultMonth, year: defaultYear });
    }
  }, [initial, defaultMonth, defaultYear]);

  const expenseCategories = categories.filter((c) => c.type === 'EXPENSE');

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const validate = () => {
    const errs = {};
    if (!form.categoryId) errs.categoryId = 'Choose a category';
    if (!form.limitAmount || Number(form.limitAmount) <= 0) errs.limitAmount = 'Enter a limit greater than zero';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    onSubmit({
      categoryId: Number(form.categoryId),
      limitAmount: Number(form.limitAmount),
      month: Number(form.month),
      year: Number(form.year),
    });
  };

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <div className="field">
        <label className="field-label" htmlFor="categoryId">Category</label>
        <select
          id="categoryId"
          name="categoryId"
          className="field-select"
          value={form.categoryId}
          onChange={handleChange}
          disabled={!!initial}
        >
          <option value="">Select a category</option>
          {expenseCategories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        {errors.categoryId && <span className="field-error">{errors.categoryId}</span>}
      </div>

      <div className="field">
        <label className="field-label" htmlFor="limitAmount">Monthly limit</label>
        <input
          id="limitAmount"
          name="limitAmount"
          type="number"
          step="0.01"
          min="0.01"
          className="field-input"
          value={form.limitAmount}
          onChange={handleChange}
          placeholder="0.00"
        />
        {errors.limitAmount && <span className="field-error">{errors.limitAmount}</span>}
      </div>

      <div className="field-row">
        <div className="field">
          <label className="field-label" htmlFor="month">Month</label>
          <select id="month" name="month" className="field-select" value={form.month} onChange={handleChange} disabled={!!initial}>
            {MONTH_NAMES.map((name, idx) => (
              <option key={name} value={idx + 1}>{name}</option>
            ))}
          </select>
        </div>
        <div className="field">
          <label className="field-label" htmlFor="year">Year</label>
          <input
            id="year"
            name="year"
            type="number"
            className="field-input"
            value={form.year}
            onChange={handleChange}
            disabled={!!initial}
          />
        </div>
      </div>

      <div className="modal-actions">
        <button type="button" className="btn btn-ghost" onClick={onCancel}>Cancel</button>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Saving…' : initial ? 'Save changes' : 'Set budget'}
        </button>
      </div>
    </form>
  );
}
