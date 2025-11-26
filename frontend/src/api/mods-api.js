import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const modsApi = {
  page: (param) => {
    return postRequest('/mods/page', param);
  },

  add: (param) => {
    return postRequest('/mods/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/mods/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/mods/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/mods/update', param);
  },

  get: (id) => {
    return getRequest(`/mods/${id}`);
  }
};
