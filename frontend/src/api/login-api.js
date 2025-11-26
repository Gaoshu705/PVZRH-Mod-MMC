import {getRequest, postRequest} from '@/lib/axios'

export const loginApi = {
  login: (param) => {
    return postRequest('/login', param)
  },

  logout: () => {
    return getRequest('/logout')
  },

  getCaptcha: () => {
    return getRequest('/getCaptcha')
  },

  getUserInfo: () => {
    return getRequest('/user/getUserInfo')
  },

  register: (param) => {
    return postRequest('/user/register', param)
  }


}
