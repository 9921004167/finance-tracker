import { Pencil, Trash2, Receipt } from 'lucide-react';
import { formatCurrency } from '../utils/format';

export default function TransactionList({ transactions, onEdit, onDelete }) {
  if (!transactions.length) {
    return (
      <div className="empty-state">
        <div className="empty-state-icon"><Receipt size={20} /></div>
        <strong>No transactions found</strong>
        <span>Add your first transaction or adjust your filters.</span>
      </div>
    );
  }

  return (
    <div className="txn-table">
      <div className="txn-table-header">
        <span>Date</span>
        <span>Category</span>
        <span>Description</span>
        <span className="align-right">Amount</span>
        <span></span>
      </div>
      {transactions.map((t) => (
        <div className="txn-row" key={t.id}>
          <span className="txn-date mono">{t.transactionDate}</span>
          <span className="txn-category">
            <span className="pill-dot" style={{ background: t.categoryColor }} />
            {t.categoryName}
          </span>
          <span className="txn-desc">{t.description || <span className="dim">—</span>}</span>
          <span className={`txn-amount mono align-right ${t.type === 'INCOME' ? 'positive' : 'negative'}`}>
            {t.type === 'INCOME' ? '+' : '−'}{formatCurrency(t.amount)}
          </span>
          <span className="txn-actions">
            <button className="icon-btn" onClick={() => onEdit(t)} aria-label="Edit">
              <Pencil size={14} />
            </button>
            <button className="icon-btn icon-btn-danger" onClick={() => onDelete(t)} aria-label="Delete">
              <Trash2 size={14} />
            </button>
          </span>
        </div>
      ))}
    </div>
  );
}
