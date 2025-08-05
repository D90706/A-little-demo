import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    //登录页面
    {
      path: '/login',
      name: 'Login',
      component: () => import("../views/Login.vue"),
      meta: {
        title: "登录"
      }
    },
    //注册页面
    {
      path: "/home",
      name: "Home",
      component: () => import("../views/Home.vue"),
      meta: {
        title: "首页"
      }
    },
    //用户信息页面
    {
      path: '/user',
      name: 'User',
      component: () => import("../views/User.vue"),
      meta: {
        title: "用户信息"
      }
    },
    {
      path:"/test",
      name:"Test",
      component:()=>import("../views/test.vue"),
      meta:{
        title:"测试页面"
      }
    },
    {
      path:"/registration",
      name:"Registration",
      component:()=>import("../views/Registration.vue"),
      meta:{
        title:"注册页面"
      }
    },
  ]
})

export default router
