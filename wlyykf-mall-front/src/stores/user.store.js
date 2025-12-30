// src/stores/user.store.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)
  // 添加一个存储管理员状态的ref
  const isAdminFlag = ref(false)

  // 计算属性：是否为管理员
  const isAdmin = computed(() => {
    return isAdminFlag.value // 使用专门的管理员标志位
  })

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/user/getCurrentUser', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        user.value = response.data.data
      } else {
        throw new Error(response.data.info || '获取用户信息失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取用户信息失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 检查是否为管理员（新添加的方法）
  const checkIsAdmin = async () => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/user/isAdmin', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        isAdminFlag.value = response.data.data.isAdmin
      } else {
        throw new Error(response.data.info || '检查管理员权限失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '检查管理员权限失败'
      isAdminFlag.value = false
      throw err
    } finally {
      loading.value = false
    }
  }

  // 分页查询用户列表
  const getUserList = async (pageNum, pageSize) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/user/getUserList', {
        params: {
          pageNum,
          pageSize
        },
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.info || '获取用户列表失败')
      }
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取用户列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 删除用户
  const deleteUser = async (userId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put(`/user/${userId}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.info || '删除用户失败')
      }
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '删除用户失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新用户信息
  const updateUser = async (userData) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put('/user', userData, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        // 更新成功后更新本地用户信息
        user.value = { ...user.value, ...userData }
      } else {
        throw new Error(response.data.info || '更新用户信息失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '更新用户信息失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 充值
  const recharge = async (rechargeMoney) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.post(`/user/recharge?rechargeMoney=${rechargeMoney}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        // 更新用户余额
        if (user.value) {
          user.value.money = response.data.data.money
        }
      } else {
        throw new Error(response.data.info || '充值失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '充值失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 修改密码
  const updatePassword = async (oldPassword, newPassword) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put(`/user/updatePassword?oldPassword=${oldPassword}&newPassword=${newPassword}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code !== 200) {
        throw new Error(response.data.info || '修改密码失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '修改密码失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 重置用户状态
  const resetUser = () => {
    user.value = null
    loading.value = false
    error.value = null
    isAdminFlag.value = false // 重置管理员标志位
  }

  return {
    // 状态
    user,
    loading,
    error,
    isAdmin,

    // 方法
    fetchCurrentUser,
    checkIsAdmin, // 导出新添加的方法
    updateUser,
    recharge,
    updatePassword,
    resetUser,
    getUserList,
    deleteUser
  }
})