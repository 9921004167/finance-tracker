import { useEffect, useState, useCallback } from 'react';
import { TrendingUp, TrendingDown, Wallet, AlertTriangle } from 'lucide-react';
import Layout from '../components/Layout';
import MonthPicker from '../components/MonthPicker';
import StatCard from '../components/StatCard';
import CategoryPieChart from '../components/charts/CategoryPieChart';
import MonthlyTrendChart from '../components/charts/MonthlyTrendChart';
import { dashboardApi } from '../api/dashboardApi';
import { formatCurrency } from '../utils/format';
import '../components/charts/charts.css';
import './Dashboard.css';

export default function Dashboard() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await dashboardApi.getSummary(month, year);
      setSummary(data);
    } catch {
      setError('Could not load dashboard data. Is the backend running?');
    } finally {
      setLoading(false);
    }
  }, [month, year]);

  useEffect(() => {
    load();
  }, [load]);

  const handleMonthChange = (m, y) => {
    setMonth(m);
    setYear(y);
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">A monthly snapshot of where your money is going.</p>
        </div>
        <MonthPicker month={month} year={year} onChange={handleMonthChange} />
      </div>

      {error && <div className="dashboard-error">{error}</div>}

      {!error && summary && (
        <>
          {summary.budgetAlerts?.length > 0 && (
            <div className="alert-banner">
              <AlertTriangle size={18} />
              <div>
                <strong>{summary.budgetAlerts.length} categor{summary.budgetAlerts.length > 1 ? 'ies are' : 'y is'} over budget</strong>
                <span>
                  {' '}{summary.budgetAlerts.map((b) => b.categoryName).join(', ')} exceeded their monthly limit.
                </span>
              </div>
            </div>
          )}

          <div className="stat-grid">
            <StatCard label="Income" value={summary.totalIncome} icon={TrendingUp} tone="income" />
            <StatCard label="Expenses" value={summary.totalExpense} icon={TrendingDown} tone="expense" />
            <StatCard label="Net Balance" value={summary.netBalance} icon={Wallet} tone="balance" />
          </div>

          <div className="dashboard-grid">
            <div className="card">
              <div className="card-title">Spending by Category</div>
              <CategoryPieChart data={summary.expenseByCategory} emptyLabel="No expenses recorded this month" />
              {summary.expenseByCategory?.length > 0 && (
                <div className="legend">
                  {summary.expenseByCategory.map((c) => (
                    <div className="legend-item" key={c.categoryId}>
                      <span className="legend-dot" style={{ background: c.color }} />
                      <span className="legend-label">{c.categoryName}</span>
                      <span className="legend-value mono">{formatCurrency(c.amount)}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="card">
              <div className="card-title">Income vs Expense — Last 6 Months</div>
              <MonthlyTrendChart data={summary.monthlyTrend} />
              <div className="trend-legend">
                <span><i className="dot mint" /> Income</span>
                <span><i className="dot coral" /> Expense</span>
              </div>
            </div>
          </div>
        </>
      )}

      {loading && !summary && <div className="dashboard-loading">Loading your dashboard…</div>}
    </Layout>
  );
}
