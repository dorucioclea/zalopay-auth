export default [
  // user
  {
    path: '/user',
    component: '../layouts/UserLayout',
    routes: [
      { path: '/user', redirect: '/user/login' },
      { path: '/user/login', name: 'login', component: './User/Login' },
      {
        component: '404',
      },
    ],
  },
  // app
  {
    path: '/',
    component: '../layouts/BasicLayout',
    Routes: ['src/pages/Authorized'],
    routes: [
      // Default Route to Zalo Home
      {
        path: '/',
        redirect: '/zaloapp/home',
        authority: ['admin', 'user'] },
      // Zalo Admin App
      {
        path: '/zaloapp/home',
        name: 'zaloadmin',
        icon: 'highlight',
        component: './Dashboard/Home',
      },
      {
        path: '/zalouser/users',
        authority: ['admin'],
        name: 'zalousers',
        icon: 'user',
        component: './User/Zalo/ZaloUsers',
      },
      {
        path: '/zalouser/users/detail/:id',
        authority: ['admin'],
        name: 'zalouserdetail',
        component: './User/Zalo/ZaloUserDetail',
        hideInMenu: true,
        routes: [
          {
            path: '/zalouser/users/detail/:id',
            authority: ['admin'],
            redirect: '/zalouser/users/detail/:id/services',
          },
          {
            path: '/zalouser/users/detail/:id/services',
            authority: ['admin'],
            component: './User/Zalo/ZaloUserServicesDetail',
          },
        ],
      },
      {
        path: '/zaloservices/services',
        authority: ['admin'],
        name: 'zaloservices',
        icon: 'form',
        component: './Zaloservices/ListServices',
      },
      {
        path: '/statistic/logtracking',
        authority: ['admin'],
        name: 'statistic',
        icon: 'dashboard',
        component: './Statistic/LogTracking',
      },
      {
        component: '404',
      },
    ],
  },
];
