import {defineStore} from 'pinia'

const defaultConfig = {
  userId: null,
  username: '',
  nickname: '',
  token: null,
  roles: [],
  avatar: '',
  gender: '',
}

export const useUserStore = defineStore('user', {
  state: () => ({
    ...defaultConfig
  }),
  actions: {
    reset() {
      for (const k in defaultConfig) {
        this[k] = defaultConfig[k]
      }
    },
    setUserInfo(data) {
      // 用户基本信息
      let user = data.user
      this.roles = data.roles
      this.userId = user.userId
      this.avatar = user.avatar
      this.username = user.username
      this.nickname = user.nickname
      this.gender = user.gender
    },
    setToken(token) {
      this.token = token
    }
  },
  persist: {
    key: 'user',
    storage: localStorage,
  },
})

