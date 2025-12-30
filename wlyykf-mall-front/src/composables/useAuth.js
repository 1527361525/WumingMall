import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth.store'

export const useAuth = () => {
  const authStore = useAuthStore()
  const loading = ref(false)
  const error = ref(null)

  const handleRegister = async (formData) => {
    try {
      loading.value = true
      return await authStore.register(formData)
    } catch (err) {
      error.value = err.response?.data?.info || '注册失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const handleLogin = async (credentials) => {
    try {
      loading.value = true
      return await authStore.login(credentials)
    } catch (err) {
      error.value = err.response?.data?.info || '登录失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const handleSendCode = async (email) => {
    try {
      loading.value = true
      return await authStore.sendCode(email)
    } catch (err) {
      error.value = err.response?.data?.info || '发送验证码失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    handleRegister,
    handleLogin,
    handleSendCode,
    logout: authStore.logout,
    user: authStore.user,
    token: authStore.token
  }
}