import api from './axiosConfig';

export const dashboardApi = {
  getSummary: (month, year) => api.get('/dashboard/summary', { params: { month, year } }).then((res) => res.data),
};
