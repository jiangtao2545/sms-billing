import request from '@/utils/request'

export function sendSms(data) {
  return request({ method: 'post', url: '/sms/send', data })
}

export function batchSendSms(data) {
  return request({ method: 'post', url: '/sms/batch', data })
}

export function parseExcel(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({ method: 'post', url: '/sms/parseExcel', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
