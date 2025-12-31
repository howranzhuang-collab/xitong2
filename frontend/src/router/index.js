import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Application from '../views/Application.vue'
import AdminApplicationList from '../views/AdminApplicationList.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: false }
  },
  {
    path: '/application',
    name: 'Application',
    component: Application,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin/applications',
    name: 'AdminApplicationList',
    component: AdminApplicationList,
    meta: { requiresAuth: true } // Assuming admin is also "auth"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 简单的路由守卫，如果未来有需要认证的前端路由，可以在这里处理
router.beforeEach((to, from, next) => {
  const userJson = localStorage.getItem('user')
  const isAuthenticated = !!userJson
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 如果需要认证但未登录，强制跳转到后端登录页
    window.location.href = '/login'
  } else {
    next()
  }
})

export default router
