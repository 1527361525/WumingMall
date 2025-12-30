import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  
  // 注册方法
  const register = async (formData) => {
    const { data } = await axios.post('/user/register', formData)
    return data
  }

  // 登录方法
  const login = async ({ email, password }) => {
    const { data } = await axios.post('/user/login', { email, password })
    user.value = data.data
    token.value = data.data.token
    localStorage.setItem('token', token.value)
    return data
  }

  // 发送验证码
  const sendCode = async (email) => {
    const { data } = await axios.post(`/user/sendCode?email=${email}`)
    return data
  }
  // 登出
  const logout = async () => {
    try {

      // 调用后台登出接口
      const { data } = await axios.post('/user/logout', {}, {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })
      
      // 检查响应结果
      if (data.code !== 200) {
        throw new Error(data.info || '登出失败')
      }
      
      // 清除本地状态
      user.value = null
      token.value = null
      localStorage.removeItem('token')
      
      return data
    } catch (error) {
      console.error('登出失败:', error)
      // 即使后台登出失败，也要清除本地状态
      user.value = null
      token.value = null
      localStorage.removeItem('token')
      throw error
    }
  }

  // 检查是否为管理员
  const checkIsAdmin = async () => {
    try {
      const response = await axios.get('/user/isAdmin', {
        headers: {
          Authorization: `Bearer ${token.value}`
        }
      })
      
      // 更新用户信息，添加管理员状态
      if (user.value) {
        user.value.isAdmin = response.data.data.isAdmin
      } else {
        user.value = {
          isAdmin: response.data.data.isAdmin
        }
      }
      
      return response.data.data.isAdmin
    } catch (error) {
      console.error('检查管理员权限失败:', error)
      throw error
    }
  }

  return { user, token, register, login, sendCode, logout, checkIsAdmin }
})