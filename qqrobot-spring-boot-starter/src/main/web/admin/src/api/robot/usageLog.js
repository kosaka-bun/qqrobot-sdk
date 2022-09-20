import request from '@/utils/request'

export function getUsageLogApi(page) {
  return request({
    url: '/usage_log',
    method: 'get',
    params: {
      page
    }
  });
}
