import request from '@/utils/request'

export function getExceptionApi() {
  return request({
    url: '/exception',
    method: 'get'
  });
}
