import request from '@/utils/request';

export async function query() {
  return request('/api/users');
}

export async function queryCurrent() {
  return request('http://localhost:8000/zalo/api/user/me');
}
