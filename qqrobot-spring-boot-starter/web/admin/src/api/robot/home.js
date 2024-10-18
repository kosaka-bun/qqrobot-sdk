import request from '@/utils/request'

export function getMainInfo() {
  return request({
    url: '/main',
    method: 'get'
  });
}

export function switchSendTestMessageOnReloginApi() {
  return request({
    url: '/switch/send_test_message',
    method: 'post'
  });
}

export function switchResendOnSendFailedApi() {
  return request({
    url: '/switch/resend_on_failed',
    method: 'post'
  });
}

export function reloginApi() {
  return request({
    url: '/action/relogin',
    method: 'post'
  });
}

export function sendTestMessageApi() {
  return request({
    url: '/action/send_test_message',
    method: 'post'
  });
}
