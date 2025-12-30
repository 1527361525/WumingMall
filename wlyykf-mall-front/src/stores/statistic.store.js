// src/stores/statistic.store.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useStatisticStore = defineStore('statistic', () => {
  const totalAmount = ref(0)
  const topProducts = ref([])
  const orderStats = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 获取总销售额
  const fetchTotalAmount = async (type = 1) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get(`/statistic/getTotalAmount?type=${type}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        totalAmount.value = response.data.data.totalAmount
      } else {
        throw new Error(response.data.info || '获取销售额失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取销售额失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取热销商品TOP N
  const fetchTopProducts = async (type = 1, n = 10) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get(`/statistic/getProductTopN?type=${type}&n=${n}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        topProducts.value = response.data.data
      } else {
        throw new Error(response.data.info || '获取热销商品失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取热销商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取订单统计
  const fetchOrderStats = async (type = 1) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get(`/statistic/getAllTypeOrderCount?type=${type}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        orderStats.value = response.data.data
      } else {
        throw new Error(response.data.info || '获取订单统计失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取订单统计失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // 状态
    totalAmount,
    topProducts,
    orderStats,
    loading,
    error,
    
    // 方法
    fetchTotalAmount,
    fetchTopProducts,
    fetchOrderStats
  }
})