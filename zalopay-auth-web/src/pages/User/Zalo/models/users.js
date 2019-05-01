import { queryUsers,
  toggleUsers,
  logoutUsers,
  queryUserById,
  deleteClientRoles,
  updateClientRoles,
} from '@/services/api';

export default {
  namespace: 'zaloUsers',

  state: {
    data: {
      list: [],
      pagination: {},
    },
    queryUser: {},
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryUsers, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *toggle({ payload, callback }, { call, put }) {
      const response = yield call(toggleUsers, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      if (callback) callback();
    },
    *query({ payload }, { call, put }) {
      const response = yield call(queryUserById, payload);
      yield put({
        type: 'updateUser',
        payload: response,
      });
    },
    *deleteClientRoles({ payload }, { call, put }) {
      const response = yield call(deleteClientRoles, payload);
      yield put({
        type: 'updateUser',
        payload: response,
      });
    },
    *updateClientRoles({ payload }, { call, put }) {
      const response = yield call(updateClientRoles, payload);
      yield put({
        type: 'updateUser',
        payload: response,
      });
    },
    *logout({ payload, callback }, { call, put }) {
      const response = yield call(logoutUsers, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      // window.location.href = "http://localhost:8000/zalo-login";
      if (callback) callback();
    },
  },

  reducers: {
    save(state, action) {
      return {
        ...state,
        data: action.payload,
      };
    },
    updateUser(state, action) {
      return {
        ...state,
        queryUser: action.payload,
      };
    }
  },
};
