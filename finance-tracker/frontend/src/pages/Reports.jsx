import { useState } from 'react';
import { FileDown, FileText, FileSpreadsheet } from 'lucide-react';
import Layout from '../components/Layout';
import MonthPicker from '../components/MonthPicker';
import { reportApi } from '../api/reportApi';
import { MONTH_NAMES } from '../utils/format';
import '../components/ui/ui.css';
import './Reports.css';

export default function Reports() {
  const now = new Date();
  const [month, setMonth] = useState(now.getMonth() + 1);
  const [year, setYear] = useState(now.getFullYear());
  const [downloading, setDownloading] = useState(null); // 'csv' | 'pdf' | null
  const [error, setError] = useState(null);

  const handleDownload = async (format) => {
    setDownloading(format);
    setError(null);
    try {
      if (format === 'csv') await reportApi.downloadCsv(month, year);
      else await reportApi.downloadPdf(month, year);
    } catch {
      setError('Could not generate the report. Is the backend running?');
    } finally {
      setDownloading(null);
    }
  };

  return (
    <Layout>
      <div className="page-header">
        <div>
          <h1 className="page-title">Reports</h1>
          <p className="page-subtitle">Export a monthly statement of your income and expenses.</p>
        </div>
        <MonthPicker month={month} year={year} onChange={(m, y) => { setMonth(m); setYear(y); }} />
      </div>

      {error && <div className="dashboard-error">{error}</div>}

      <div className="report-grid">
        <div className="card report-card">
          <div className="report-icon pdf"><FileText size={22} /></div>
          <h3>PDF Statement</h3>
          <p>A formatted, printable report for {MONTH_NAMES[month - 1]} {year} — ideal for records or sharing.</p>
          <button className="btn btn-primary btn-block" onClick={() => handleDownload('pdf')} disabled={downloading === 'pdf'}>
            <FileDown size={16} /> {downloading === 'pdf' ? 'Generating…' : 'Download PDF'}
          </button>
        </div>

        <div className="card report-card">
          <div className="report-icon csv"><FileSpreadsheet size={22} /></div>
          <h3>CSV Export</h3>
          <p>Raw transaction data for {MONTH_NAMES[month - 1]} {year} — perfect for spreadsheets and further analysis.</p>
          <button className="btn btn-secondary btn-block" onClick={() => handleDownload('csv')} disabled={downloading === 'csv'}>
            <FileDown size={16} /> {downloading === 'csv' ? 'Generating…' : 'Download CSV'}
          </button>
        </div>
      </div>
    </Layout>
  );
}
