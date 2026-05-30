// src/stores/user.store.js
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)
  // 存储权限状态的ref
  const isAdminFlag = ref(false)
  const isSalesPersonFlag = ref(false)
  const canAccessDataAnalysisFlag = ref(false)
  const userRole = ref(null)

  // 计算属性：是否为管理员
  const isAdmin = computed(() => {
    return isAdminFlag.value
  })

  // 计算属性：是否为销售人员
  const isSalesPerson = computed(() => {
    return isSalesPersonFlag.value
  })

  // 计算属性：是否可以访问数据分析
  const canAccessDataAnalysis = computed(() => {
    return canAccessDataAnalysisFlag.value
  })

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    const token = localStorage.getItem('token')
    // 无token时跳过请求
    if (!token || token === 'null' || token === 'undefined') {
      return null
    }
    
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/user/getCurrentUser')

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

  // 检查权限（修改后的方法，返回更详细的权限信息）
  const checkIsAdmin = async () => {
    const token = localStorage.getItem('token')
    // 无token时跳过请求
    if (!token || token === 'null' || token === 'undefined') {
      isAdminFlag.value = false
      isSalesPersonFlag.value = false
      canAccessDataAnalysisFlag.value = false
      userRole.value = null
      return null
    }

    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/user/isAdmin')

      if (response.data.code === 200) {
        const data = response.data.data
        isAdminFlag.value = data.isAdmin || false
        isSalesPersonFlag.value = data.isSalesPerson || false
        canAccessDataAnalysisFlag.value = data.canAccessDataAnalysis || false
        userRole.value = data.role
      } else {
        throw new Error(response.data.info || '检查权限失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '检查权限失败'
      isAdminFlag.value = false
      isSalesPersonFlag.value = false
      canAccessDataAnalysisFlag.value = false
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

  // ============== 销售人员管理接口 ==============

  // 添加销售人员
  const addSalesPerson = async (email, nickName, password) => {
    try {
      loading.value = true
      error.value = null

      const params = new URLSearchParams()
      params.append('email', email)
      params.append('nickName', nickName)
      params.append('password', password)

      const response = await axios.post('/user/addSalesPerson', params, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.info || '添加销售人员失败')
      }
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '添加销售人员失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 删除销售人员
  const deleteSalesPerson = async (userId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.delete(`/user/salesPerson/${userId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.info || '删除销售人员失败')
      }
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '删除销售人员失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 重置用户密码
  const resetPassword = async (userId, newPassword) => {
    try {
      loading.value = true
      error.value = null

      const params = new URLSearchParams()
      params.append('newPassword', newPassword)

      const response = await axios.post(`/user/resetPassword/${userId}`, params, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.info || '重置密码失败')
      }
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '重置密码失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 查询销售人员列表
  const getSalesPersonList = async (pageNum, pageSize) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/user/getSalesPersonList', {
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
        throw new Error(response.data.info || '获取销售人员列表失败')
      }
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取销售人员列表失败'
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
    isAdminFlag.value = false
    isSalesPersonFlag.value = false
    canAccessDataAnalysisFlag.value = false
    userRole.value = null
  }

  return {
    // 状态
    user,
    loading,
    error,
    isAdmin,
    isSalesPerson,
    canAccessDataAnalysis,
    userRole,

    // 方法
    fetchCurrentUser,
    checkIsAdmin,
    updateUser,
    recharge,
    updatePassword,
    resetUser,
    getUserList,
    deleteUser,
    // 销售人员管理方法
    addSalesPerson,
    deleteSalesPerson,
    resetPassword,
    getSalesPersonList
  }
})