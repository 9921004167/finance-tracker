import api from './axiosConfig';

export const budgetApi = {
  getForMonth: (month, year) => api.get('/budgets', { params: { month, year } }).then((res) => res.data),
  getAll: () => api.get('/budgets/all').then((res) => res.data),
  create: (data) => api.post('/budgets', data).then((res) => res.data),
  update: (id, data) => api.put(`/budgets/${id}`, data).then((res) => res.data),
  remove: (id) => api.delete(`/budgets/${id}`),
};
