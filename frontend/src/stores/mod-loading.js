import {defineStore} from 'pinia'

export const usemodLoadingStore = defineStore('mod-loading', {
  state: () => ({
    loading: false
  }),
  actions: {
    hide() {
      this.loading = false
    },
    show() {
      this.loading = true
    }
  }
})
