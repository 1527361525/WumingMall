// src/stores/order.store.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useOrderStore = defineStore('order', () => {
  const orders = ref([])
  const orderDetail = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const pagination = ref({
    pageNum: 1,
    pageSize: 10,
    total: 0
  })
  const filterStatus = ref('') // 订单状态筛选条件
  const startDate = ref('') // 开始日期
  const endDate = ref('')   // 结束日期

  // 获取订单列表（普通用户）
  const fetchOrders = async (params = {}) => {
    try {
      loading.value = true
      error.value = null

      // 构建请求参数
      const requestParams = {
        orderStatus: filterStatus.value,
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize,
        ...params
      }

      // 只有当日期存在时才添加到请求参数中
      if (startDate.value) {
        requestParams.startDate = startDate.value
      }
      if (endDate.value) {
        requestParams.endDate = endDate.value
      }

      const response = await axios.post('/order/getOrderList', requestParams, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        orders.value = response.data.data || []
        pagination.value.total = response.data.total || 0
      } else {
        throw new Error(response.data.info || '获取订单列表失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取订单列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取订单列表（管理员）
  const fetchAdminOrders = async (params = {}) => {
    try {
      loading.value = true
      error.value = null

      // 构建请求参数
      const requestParams = {
        orderStatus: filterStatus.value,
        pageNum: pagination.value.pageNum,
        pageSize: pagination.value.pageSize,
        ...params
      }

      // 只有当日期存在时才添加到请求参数中
      if (startDate.value) {
        requestParams.startDate = startDate.value
      }
      if (endDate.value) {
        requestParams.endDate = endDate.value
      }

      const response = await axios.post('/order/admin/getOrderList', requestParams, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        orders.value = response.data.data || []
        pagination.value.total = response.data.total || 0
      } else {
        throw new Error(response.data.info || '获取订单列表失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取订单列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 删除订单
  const deleteOrder = async (orderId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put(`/order/${orderId}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        orders.value = orders.value.filter(order => order.orderId !== orderId)
      } else {
        throw new Error(response.data.info || '删除订单失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '删除订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新订单
  const updateOrder = async (orderData) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.put('/order', orderData, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        // 更新本地数据
        const index = orders.value.findIndex(order => order.orderId === orderData.orderId)
        if (index !== -1) {
          orders.value[index] = {
            ...orders.value[index],
            ...orderData
          }
        }
      } else {
        throw new Error(response.data.info || '更新订单失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '更新订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取订单详情
  const fetchOrderDetail = async (orderId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.get(`/order/${orderId}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        orderDetail.value = response.data.data
      } else {
        throw new Error(response.data.info || '获取订单详情失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '获取订单详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 付款
  const payOrder = async (orderId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.post(`/order/pay?orderId=${orderId}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        // 更新订单状态
        const index = orders.value.findIndex(order => order.orderId === orderId)
        if (index !== -1) {
          orders.value[index].orderStatus = 1
          orders.value[index].orderStatusMsg = '已支付'
        }
        
        if (orderDetail.value && orderDetail.value.orderId === orderId) {
          orderDetail.value.orderStatus = '已支付'
        }
      } else {
        throw new Error(response.data.info || '付款失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '付款失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 下单（在购物车界面被调用）
  const submitOrder = async (address) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.post(`/order/submitOrder?address=${address}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code !== 200) {
        throw new Error(response.data.info || '下单失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '下单失败'
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

  // 设置筛选状态
  const setFilterStatus = (status) => {
    filterStatus.value = status
  }

  // 设置日期范围
  const setDateRange = (start, end) => {
    startDate.value = start
    endDate.value = end
  }

  // 清除日期范围
  const clearDateRange = () => {
    startDate.value = ''
    endDate.value = ''
  }

  // 新增立即购买方法
  const buyNow = async (buyData) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.post('/order/buy', buyData, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code !== 200) {
        throw new Error(response.data.info || '立即购买失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '立即购买失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 新增发货方法
  const deliverOrder = async (orderId) => {
    try {
      loading.value = true
      error.value = null

      const response = await axios.post(`/order/deliver?orderId=${orderId}`, {}, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })

      if (response.data.code === 200) {
        // 更新订单状态
        const index = orders.value.findIndex(order => order.orderId === orderId)
        if (index !== -1) {
          orders.value[index].orderStatus = 2
          orders.value[index].orderStatusMsg = '已发货'
        }
        
        if (orderDetail.value && orderDetail.value.orderId === orderId) {
          orderDetail.value.orderStatus = '已发货'
        }
      } else {
        throw new Error(response.data.info || '发货失败')
      }
      
      return response.data
    } catch (err) {
      error.value = err.response?.data?.info || err.message || '发货失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    // 状态
    orders,
    orderDetail,
    loading,
    error,
    pagination,
    filterStatus,
    startDate,
    endDate,
    
    // 方法
    fetchOrders,
    fetchAdminOrders,
    deleteOrder,
    updateOrder,
    fetchOrderDetail,
    payOrder,
    submitOrder,
    setPagination,
    setFilterStatus,
    setDateRange,
    clearDateRange,
    buyNow,
    deliverOrder
  }
})