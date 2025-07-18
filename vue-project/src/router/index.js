import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Manager from '../views/manager.vue'
import Data from '../views/Data.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/manager',
    },
    {
      path: '/manager',
      name: 'Manager',
      component: () => import("@/views/manager.vue"),
      children: [
        {
          path: "home",
          name: "Home",
          component: () => import("@/views/Home.vue"),
          meta: {
            title: "首页"
          }
        },
        {
          path: "data",
          name: "Data",
          component: () => import("@/views/Data.vue"),
          meta: {
            title: "数据"
          }
        },
        {
          path: "employee",
          name: "Employee",
          component: () => import("@/views/employee.vue"),
          meta:{
            title: "员工信息"
          }
        },

      ]
    },
  ],
})

export default router
