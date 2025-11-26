import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const userRoleApi = {
  page: (param) => {
    return postRequest('/user-role/page', param);
  },

  add: (param) => {
    return postRequest('/user-role/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/user-role/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/user-role/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/user-role/update', param);
  },

  get: (id) => {
    return getRequest(`/user-role/${id}`);
  }
};
