import {postRequest, getRequest, deleteRequest, putRequest} from '@/lib/axios';

export const roleMenuApi = {
  page: (param) => {
    return postRequest('/role-menu/page', param);
  },

  add: (param) => {
    return postRequest('/role-menu/add', param);
  },

  delete: (id) => {
    return deleteRequest(`/role-menu/${id}`);
  },

  batchDelete: (ids) => {
    return postRequest(`/role-menu/batchDelete`, ids);
  },

  update: (param) => {
    return putRequest('/role-menu/update', param);
  },

  get: (id) => {
    return getRequest(`/role-menu/${id}`);
  },

  getSelectedMenu: (id) => {
    return getRequest(`/role-menu/selectedMenu/${id}`)
  },

  updateRoleTree: (param) => {
    return putRequest(`/role-menu/update/all`, param)
  }
};
