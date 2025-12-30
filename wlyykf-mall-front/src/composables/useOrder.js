// src/composables/useOrder.js
import { ref, computed } from 'vue'
import { useOrderStore } from '@/stores/order.store'

export const useOrder = () => {
  const orderStore = useOrderStore()
  const currentPage = ref(1)
  const pageSize = ref(10)
  
  // 加载订单列表（普通用户）
  const loadOrders = async () => {
    orderStore.setPagination(currentPage.value, pageSize.value)
    await orderStore.fetchOrders()
  }
  
  // 加载订单列表（管理员）
  const loadAdminOrders = async () => {
    orderStore.setPagination(currentPage.value, pageSize.value)
    await orderStore.fetchAdminOrders()
  }
  
  // 删除订单
  const removeOrder = async (orderId) => {
    await orderStore.deleteOrder(orderId)
  }
  
  // 更新订单
  const updateOrder = async (orderData) => {
    await orderStore.updateOrder(orderData)
  }
  
  // 获取订单详情
  const loadOrderDetail = async (orderId) => {
    await orderStore.fetchOrderDetail(orderId)
  }
  
  // 付款
  const payOrder = async (orderId) => {
    await orderStore.payOrder(orderId)
  }
  
  // 下单
  const submitOrder = async (address) => {
    return await orderStore.submitOrder(address)
  }
  
  // 改变页码
  const changePage = (page) => {
    currentPage.value = page
    loadOrders()
  }
  
  // 改变页码（管理员）
  const changeAdminPage = (page) => {
    currentPage.value = page
    loadAdminOrders()
  }
  
  // 设置订单状态筛选
  const setOrderStatusFilter = (status) => {
    orderStore.setFilterStatus(status)
    currentPage.value = 1 // 重置到第一页
    loadOrders()
  }
  
  // 设置订单状态筛选（管理员）
  const setAdminOrderStatusFilter = (status) => {
    orderStore.setFilterStatus(status)
    currentPage.value = 1 // 重置到第一页
    loadAdminOrders()
  }

  // 设置日期范围
  const setDateRange = (startDate, endDate) => {
    orderStore.setDateRange(startDate, endDate)
    currentPage.value = 1 // 重置到第一页
    loadOrders()
  }
  
  // 设置日期范围（管理员）
  const setAdminDateRange = (startDate, endDate) => {
    orderStore.setDateRange(startDate, endDate)
    currentPage.value = 1 // 重置到第一页
    loadAdminOrders()
  }

  // 清除日期范围
  const clearDateRange = () => {
    orderStore.clearDateRange()
    currentPage.value = 1 // 重置到第一页
    loadOrders()
  }
  
  // 清除日期范围（管理员）
  const clearAdminDateRange = () => {
    orderStore.clearDateRange()
    currentPage.value = 1 // 重置到第一页
    loadAdminOrders()
  }

  // 新增立即购买方法
  const buyNow = async (buyData) => {
    return await orderStore.buyNow(buyData)
  }

  // 新增发货方法
  const deliverOrder = async (orderId) => {
    return await orderStore.deliverOrder(orderId)
  }
  
  return {
    // 状态
    orders: computed(() => orderStore.orders),
    orderDetail: computed(() => orderStore.orderDetail),
    loading: computed(() => orderStore.loading),
    error: computed(() => orderStore.error),
    total: computed(() => orderStore.pagination.total),
    currentPage,
    pageSize,
    filterStatus: computed(() => orderStore.filterStatus),
    startDate: computed(() => orderStore.startDate),
    endDate: computed(() => orderStore.endDate),
    
    // 方法
    loadOrders,
    loadAdminOrders,
    removeOrder,
    updateOrder,
    loadOrderDetail,
    payOrder,
    submitOrder,
    changePage,
    changeAdminPage,
    setOrderStatusFilter,
    setAdminOrderStatusFilter,
    setDateRange,
    setAdminDateRange,
    clearDateRange,
    clearAdminDateRange,
    buyNow,
    deliverOrder
  }
}