import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/stat',
    children: [
      {
        path: 'app',
        name: 'AppList',
        component: () => import('@/views/app/AppList.vue'),
        meta: { title: '应用管理' }
      },
      {
        path: 'sms',
        name: 'SmsSend',
        component: () => import('@/views/sms/SmsSend.vue'),
        meta: { title: '发送短信' }
      },
      {
        path: 'record',
        name: 'RecordList',
        component: () => import('@/views/record/RecordList.vue'),
        meta: { title: '发送记录' }
      },
      {
        path: 'stat',
        name: 'StatReport',
        component: () => import('@/views/stat/StatReport.vue'),
        meta: { title: '统计报表' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

router.beforeEach((to, from, next) => {
  const token = store.state.token
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
