import { postRequest, getRequest, deleteRequest, putRequest } from '@/lib/axios';

export const fileApi = {

  upload: (file, folder) => {
    return postRequest(`/file/upload?folderType=${folder}`, file);
  },

  page: (param) => {
    return postRequest('/file/page', param);
  },

  add: (param) => {
    return postRequest('/file/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/file/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/file/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/file/update', param);
  },

  get: (id) => {
    return getRequest(`/file/${id}`);
  }
};
