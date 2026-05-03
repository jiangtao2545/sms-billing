import request from '@/utils/request'

export function sendSms(data) {
  return request({ method: 'post', url: '/sms/send', data })
}

export function batchSendSms(data) {
  return request({ method: 'post', url: '/sms/batch', data })
}
