import api from './axiosConfig';

export const categoryApi = {
  getAll: () => api.get('/categories').then((res) => res.data),
  create: (data) => api.post('/categories', data).then((res) => res.data),
  remove: (id) => api.delete(`/categories/${id}`),
};
