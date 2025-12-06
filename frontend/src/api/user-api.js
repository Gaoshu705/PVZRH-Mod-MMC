import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const userApi = {
  page: (param) => {
    return postRequest('/user/page', param);
  },

  add: (param) => {
    return postRequest('/user/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/user/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/user/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/user/update', param);
  },

  get: (id) => {
    return getRequest(`/user/${id}`);
  },

  getList: () => {
    return getRequest('/user/list');
  }
};
