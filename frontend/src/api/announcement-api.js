import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const announcementApi = {
  page: (param) => {
    return postRequest('/announcement/page', param);
  },

  add: (param) => {
    return postRequest('/announcement/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/announcement/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/announcement/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/announcement/update', param);
  },

  get: (id) => {
    return getRequest(`/announcement/${id}`);
  }
};
