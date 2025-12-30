<!-- src/views/OrderAdmin.vue -->
<template>
  <div class="order-admin-page">
    <h1>订单管理</h1>
    
    <!-- 订单筛选条件 -->
    <div class="order-filters-container">
      <!-- 订单状态筛选 -->
      <div class="order-filters">
        <button 
          v-for="filter in orderFilters" 
          :key="filter.value"
          :class="{ active: filterStatus === filter.value }"
          @click="setAdminOrderStatusFilter(filter.value)"
        >
          {{ filter.label }}
        </button>
      </div>
      
      <!-- 日期筛选 -->
      <div class="date-filters">
        <div class="date-picker-group">
          <label>开始日期:</label>
          <input 
            type="date" 
            v-model="localStartDate" 
            @change="onDateChange"
          />
        </div>
        <div class="date-picker-group">
          <label>结束日期:</label>
          <input 
            type="date" 
            v-model="localEndDate" 
            @change="onDateChange"
          />
        </div>
        <button @click="clearDates" class="clear-dates-btn">清空日期</button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <!-- 错误信息 -->
    <div v-else-if="error" class="error">
      {{ error }}
      <button @click="loadAdminOrders">重试</button>
    </div>
    
    <!-- 订单列表为空 -->
    <div v-else-if="orders.length === 0" class="empty-orders">
      <p>暂无订单</p>
    </div>
    
    <!-- 订单列表 -->
    <div v-else>
      <div class="order-list">
        <div 
          v-for="order in orders" 
          :key="order.orderId" 
          class="order-item"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-no">订单号: {{ order.orderNo }}</span>
              <span class="user">所属用户: {{ order.nickName }}</span>
              <span class="create-time">创建时间: {{ order.createTime }}</span>
            </div>
            <div class="order-status" :class="`status-${order.orderStatus}`">
              {{ order.orderStatusMsg }}
            </div>
          </div>
          
          <div class="order-content">
            <div class="order-amount">
              实付金额: ¥{{ order.totalAmount }}
            </div>
            
            <div class="order-actions">
              <button @click="viewOrderDetail(order.orderId)" class="detail-btn">
                查看详情
              </button>
              
              <button 
                v-if="order.orderStatus === 1" 
                @click="shipOrder(order.orderId)"
                class="ship-btn"
              >
                确认发货
              </button>
              
              <!-- 修改：所有订单都显示删除按钮 -->
              <button 
                @click="removeOrder(order.orderId)"
                class="delete-btn"
              >
                删除订单
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination">
        <button 
          :disabled="currentPage <= 1" 
          @click="changeAdminPage(currentPage - 1)"
        >
          上一页
        </button>
        
        <span>{{ currentPage }} / {{ Math.ceil(total / pageSize) }}</span>
        
        <button 
          :disabled="currentPage >= Math.ceil(total / pageSize)" 
          @click="changeAdminPage(currentPage + 1)"
        >
          下一页
        </button>
      </div>
    </div>
    
    <!-- 订单详情弹窗 -->
    <div v-if="showOrderDetail" class="modal" @click="closeOrderDetail">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>订单详情</h2>
          <button @click="closeOrderDetail" class="close-btn">&times;</button>
        </div>
        
        <div v-if="orderDetail" class="modal-body">
          <div class="order-detail-info">
            <p><strong>订单号:</strong> {{ orderDetail.orderNo }}</p>
            <p><strong>订单状态:</strong> {{ orderDetail.orderStatus }}</p>
            <p><strong>收货地址:</strong> {{ orderDetail.address }}</p>
            <p><strong>创建时间:</strong> {{ orderDetail.createTime }}</p>
            <p><strong>付款时间:</strong> {{ orderDetail.paymentTime || '未付款' }}</p>
            <p><strong>总金额:</strong> ¥{{ orderDetail.totalAmount }}</p>
          </div>
          
          <div class="order-products">
            <h3>商品信息</h3>
            <div 
              v-for="product in orderDetail.products" 
              :key="product.productId"
              class="product-item"
            >
              <img 
                :src="getResourceUrl(product.productImage)" 
                :alt="product.productName"
                class="product-image"
              />
              <div class="product-info">
                <h4>{{ product.productName }}</h4>
                <p>单价: ¥{{ product.price }}</p>
                <p>数量: {{ product.quantity }}</p>
                <p>小计: ¥{{ product.totalPrice }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from 'vue'
import { useOrder } from '@/composables/useOrder'

export default {
  name: 'OrderAdmin',
  setup() {
    const {
      orders,
      orderDetail,
      loading,
      error,
      total,
      currentPage,
      pageSize,
      filterStatus,
      startDate,
      endDate,
      loadAdminOrders,
      removeOrder,
      loadOrderDetail,
      changeAdminPage,
      setAdminOrderStatusFilter,
      setAdminDateRange,
      clearAdminDateRange,
      deliverOrder
    } = useOrder()
    
    const showOrderDetail = ref(false)
    
    // 本地日期变量
    const localStartDate = ref('')
    const localEndDate = ref('')
    
    const orderFilters = [
      { label: '全部', value: '' },
      { label: '待支付', value: '0' },
      { label: '已支付', value: '1' },
      { label: '已发货', value: '2' },
      { label: '已完成', value: '3' }
    ]
    
    // 同步store中的日期值到本地ref
    watch([startDate, endDate], ([newStart, newEnd]) => {
      localStartDate.value = newStart
      localEndDate.value = newEnd
    }, { immediate: true })
    
    // 确认发货
    const shipOrder = async (orderId) => {
      if (!confirm('确认要发货吗？')) return
      
      try {
        loading.value = true
        // 使用 updateOrder 方法更新订单状态为已发货(状态码2)
        await deliverOrder(orderId)
        
        // 更新成功后重新加载订单列表
        await loadAdminOrders()
      } catch (err) {
        error.value = '发货失败: ' + (err.response?.data?.message || err.message)
      } finally {
        loading.value = false
      }
    }
    
    // 查看订单详情
    const viewOrderDetail = async (orderId) => {
      await loadOrderDetail(orderId)
      showOrderDetail.value = true
    }
    
    // 关闭订单详情
    const closeOrderDetail = () => {
      showOrderDetail.value = false
    }
    
    // 获取资源URL
    const getResourceUrl = (sourceName) => {
      if (!sourceName) return ''
      return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(sourceName)}`
    }
    
    // 日期变更处理
    const onDateChange = () => {
      setAdminDateRange(localStartDate.value, localEndDate.value)
    }
    
    // 清空日期
    const clearDates = () => {
      localStartDate.value = ''
      localEndDate.value = ''
      clearAdminDateRange()
    }
    
    // 页面加载时获取订单数据
    onMounted(async () => {
      await loadAdminOrders()
    })
    
    return {
      orders,
      orderDetail,
      loading,
      error,
      total,
      currentPage,
      pageSize,
      filterStatus,
      showOrderDetail,
      orderFilters,
      localStartDate,
      localEndDate,
      loadAdminOrders,
      removeOrder,
      viewOrderDetail,
      closeOrderDetail,
      shipOrder,
      changeAdminPage,
      setAdminOrderStatusFilter,
      onDateChange,
      clearDates,
      getResourceUrl
    }
  }
}
</script>

<style scoped>
.order-admin-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.order-filters-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  align-items: end;
  margin-bottom: 20px;
  justify-content: space-between;
}

.order-filters {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.order-filters button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.order-filters button.active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.date-filters {
  display: flex;
  gap: 15px;
  align-items: end;
  flex-wrap: wrap;
}

.date-picker-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.date-picker-group label {
  font-weight: bold;
}

.date-picker-group input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.clear-dates-btn {
  padding: 8px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  height: fit-content;
}

.loading, .error, .empty-orders {
  text-align: center;
  padding: 50px 0;
}

.error button {
  margin-left: 10px;
  padding: 5px 10px;
}

.order-list {
  margin: 20px 0;
}

.order-item {
  border: 1px solid #eee;
  border-radius: 4px;
  margin-bottom: 15px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.order-info {
  display: flex;
  flex-direction: row;
  gap: 20px;
  flex-wrap: wrap;
}

.order-info .order-no,
.order-info .user,
.order-info .create-time {
  white-space: nowrap;
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  white-space: nowrap;
}

.order-status.status-0 {
  background: #fff7e6;
  color: #fa8c16;
}

.order-status.status-1 {
  background: #e6f7ff;
  color: #1890ff;
}

.order-status.status-2 {
  background: #f9f0ff;
  color: #722ed1;
}

.order-status.status-3 {
  background: #f6ffed;
  color: #52c41a;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
}

.order-amount {
  font-weight: bold;
  font-size: 18px;
  color: #ff4d4f;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.order-actions button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.detail-btn {
  background: #1890ff;
  color: white;
}

.ship-btn {
  background: #52c41a;
  color: white;
}

.delete-btn {
  background: #ff7875;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
}

.pagination button {
  padding: 8px 15px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.order-detail-info {
  margin-bottom: 20px;
}

.order-detail-info p {
  margin: 10px 0;
}

.order-products h3 {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.product-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-info h4 {
  margin: 0 0 10px 0;
}

@media (max-width: 768px) {
  .order-filters-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .order-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .order-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .date-filters {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .product-item {
    flex-direction: column;
  }
}
</style>