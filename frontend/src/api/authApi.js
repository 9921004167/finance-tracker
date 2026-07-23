import api from './axiosConfig';

export const authApi = {
  signup: (data) => api.post('/auth/signup', data).then((res) => res.data),
  login: (data) => api.post('/auth/login', data).then((res) => res.data),
};
