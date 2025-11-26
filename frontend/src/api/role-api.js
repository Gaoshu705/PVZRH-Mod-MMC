import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const roleApi = {
  page: (param) => {
    return postRequest('/role/page', param);
  },

  add: (param) => {
    return postRequest('/role/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/role/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/role/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/role/update', param);
  },

  get: (id) => {
    return getRequest(`/role/${id}`);
  }
};
