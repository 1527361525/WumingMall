import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'

// 引入Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

// 引入全局样式
import '@/assets/styles/index.scss'

// 创建应用实例
const app = createApp(App)

// 注册状态管理
const pinia = createPinia()
app.use(pinia)

// 注册路由
app.use(router)

// 注册Element Plus
app.use(ElementPlus)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局配置axios（可选）
import axios from 'axios'
axios.defaults.baseURL = import.meta.env.VITE_API_BASE_URL

// 添加响应拦截器处理响应中的code字段
axios.interceptors.response.use(
  response => {
    // 从正常响应中获取code字段
    const { code } = response.data || {}
    if (code === 401) {
      // 清除可能存在的认证信息
      localStorage.removeItem('token') // 如果有存储token的话
      // 重定向到登录页面
      router.push('/login')
      // 返回错误以便后续处理
      return Promise.reject(new Error('Unauthorized'))
    }
    return response
  },
  error => {
    // 处理HTTP错误状态码
    if (error.response && error.response.status === 401) {
      // 清除可能存在的认证信息
      localStorage.removeItem('token')
      // 重定向到登录页面
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

app.config.globalProperties.$axios = axios

// 挂载应用
app.mount('#app')