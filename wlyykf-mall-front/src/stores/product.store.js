import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useProductStore = defineStore('product', () => {
  const products = ref([])
  const currentProduct = ref(null)
  const total = ref(0)
  const loading = ref(false)
  const error = ref(null)
  
  // 分页查询商品
  const fetchProducts = async (params) => {
    try {
      loading.value = true

      const response = await axios.post('/product/getProductList', params, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      })
      products.value = response.data.data
      total.value = response.data.total
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '获取商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 获取商品详情
  const fetchProductDetail = async (productId) => {
    try {
      loading.value = true

      const response = await axios.get(`/product/${productId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      currentProduct.value = response.data.data
      return response.data.data
    } catch (err) {
      error.value = err.response?.data?.info || '获取商品详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 添加商品
  const addProduct = async (productData) => {
    try {
      loading.value = true
      const response = await axios.post('/product', productData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '添加商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 更新商品
  const updateProduct = async (productData) => {
    try {
      loading.value = true
      const response = await axios.put('/product', productData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '更新商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }
  
  // 删除商品
  const deleteProduct = async (productId) => {
    try {
      loading.value = true
      const response = await axios.put(`/product/${productId}`, {}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`
        }
      })
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || '删除商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }


// 获取商品更新信息（用于编辑回显）
const fetchProductForUpdate = async (productId) => {
  try {
    loading.value = true
    const response = await axios.get(`/product/getForUpdate/${productId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.data.data
  } catch (err) {
    error.value = err.response?.data?.info || '获取商品信息失败'
    throw err
  } finally {
    loading.value = false
  }
}
  
  return {
    products,
    currentProduct,
    total,
    loading,
    error,
    fetchProducts,
    fetchProductDetail,
    fetchProductForUpdate,
    addProduct,
    updateProduct,
    deleteProduct
  }
})