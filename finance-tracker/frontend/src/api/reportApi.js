import api, { API_BASE_URL } from './axiosConfig';

async function downloadFile(url, filenameFallback) {
  const token = localStorage.getItem('ledger_token');
  const response = await fetch(`${API_BASE_URL}${url}`, {
    headers: { Authorization: `Bearer ${token}` },
  });

  if (!response.ok) {
    throw new Error('Failed to generate report');
  }

  const disposition = response.headers.get('Content-Disposition') || '';
  const match = disposition.match(/filename="?([^";]+)"?/);
  const filename = match ? match[1] : filenameFallback;

  const blob = await response.blob();
  const objectUrl = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = objectUrl;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(objectUrl);
}

export const reportApi = {
  downloadCsv: (month, year) =>
    downloadFile(`/reports/csv?month=${month}&year=${year}`, `finance-report-${month}-${year}.csv`),
  downloadPdf: (month, year) =>
    downloadFile(`/reports/pdf?month=${month}&year=${year}`, `finance-report-${month}-${year}.pdf`),
};
