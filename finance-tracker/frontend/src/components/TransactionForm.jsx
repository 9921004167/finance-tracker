import { useState, useEffect } from 'react';

const emptyForm = { amount: '', type: 'EXPENSE', description: '', transactionDate: '', categoryId: '' };

export default function TransactionForm({ initial, categories, onSubmit, onCancel, submitting }) {
  const [form, setForm] = useState(emptyForm);
  const [errors, setErrors] = useState({});

  useEffect(() => {
    if (initial) {
      setForm({
        amount: initial.amount,
        type: initial.type,
        description: initial.description || '',
        transactionDate: initial.transactionDate,
        categoryId: initial.categoryId,
      });
    } else {
      setForm({ ...emptyForm, transactionDate: new Date().toISOString().slice(0, 10) });
    }
  }, [initial]);

  const filteredCategories = categories.filter((c) => c.type === form.type);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => {
      const next = { ...prev, [name]: value };
      // Reset the category if it no longer matches the selected type
      if (name === 'type') next.categoryId = '';
      return next;
    });
  };

  const validate = () => {
    const errs = {};
    if (!form.amount || Number(form.amount) <= 0) errs.amount = 'Enter an amount greater than zero';
    if (!form.transactionDate) errs.transactionDate = 'Pick a date';
    if (!form.categoryId) errs.categoryId = 'Choose a category';
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!validate()) return;
    onSubmit({
      amount: Number(form.amount),
      type: form.type,
      description: form.description,
      transactionDate: form.transactionDate,
      categoryId: Number(form.categoryId),
    });
  };

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <div className="field-row">
        <button
          type="button"
          className={`type-toggle ${form.type === 'EXPENSE' ? 'active-expense' : ''}`}
          onClick={() => handleChange({ target: { name: 'type', value: 'EXPENSE' } })}
        >
          Expense
        </button>
        <button
          type="button"
          className={`type-toggle ${form.type === 'INCOME' ? 'active-income' : ''}`}
          onClick={() => handleChange({ target: { name: 'type', value: 'INCOME' } })}
        >
          Income
        </button>
      </div>

      <div className="field">
        <label className="field-label" htmlFor="amount">Amount</label>
        <input
          id="amount"
          name="amount"
          type="number"
          step="0.01"
          min="0.01"
          className="field-input"
          value={form.amount}
          onChange={handleChange}
          placeholder="0.00"
        />
        {errors.amount && <span className="field-error">{errors.amount}</span>}
      </div>

      <div className="field">
        <label className="field-label" htmlFor="categoryId">Category</label>
        <select
          id="categoryId"
          name="categoryId"
          className="field-select"
          value={form.categoryId}
          onChange={handleChange}
        >
          <option value="">Select a category</option>
          {filteredCategories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
        {errors.categoryId && <span className="field-error">{errors.categoryId}</span>}
      </div>

      <div className="field">
        <label className="field-label" htmlFor="transactionDate">Date</label>
        <input
          id="transactionDate"
          name="transactionDate"
          type="date"
          className="field-input"
          value={form.transactionDate}
          onChange={handleChange}
        />
        {errors.transactionDate && <span className="field-error">{errors.transactionDate}</span>}
      </div>

      <div className="field">
        <label className="field-label" htmlFor="description">Description (optional)</label>
        <input
          id="description"
          name="description"
          className="field-input"
          value={form.description}
          onChange={handleChange}
          placeholder="e.g. Weekly groceries"
        />
      </div>

      <div className="modal-actions">
        <button type="button" className="btn btn-ghost" onClick={onCancel}>Cancel</button>
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Saving…' : initial ? 'Save changes' : 'Add transaction'}
        </button>
      </div>
    </form>
  );
}
