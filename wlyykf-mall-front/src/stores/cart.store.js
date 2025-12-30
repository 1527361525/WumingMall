// src/stores/cart.store.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const total = ref(0)
  const totalPrice = ref(0)
  const loading = ref(false)
  const error = ref(null)
  const pagination = ref({
    pageNum: 1,
    pageSize: 10,
    total: 0
  })

  // 获取购物车所有商品
const fetchCartItems = async (params = {}) => {
  try {
    loading.value = true
    error.value = null

    const response = await axios.get('/cartItem/getAll', {
      params: {
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize,
        ...params
      },
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })

    if (response.data.code === 200) {
      // 确保正确处理响应数据
      const responseData = response.data.data || []
      items.value = responseData.map(item => ({
        ...item,
        selected: item.selected === 1 // 后端返回1表示选中
      }))
      pagination.value.total = response.data.total || 0
    } else {
      throw new Error(response.data.info || '获取购物车失败')
    }
    
    return response.data
  } catch (err) {
    error.value = err.response?.data?.info || err.message || '获取购物车失败'
    throw err
  } finally {
    loading.value = false
  }
}

  // 删除购物车商品
  const deleteCartItem = async (cartItemId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put(`/cartItem/${cartItemId}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        items.value = items.value.filter(item => item.cartItemId !== cartItemId)
        await calculateTotalPrice()
      } else {
        throw new Error(response.data.info || '删除商品失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '删除商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新购物车商品
  const updateCartItem = async (cartItemData) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put('/cartItem', {
        ...cartItemData,
        selected: cartItemData.selected ? 1 : 0
      }, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        // 更新本地数据
        const index = items.value.findIndex(item => item.cartItemId === cartItemData.cartItemId)
        if (index !== -1) {
          items.value[index] = {
            ...items.value[index],
            ...cartItemData,
            selected: cartItemData.selected
          }
        }
        await calculateTotalPrice()
      } else {
        throw new Error(response.data.info || '更新商品失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '更新商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 计算选中物品总金额
  const calculateTotalPrice = async () => {
    try {
      const response = await axios.get('/cartItem/totalPrice', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        totalPrice.value = response.data.data.totalPrice
      } else {
        throw new Error(response.data.info || '计算总价失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '计算总价失败'
      throw err
    }
  }

  // 清空购物车
  const clearCart = async () => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put('/cartItem/clear', {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        items.value = []
        totalPrice.value = 0
      } else {
        throw new Error(response.data.info || '清空购物车失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '清空购物车失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 新增物品到购物车
  const addProductToCart = async (productData) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.post('/cartItem', productData, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code !== 200) {
        throw new Error(response.data.info || '添加商品失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '添加商品失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 设置分页
  const setPagination = (pageNum, pageSize) => {
    pagination.value.pageNum = pageNum
    pagination.value.pageSize = pageSize
  }

  return {
    // 状态
    items,
    total,
    totalPrice,
    loading,
    error,
    pagination,
    
    // 方法
    fetchCartItems,
    deleteCartItem,
    updateCartItem,
    calculateTotalPrice,
    clearCart,
    addProductToCart,
    setPagination
  }
})