import axios from 'axios'
import store from '@/store'
import router from '@/router'
import { Message } from 'element-ui'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = store.state.token
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
}, error => {
  return Promise.reject(error)
})

request.interceptors.response.use(response => {
  return response.data
}, error => {
  if (error.response && error.response.status === 401) {
    store.dispatch('logout')
    router.push('/login')
    Message.error('登录已过期，请重新登录')
  } else {
    Message.error(error.response ? error.response.data.msg || '请求失败' : '网络错误')
  }
  return Promise.reject(error)
})

export default request
