import { useEffect, useState, useCallback } from 'react';
import { Plus, Pencil, Trash2, PiggyBank } from 'lucide-react';
import Layout from '../components/Layout';
import MonthPicker from '../components/MonthPicker';
import Modal from '../components/ui/Modal';
import BudgetForm from '../components/BudgetForm';
import BudgetDial from '../components/charts/BudgetDial';
import { budgetApi } from '../api/budgetApi';
import { categoryApi } from '../api/categoryApi';
import '../components/charts/charts.css';
import '../components/ui/ui.css';
import './Budgets.css';

export default function Budgets() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [budgets, setBudgets] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingBudget, setEditingBudget] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const loadCategories = useCallback(async () => {
    try {
      setCategories(await categoryApi.getAll());
    } catch { /* non-critical */ }
  }, []);

  const loadBudgets = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setBudgets(await budgetApi.getForMonth(month, year));
    } catch {
      setError('Could not load budgets. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, [month, year]);

  useEffect(() => { loadCategories(); }, [loadCategories]);
  useEffect(() => { loadBudgets(); }, [loadBudgets]);

  const openAddModal = () => { setEditingBudget(null); setModalOpen(true); };
  const openEditModal = (b) => { setEditingBudget(b); setModalOpen(true); };
  const closeModal = () => setModalOpen(false);

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      if (editingBudget) {
        await budgetApi.update(editingBudget.id, payload);
      } else {
        await budgetApi.create(payload);
      }
      setModalOpen(false);
      loadBudgets();
    } catch (err) {
      alert(err.response?.data?.message || 'Could not save budget');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (b) => {
    if (!window.confirm(`Remove the budget for ${b.categoryName}?`)) return;
    try {
      await budgetApi.remove(b.id);
      loadBudgets();
    } catch {
      alert('Could not delete budget');
    }
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h1 className="page-title">Budgets</h1>
          <p className="page-subtitle">Set monthly limits and watch the dial before you overspend.</p>
        </div>
        <div className="page-header-actions">
          <MonthPicker month={month} year={year} onChange={(m, y) => { setMonth(m); setYear(y); }} />
          <button className="btn btn-primary" onClick={openAddModal}>
            <Plus size={16} /> Set budget
          </button>
        </div>
      </div>

      {error && <div className="dashboard-error">{error}</div>}
      {loading && !error && <div className="dashboard-loading">Loading budgets…</div>}

      {!loading && !error && budgets.length === 0 && (
        <div className="card">
          <div className="empty-state">
            <div className="empty-state-icon"><PiggyBank size={20} /></div>
            <strong>No budgets set for this month</strong>
            <span>Set a limit on a category to start tracking against it.</span>
          </div>
        </div>
      )}

      {!loading && !error && budgets.length > 0 && (
        <div className="budget-grid">
          {budgets.map((b) => (
            <div className={`card budget-card ${b.overBudget ? 'over-budget' : ''}`} key={b.id}>
              <BudgetDial
                label={b.categoryName}
                spent={b.spentAmount}
                limit={b.limitAmount}
                percentUsed={b.percentUsed}
                overBudget={b.overBudget}
                color={b.categoryColor}
              />
              {b.overBudget && (
                <div className="over-budget-tag">Over by {(b.percentUsed - 100).toFixed(0)}%</div>
              )}
              <div className="budget-card-actions">
                <button className="icon-btn" onClick={() => openEditModal(b)} aria-label="Edit budget">
                  <Pencil size={14} />
                </button>
                <button className="icon-btn icon-btn-danger" onClick={() => handleDelete(b)} aria-label="Delete budget">
                  <Trash2 size={14} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {modalOpen && (
        <Modal title={editingBudget ? 'Edit budget' : 'Set a budget'} onClose={closeModal}>
          <BudgetForm
            initial={editingBudget}
            categories={categories}
            defaultMonth={month}
            defaultYear={year}
            onSubmit={handleSubmit}
            onCancel={closeModal}
            submitting={submitting}
          />
        </Modal>
      )}
    </Layout>
  );
}
