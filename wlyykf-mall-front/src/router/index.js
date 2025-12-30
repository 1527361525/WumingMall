import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Auth/Login.vue'
import Register from '@/views/Auth/Register.vue'
import Home from '@/views/Home.vue'
import Cart from '@/views/Cart.vue'
import Order from '@/views/Order.vue'
import Statistic from '@/views/Statistic.vue'
import Profile from '@/views/Profile.vue'
import User from '@/views/User.vue'


const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { guestOnly: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { guestOnly: true }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: Cart,
    meta: { requiresAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: Order,
    meta: { requiresAuth: true }
  },
  {
    path: '/statistic',
    name: 'Statistic',
    component: Statistic,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: { requiresAuth: true }
  },
  {
    path: '/order-admin',
    name: 'OrderAdmin',
    component: () => import('@/views/OrderAdmin.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'User',
    component: User,
    meta: { requiresAuth: true }
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router