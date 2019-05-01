import { queryClient, addClient, removeClient, updateClient } from '@/services/api';

export default {
  namespace: 'services',

  state: {
    list: [],
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryClient, payload);
      yield put({
        type: 'queryList',
        payload: Array.isArray(response) ? response : [],
      });
    },
    *submit({ payload }, { call, put }) {
      let callback;
      let response;
      if (payload.id === "") {
        callback = addClient;
        response = yield call(callback, payload); // post
      } else {
        callback = payload.type === "removeClient" ? removeClient : updateClient;
        response = yield call(callback, payload); // post
      }

      yield put({
        type: 'queryList',
        payload: response,
      });
    },
  },

  reducers: {
    queryList(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
    appendList(state, action) {
      return {
        ...state,
        list: state.list.concat(action.payload),
      };
    },
  },
};

