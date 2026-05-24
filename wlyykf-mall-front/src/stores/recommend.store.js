// src/stores/recommend.store.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useRecommendStore = defineStore('recommend', () => {
  // 状态
  const collaborativeRecommendations = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 获取协同过滤推荐
  const fetchCollaborativeRecommend = async (userId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get(`/recommend/collaborative/${userId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        collaborativeRecommendations.value = response.data.data || []
      } else {
        throw new Error(response.data.info || '获取推荐商品失败')
      }

      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取推荐商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // 状态
    collaborativeRecommendations,
    loading,
    error,

    // 方法
    fetchCollaborativeRecommend
  }
})
