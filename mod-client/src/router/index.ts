import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';

// 懒加载路由组件
const Dashboard = () => import('../views/Dashboard.vue');
const BasicList = () => import('../views/list/BasicList.vue');
const CardList = () => import('../views/list/CardList.vue');
const FilterList = () => import('../views/list/FilterList.vue');
const TreeFilterList = () => import('../views/list/TreeFilterList.vue');
const FormPage = () => import('../views/FormPage.vue');
const DetailPage = () => import('../views/DetailPage.vue');
const ResultPage = () => import('../views/ResultPage.vue');
const UserPage = () => import('../views/UserPage.vue');
const LoginPage = () => import('../views/LoginPage.vue');
const SettingPage = () => import('../views/SettingPage.vue');
const OnlineModPage = () => import('../views/OnlineModPage.vue');
const LocalModPage = () => import('../views/LocalModPage.vue');

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    component: Dashboard,
    meta: { title: '仪表盘', key: 'item1' }
  },
  {
    path: '/list/basic',
    name: 'basicList',
    component: BasicList,
    meta: { title: '基础列表项', key: '2-1-1' }
  },
  {
    path: '/list/card',
    name: 'cardList',
    component: CardList,
    meta: { title: '卡片列表项', key: '2-1-2' }
  },
  {
    path: '/list/filter',
    name: 'filterList',
    component: FilterList,
    meta: { title: '筛选列表项', key: '2-1-3' }
  },
  {
    path: '/list/tree-filter',
    name: 'treeFilterList',
    component: TreeFilterList,
    meta: { title: '树状筛选列表项', key: '2-1-4' }
  },
  {
    path: '/form',
    name: 'form',
    component: FormPage,
    meta: { title: '表单项', key: '2-2' }
  },
  {
    path: '/detail',
    name: 'detail',
    component: DetailPage,
    meta: { title: '详情页', key: '2-3' }
  },
  {
    path: '/result',
    name: 'result',
    component: ResultPage,
    meta: { title: '结果页', key: '2-4' }
  },
  {
    path: '/user',
    name: 'user',
    component: UserPage,
    meta: { title: '个人页', key: 'item3' }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { title: '登录页', key: 'item4' }
  },
  {
    path: '/setting',
    name: 'setting',
    component: SettingPage,
    meta: { title: '设置页面', key: 'setting' }
  },
  {
    path: '/online-mod',
    name: 'onlineMod',
    component: OnlineModPage,
    meta: { title: '在线模组', key: 'online-mod' }
  },
  {
    path: '/local-mod',
    name: 'localMod',
    component: LocalModPage,
    meta: { title: '本地模组', key: 'local-mod' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局前置守卫，设置页面标题
router.beforeEach((to, _from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - MOD应用`;
  }
  next();
});

export default router;