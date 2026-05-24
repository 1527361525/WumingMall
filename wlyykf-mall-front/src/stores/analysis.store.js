// src/stores/analysis.store.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useAnalysisStore = defineStore('analysis', () => {
  // 状态
  const regionDistribution = ref([])
  const purchasingPower = ref([])
  const userPreference = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 获取用户地域分布
  const fetchRegionDistribution = async () => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/analysis/user/regionDistribution', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        regionDistribution.value = response.data.data || []
      } else {
        throw new Error(response.data.info || '获取地域分布失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取地域分布失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取用户购买力分层
  const fetchPurchasingPower = async () => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get('/analysis/user/purchasingPower', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        purchasingPower.value = response.data.data || []
      } else {
        throw new Error(response.data.info || '获取购买力分层失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取购买力分层失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取用户偏好分类
  const fetchUserPreference = async (userId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get(`/analysis/user/preference/${userId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        userPreference.value = response.data.data || []
      } else {
        throw new Error(response.data.info || '获取用户偏好失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取用户偏好失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // 状态
    regionDistribution,
    purchasingPower,
    userPreference,
    loading,
    error,
    
    // 方法
    fetchRegionDistribution,
    fetchPurchasingPower,
    fetchUserPreference
  }
})
