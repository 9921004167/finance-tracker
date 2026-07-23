import { useEffect, useState, useCallback } from 'react';
import { Plus } from 'lucide-react';
import Layout from '../components/Layout';
import Modal from '../components/ui/Modal';
import TransactionForm from '../components/TransactionForm';
import TransactionList from '../components/TransactionList';
import { transactionApi } from '../api/transactionApi';
import { categoryApi } from '../api/categoryApi';
import '../components/ui/ui.css';
import './Transactions.css';

const PAGE_SIZE = 15;

export default function Transactions() {
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [filters, setFilters] = useState({ type: '', categoryId: '' });
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTxn, setEditingTxn] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  const loadCategories = useCallback(async () => {
    try {
      const data = await categoryApi.getAll();
      setCategories(data);
    } catch {
      // categories are non-critical for initial render; the form will just be empty
    }
  }, []);

  const loadTransactions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = { page, size: PAGE_SIZE };
      if (filters.type) params.type = filters.type;
      if (filters.categoryId) params.categoryId = filters.categoryId;
      const data = await transactionApi.search(params);
      setTransactions(data.content);
      setTotalPages(data.totalPages);
    } catch {
      setError('Could not load transactions. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, [page, filters]);

  useEffect(() => { loadCategories(); }, [loadCategories]);
  useEffect(() => { loadTransactions(); }, [loadTransactions]);

  const openAddModal = () => { setEditingTxn(null); setModalOpen(true); };
  const openEditModal = (txn) => { setEditingTxn(txn); setModalOpen(true); };
  const closeModal = () => setModalOpen(false);

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      if (editingTxn) {
        await transactionApi.update(editingTxn.id, payload);
      } else {
        await transactionApi.create(payload);
      }
      setModalOpen(false);
      loadTransactions();
    } catch (err) {
      alert(err.response?.data?.message || 'Could not save transaction');
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (txn) => {
    if (!window.confirm(`Delete this ${txn.type.toLowerCase()} of $${txn.amount}?`)) return;
    try {
      await transactionApi.remove(txn.id);
      loadTransactions();
    } catch {
      alert('Could not delete transaction');
    }
  };

  const handleFilterChange = (e) => {
    setPage(0);
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h1 className="page-title">Transactions</h1>
          <p className="page-subtitle">Every income and expense, in one ledger.</p>
        </div>
        <button className="btn btn-primary" onClick={openAddModal}>
          <Plus size={16} /> Add transaction
        </button>
      </div>

      <div className="card">
        <div className="txn-filters">
          <select name="type" className="field-select" value={filters.type} onChange={handleFilterChange}>
            <option value="">All types</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
          <select name="categoryId" className="field-select" value={filters.categoryId} onChange={handleFilterChange}>
            <option value="">All categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>

        {error && <div className="dashboard-error">{error}</div>}

        {!error && !loading && <TransactionList transactions={transactions} onEdit={openEditModal} onDelete={handleDelete} />}
        {loading && <div className="dashboard-loading">Loading transactions…</div>}

        {totalPages > 1 && (
          <div className="pagination">
            <button className="btn btn-ghost" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>Previous</button>
            <span className="mono">{page + 1} / {totalPages}</span>
            <button className="btn btn-ghost" disabled={page >= totalPages - 1} onClick={() => setPage((p) => p + 1)}>Next</button>
          </div>
        )}
      </div>

      {modalOpen && (
        <Modal title={editingTxn ? 'Edit transaction' : 'Add transaction'} onClose={closeModal}>
          <TransactionForm
            initial={editingTxn}
            categories={categories}
            onSubmit={handleSubmit}
            onCancel={closeModal}
            submitting={submitting}
          />
        </Modal>
      )}
    </Layout>
  );
}
