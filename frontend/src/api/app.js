import request from '@/utils/request'

export function getAppList(params) {
  return request({ method: 'get', url: '/app/list', params })
}

export function createApp(data) {
  return request({ method: 'post', url: '/app/create', data })
}

export function updateApp(id, data) {
  return request({ method: 'put', url: `/app/update/${id}`, data })
}

export function toggleAppStatus(id, data) {
  return request({ method: 'put', url: `/app/status/${id}`, data })
}

export function resetAppKey(id) {
  return request({ method: 'post', url: `/app/resetKey/${id}` })
}

export function deleteApp(id) {
  return request({ method: 'delete', url: `/app/delete/${id}` })
}
