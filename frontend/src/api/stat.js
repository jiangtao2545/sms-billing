import request from '@/utils/request'

export function statByApp() {
  return request({ method: 'get', url: '/stat/byApp' })
}

export function statByTime(params) {
  return request({ method: 'get', url: '/stat/byTime', params })
}
