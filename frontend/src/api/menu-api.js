import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const menuApi = {
  page: (param) => {
    return postRequest('/menu/page', param);
  },

  add: (param) => {
    return postRequest('/menu/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/menu/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/menu/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/menu/update', param);
  },

  get: (id) => {
    return getRequest(`/menu/${id}`);
  }
};
