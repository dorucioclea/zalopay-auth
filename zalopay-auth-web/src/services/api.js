import { stringify } from 'qs';
import request from '@/utils/request';

export async function queryProjectNotice() {
  return request('/api/project/notice');
}

export async function queryActivities() {
  return request('/api/activities');
}

export async function queryRule(params) {
  return request(`/api/rule?${stringify(params)}`);
}

export async function removeRule(params) {
  return request('/api/rule', {
    method: 'POST',
    data: {
      ...params,
      method: 'delete',
    },
  });
}

export async function addRule(params) {
  return request('/api/rule', {
    method: 'POST',
    data: {
      ...params,
      method: 'post',
    },
  });
}

export async function updateRule(params = {}) {
  return request(`/api/rule?${stringify(params.query)}`, {
    method: 'POST',
    data: {
      ...params.body,
      method: 'update',
    },
  });
}

/*
 * Start Users Api
 */
export async function queryUsers() {
  return request(`http://localhost:8000/zalo/api/users`);
}

export async function toggleUsers(params) {
  return request(`http://localhost:8000/zalo/api/users/${params.realmId}/toggle`);
}

export async function logoutUsers(params) {
  return request(`http://localhost:8000/zalo/api/users/${params.realmId}/logout`);
}

export async function queryUserById(params) {
  return request(`http://localhost:8000/zalo/api/users/${params.userId}/details`);
}

export async function deleteClientRoles(params) {
  return request(`http://localhost:8000/zalo/api/users/deleterole/`, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}

export async function updateClientRoles(params) {
  return request(`http://localhost:8000/zalo/api/users/${params.userName}/roles`, {
    method: 'POST',
    data: {
      ...params,
    },
  });
}
/* End Users Api */

/*
 * Start Service Api
 */
export async function queryClient() {
  return request(`http://localhost:8000/zalo/api/clients`);
}


export async function removeClient(params) {
  return request(`http://localhost:8000/zalo/api/clients`, {
    method: 'POST',
    data: {
      ...params,
      method: 'delete',
    },
  });
}

export async function addClient(params) {
  return request(`http://localhost:8000/zalo/api/clients`, {
    method: 'POST',
    data: {
      ...params,
      method: 'post',
    },
  });
}

export async function updateClient(params) {
  return request(`http://localhost:8000/zalo/api/clients`, {
    method: 'POST',
    data: {
      ...params,
      method: 'update',
    },
  });
}
/* End Service Api */

/*
 * Start log api
 */
let sourceData;

function fetchLog(count) {
  return request(`http://localhost:8000/zalo/api/logs?page=${count}`);
}

var currentCount =0;
export async function queryLog(param) {
  // const count = param.count * 1 || 1 ;

  const result = await fetchLog(currentCount);

  currentCount = currentCount + 1;

  sourceData = result;

  return result;
}
/*
 * End log api
 */


export async function fakeSubmitForm(params) {
  return request('/api/forms', {
    method: 'POST',
    data: params,
  });
}

export async function fakeChartData() {
  return request('/api/fake_chart_data');
}

export async function queryTags() {
  return request('/api/tags');
}

export async function queryBasicProfile(id) {
  return request(`/api/profile/basic?id=${id}`);
}

export async function queryAdvancedProfile() {
  return request('/api/profile/advanced');
}

export async function queryFakeList(params) {
  return request(`/api/fake_list?${stringify(params)}`);
}

export async function removeFakeList(params) {
  const { count = 5, ...restParams } = params;
  return request(`/api/fake_list?count=${count}`, {
    method: 'POST',
    data: {
      ...restParams,
      method: 'delete',
    },
  });
}

export async function addFakeList(params) {
  const { count = 5, ...restParams } = params;
  return request(`/api/fake_list?count=${count}`, {
    method: 'POST',
    data: {
      ...restParams,
      method: 'post',
    },
  });
}

export async function updateFakeList(params) {
  const { count = 5, ...restParams } = params;
  return request(`/api/fake_list?count=${count}`, {
    method: 'POST',
    data: {
      ...restParams,
      method: 'update',
    },
  });
}

export async function accountLogin(params) {
  return request('http://localhost:8000/zalo/api/users/login', {
    method: 'POST',
    data: params,
  });
}

export async function fakeRegister(params) {
  return request('/api/register', {
    method: 'POST',
    data: params,
  });
}

export async function queryNotices(params = {}) {
  return request(`/api/notices?${stringify(params)}`);
}

export async function getFakeCaptcha(mobile) {
  return request(`/api/captcha?mobile=${mobile}`);
}
