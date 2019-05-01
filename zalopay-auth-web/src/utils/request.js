import { extend } from 'umi-request';
import { notification } from 'antd';
import router from 'umi/router';

const codeMessage = {
  200: '200',
  201: '201',
  202: '202',
  204: '204',
  302: 'CORS issue',
  400: '400',
  401: '401',
  403: '403',
  404: '404',
  406: '406',
  410: '410',
  422: '402',
  500: '500',
  502: '502',
  503: '503',
  504: '504',
};

/**
 *
 */
const errorHandler = error => {
  const { response = {} } = error;
  const errortext = codeMessage[response.status] || response.statusText;
  const { status, url } = response;

  // redirect to login page
  if (error == "TypeError: Failed to fetch" || status === 304) {
  // if (status === 304) {
    notification.error({
      message: '401 Error!',
    });
    localStorage.clear();
    // @HACK
    /* eslint-disable no-underscore-dangle */
    window.g_app._store.dispatch({
      type: 'login/logout',
    });
    // return;
    localStorage.clear();
    window.location.href = "http://localhost:8000/zalo-login";
  }

  if (status === 401) {
    notification.error({
      message: '401 Error!',
    });
    // @HACK
    /* eslint-disable no-underscore-dangle */
    window.g_app._store.dispatch({
      type: 'login/logout',
    });
    return;
  }
  notification.error({
    message: `Error ${status}: ${url}`,
    description: errortext,
  });
  // environment should not be used
  if (status === 403) {
    router.push('/exception/403');
    return;
  }
  if (status <= 504 && status >= 500) {
    router.push('/exception/500');
    return;
  }
  if (status >= 404 && status < 422) {
    router.push('/exception/404');
  }
  if (status >= 301 && status < 307) {
    router.push('/exception/302');
  }
};

/**
 * Common Request
 */
const request = extend({
  errorHandler,
  credentials: 'include',
  // headers: {
  //   'Access-Control-Allow-Origin' : '*',
  // },
});

export default request;
