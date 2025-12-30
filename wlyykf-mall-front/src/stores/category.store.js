import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref([])
  const loading = ref(false)
  const error = ref(null)
  
  // 获取分类
  const fetchCategories = async (parentId = 0) => {
    try {
      loading.value = true
      // 将 parentId 转换为字符串
      const stringParentId = String(parentId)
      
      const response = await axios.get('/category/getChildren', {
        params: { parentId: stringParentId },
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      categories.value = response.data.data
      return response.data.data
    } catch (err) {
      error.value = err.response?.data?.info || '获取分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 添加分类 (管理员)
  const addCategory = async (categoryData) => {
    try {
      loading.value = true
      // 确保 parentId 是字符串
      const stringCategoryData = {
        ...categoryData,
        parentId: categoryData.parentId !== undefined ? String(categoryData.parentId) : categoryData.parentId
      }
      
      const response = await axios.post('/category', stringCategoryData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '添加分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 更新分类 (管理员)
  const updateCategory = async (categoryData) => {
    try {
      loading.value = true
      // 确保 categoryId 和 parentId 是字符串
      const stringCategoryData = {
        ...categoryData,
        categoryId: categoryData.categoryId ? String(categoryData.categoryId) : categoryData.categoryId,
        parentId: categoryData.parentId !== undefined ? String(categoryData.parentId) : categoryData.parentId
      }
      
      const response = await axios.put('/category', stringCategoryData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '更新分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 删除分类 (管理员)
  const deleteCategory = async (categoryId) => {
    try {
      loading.value = true
      // 将 categoryId 转换为字符串
      const stringCategoryId = String(categoryId)
      
      const response = await axios.put(`/category/${stringCategoryId}`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '删除分类失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  return {
    categories,
    loading,
    error,
    fetchCategories,
    addCategory,
    updateCategory,
    deleteCategory
  }
})