import request from '@/utils/request'

export function login(data) {
  return request({ method: 'post', url: '/auth/login', data })
}

export function logout() {
  return request({ method: 'post', url: '/auth/logout' })
}

export function changePassword(data) {
  return request({ method: 'post', url: '/auth/password', data })
}

export function getLoginLogs(params) {
  return request({ method: 'get', url: '/auth/loginLogs', params })
}
