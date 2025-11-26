import {defineStore} from 'pinia'
import _ from 'lodash'
import {HOME_PAGE} from "@/constants/index.js";

const defaultMenu = {
  //左侧菜单树形结构
  menuTree: [],
  //父类菜单集合，用于面包屑
  menuParentListMap: {}, // 键为menuId，值为数组，[{id:menuId, name:menuName},{id:menuId, name:menuName}]
  // 标签页
  tabNav: [],
  // 权限字符串
  codes: []
}

export const useMenuStore = defineStore('modMenu', {
  state: () => ({
    ...defaultMenu
  }),
  actions: {
    reset() {
      for (const k in defaultMenu) {
        this[k] = defaultMenu[k]
      }
    },
    setMenu(data) {
      let menuList = data.filter((e) => e.uri)
      this.codes = data.map(item => item && item.code)
          .filter(code => code !== null && code !== undefined)
      this.menuTree = buildMenuTree(menuList)
      this.menuParentListMap = buildParentMap(this.menuTree)
    },
    // 增加标签页
    setTabNav(from, to) {
      if (to.meta.hideInMenu) return
      let name = to.name
      if (name === HOME_PAGE || name === '404') return
      let menuId = to.meta.menuId
      let findTab = (this.tabNav || []).find((e) => e.menuId === menuId)
      if (findTab) {
        findTab.fromPath = from.path
        findTab.fromQuery = from.query
      } else {
        this.tabNav.push({
          menuId: menuId,
          path: to.path,
          title: to.meta.title,
          query: to.query,
          fromPath: from.path,
          fromQuery: from.query
        })
      }
    },
    //关闭标签页
    closeTabNav(menuId, closeAll) {
      if (this.tabNav == null) return
      if (!menuId && closeAll) this.tabNav = []
      else {
        let findIndex = (this.tabNav || []).findIndex((e) => e.menuId === menuId)
        if (closeAll)
          if (findIndex === -1) this.tabNav = []
          else this.tabNav = [(this.tabNav || [])[findIndex]]
        else (this.tabNav || []).splice(findIndex, 1)
      }
    }
  },
  persist: {
    key: 'modMenu',
    storage: localStorage,
  },
})

// *********************** 递归构建菜单树 *****************************
function buildMenuTree(menuList) {
  let topMenuList = menuList.filter((menu) => menu.parentId === '0' || menu.parentId === 0)
  for (const topMenu of topMenuList) {
    buildMenuChildren(topMenu, menuList)
  }
  return topMenuList
}

function buildMenuChildren(menu, menuList) {
  let children = menuList.filter((e) => e.parentId === menu.menuId)
  if (children.length === 0) return
  menu.children = children
  for (const item of children) {
    buildMenuChildren(item, menuList)
  }
}

// **********************************************************
// ********************** 构建面包屑 ******************************
function buildParentMap(menuTree) {
  let menuObj = {};
  buildChildrenMap(menuTree, [], menuObj);
  return menuObj;
}

function buildChildrenMap(menuTree, parentList, menuObj) {
  for (const e of menuTree) {
    if (e.parentId === '0' || e.parentId === 0) parentList = [];
    let cloneParentList = _.cloneDeep(parentList);
    if (e.children != null && e.menuName) {
      cloneParentList.push({menuId: e.menuId, menuName: e.menuName});
      buildChildrenMap(e.children, cloneParentList, menuObj);
    } else {
      menuObj[e.menuId] = cloneParentList;
    }
  }
}

// **********************************************************