import request from '@/utils/request'

export function getRecordList(params) {
  return request({ method: 'get', url: '/record/list', params })
}

export function getRecordDetail(id) {
  return request({ method: 'get', url: `/record/detail/${id}` })
}

export function exportRecord(params) {
  return request({ method: 'get', url: '/record/export', params, responseType: 'blob' })
}
