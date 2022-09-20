import request from '@/utils/request'

export function getConsole() {
  return request({
    url: '/console',
    method: 'get'
  });
}
