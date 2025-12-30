<template>
  <div class="user-manage-page">
    <h1>用户管理</h1>

    <!-- 用户列表 -->
    <div class="user-list-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 错误信息 -->
      <div v-else-if="error" class="error">
        {{ error }}
        <button @click="loadUserList">重试</button>
      </div>

      <!-- 用户列表为空 -->
      <div v-else-if="userList.length === 0" class="empty-users">
        <p>暂无用户</p>
      </div>

      <!-- 用户列表 -->
      <div v-else>
        <div class="total-count">共 {{ total }} 个用户</div>

        <div class="user-list">
          <div v-for="user in userList" :key="user.userId" class="user-item">
            <div class="user-header">
              <div class="user-avatar">
                <div class="avatar-placeholder">
                  {{ getAvatarPlaceholder(user.nickName) }}
                </div>
              </div>
              <div class="user-info">
                <div class="user-nickname">{{ formatNull(user.nickName) }}</div>
                <div class="user-id">ID: {{ user.userId }}</div>
              </div>
            </div>

            <div class="user-details">
              <div class="user-detail">
                <span class="detail-label">邮箱:</span>
                <span class="detail-value">{{ formatNull(user.email) }}</span>
              </div>
              <div class="user-detail">
                <span class="detail-label">手机:</span>
                <span class="detail-value">{{ formatNull(user.phone) }}</span>
              </div>
              <div class="user-detail">
                <span class="detail-label">余额:</span>
                <span class="detail-money">¥{{ formatMoney(user.money) }}</span>
              </div>
            </div>

            <div class="user-actions">
              <button
                class="view-orders-btn"
                @click="handleViewOrders(user.userId, user.nickName)"
                :disabled="viewingOrdersUserId === user.userId"
              >
                {{ viewingOrdersUserId === user.userId ? "加载中..." : "查看订单" }}
              </button>
              
              <button
                class="delete-btn"
                @click="handleDeleteUser(user.userId, user.nickName)"
                :disabled="deletingUserId === user.userId"
              >
                {{ deletingUserId === user.userId ? "删除中..." : "删除" }}
              </button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <button :disabled="pageNum <= 1" @click="changePage(pageNum - 1)">
            上一页
          </button>

          <span>{{ pageNum }} / {{ Math.ceil(total / pageSize) }}</span>

          <button
            :disabled="pageNum >= Math.ceil(total / pageSize)"
            @click="changePage(pageNum + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 订单弹窗 -->
    <div v-if="showOrderModal" class="modal" @click="closeOrderModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ currentViewingUserName }} 的订单</h2>
          <button @click="closeOrderModal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-body">
          <!-- 加载状态 -->
          <div v-if="orderLoading" class="loading">加载中...</div>
          
          <!-- 错误信息 -->
          <div v-else-if="orderError" class="error">
            {{ orderError }}
            <button @click="loadUserOrders">重试</button>
          </div>
          
          <!-- 订单为空 -->
          <div v-else-if="userOrders.length === 0" class="empty-orders">
            <p>该用户暂无订单</p>
          </div>
          
          <!-- 订单列表 -->
          <div v-else>
            <div class="total-count">共 {{ userOrdersTotal }} 个订单</div>
            
            <div class="order-list">
              <div 
                v-for="order in userOrders" 
                :key="order.orderId" 
                class="order-item"
              >
                <div class="order-header">
                  <div class="order-info">
                    <span class="order-no">订单号: {{ order.orderNo }}</span>
                    <span class="create-time">创建时间: {{ order.createTime }}</span>
                  </div>
                  <div class="order-status" :class="`status-${order.orderStatus}`">
                    {{ order.orderStatusMsg }}
                  </div>
                </div>
                
                <div class="order-content">
                  <div class="order-amount">
                    实付金额: ¥{{ formatMoney(order.totalAmount) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useUserStore } from "@/stores/user.store";
import { useOrderStore } from "@/stores/order.store";

export default {
  name: "UserManage",
  setup() {
    const userStore = useUserStore();
    const orderStore = useOrderStore();

    // 状态定义
    const userList = ref([]);
    const total = ref(0);
    const pageNum = ref(1);
    const pageSize = ref(10);
    const deletingUserId = ref(null);
    const viewingOrdersUserId = ref(null);
    const showOrderModal = ref(false);
    const userOrders = ref([]);
    const userOrdersTotal = ref(0);
    const orderLoading = ref(false);
    const orderError = ref("");
    const currentViewingUserName = ref("");

    // 计算属性
    const loading = ref(false);
    const error = ref("");

    // 加载用户列表
    const loadUserList = async () => {
      try {
        loading.value = true;
        error.value = "";

        const response = await userStore.getUserList(
          pageNum.value,
          pageSize.value
        );
        userList.value = response.data || [];
        total.value = response.total || 0;
      } catch (err) {
        error.value =
          "加载用户列表失败: " +
          (err.response?.data?.info || err.message || "未知错误");
        console.error("加载用户列表失败:", err);
      } finally {
        loading.value = false;
      }
    };

    // 格式化空值
    const formatNull = (value) => {
      return value === null || value === undefined || value === ""
        ? "【未设置】"
        : value;
    };

    // 格式化金额
    const formatMoney = (money) => {
      if (money === null || money === undefined) return "0.00";
      return parseFloat(money).toFixed(2);
    };

    // 获取资源URL
    const getResourceUrl = (sourceName) => {
      if (!sourceName) return "";
      return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(
        sourceName
      )}`;
    };

    // 获取头像占位符
    const getAvatarPlaceholder = (nickName) => {
      if (!nickName) return "U";
      return nickName.charAt(0).toUpperCase();
    };

    // 删除用户
    const handleDeleteUser = async (userId, nickName) => {
      if (
        !confirm(`确定要删除用户 "${nickName || userId}" 吗？此操作不可撤销。`)
      ) {
        return;
      }

      try {
        deletingUserId.value = userId;
        await userStore.deleteUser(userId);

        // 删除成功后刷新列表
        alert("删除成功");
        await loadUserList();
      } catch (err) {
        alert(
          `删除失败: ${err.response?.data?.info || err.message || "未知错误"}`
        );
        console.error("删除用户失败:", err);
      } finally {
        deletingUserId.value = null;
      }
    };

    // 查看用户订单
    const handleViewOrders = async (userId, nickName) => {
      try {
        viewingOrdersUserId.value = userId;
        orderLoading.value = true;
        currentViewingUserName.value = nickName || `用户${userId}`;
        orderError.value = "";
        
        // 调用订单store的fetchOrders方法，传入用户ID
        const response = await orderStore.fetchOrders({ userId });
        
        userOrders.value = response.data || [];
        userOrdersTotal.value = response.total || 0;
        showOrderModal.value = true;
      } catch (err) {
        orderError.value = "加载订单失败: " + (err.response?.data?.info || err.message || "未知错误");
        console.error("加载用户订单失败:", err);
      } finally {
        viewingOrdersUserId.value = null;
        orderLoading.value = false;
      }
    };

    // 加载用户订单（用于重试）
    const loadUserOrders = async () => {
      if (viewingOrdersUserId.value) {
        await handleViewOrders(viewingOrdersUserId.value, currentViewingUserName.value);
      }
    };

    // 关闭订单弹窗
    const closeOrderModal = () => {
      showOrderModal.value = false;
      userOrders.value = [];
      userOrdersTotal.value = 0;
      orderError.value = "";
      orderLoading.value = false;
    };

    // 分页切换
    const changePage = (newPage) => {
      pageNum.value = newPage;
      loadUserList();
    };

    // 生命周期钩子
    onMounted(() => {
      loadUserList();
    });

    return {
      // 状态
      userList,
      total,
      loading,
      error,
      pageNum,
      pageSize,
      deletingUserId,
      viewingOrdersUserId,
      showOrderModal,
      userOrders,
      userOrdersTotal,
      orderLoading,
      orderError,
      currentViewingUserName,

      // 方法
      loadUserList,
      formatNull,
      formatMoney,
      getResourceUrl,
      getAvatarPlaceholder,
      handleDeleteUser,
      handleViewOrders,
      loadUserOrders,
      closeOrderModal,
      changePage,
    };
  },
};
</script>

<style scoped>
.user-manage-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.user-list-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.loading,
.error,
.empty-users {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.error {
  color: #ff4d4f;
}

.error button {
  margin-left: 10px;
  padding: 5px 10px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.total-count {
  margin-bottom: 20px;
  font-weight: bold;
  color: #666;
  font-size: 16px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.user-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.user-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #1890ff;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-nickname {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-id {
  font-size: 12px;
  color: #999;
  font-family: "Monaco", "Consolas", monospace;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.user-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  line-height: 1.4;
}

.detail-label {
  color: #666;
  font-weight: 500;
  min-width: 40px;
}

.detail-value {
  color: #333;
  word-break: break-all;
}

.detail-money {
  color: #ff4d4f;
  font-weight: 600;
  font-size: 16px;
}

.user-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.view-orders-btn {
  padding: 6px 16px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  min-width: 80px;
}

.view-orders-btn:hover:not(:disabled) {
  background: #40a9ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(24, 144, 255, 0.3);
}

.view-orders-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.delete-btn {
  padding: 6px 16px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  min-width: 60px;
}

.delete-btn:hover:not(:disabled) {
  background: #ff7875;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(255, 77, 79, 0.3);
}

.delete-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.pagination button {
  padding: 8px 20px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.pagination button:hover:not(:disabled) {
  border-color: #1890ff;
  color: #1890ff;
  transform: translateY(-1px);
}

.pagination button:disabled {
  background: #f5f5f5;
  color: #d9d9d9;
  cursor: not-allowed;
  transform: none;
}

/* 订单弹窗样式 */
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
  color: #999;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

/* 订单列表样式 */
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
  flex-direction: column;
  gap: 5px;
}

.order-no {
  font-weight: bold;
}

.create-time {
  color: #666;
  font-size: 14px;
}

.order-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  white-space: nowrap;
}

.order-status.status-0 { background: #fff7e6; color: #fa8c16; }
.order-status.status-1 { background: #e6f7ff; color: #1890ff; }
.order-status.status-2 { background: #f9f0ff; color: #722ed1; }
.order-status.status-3 { background: #f6ffed; color: #52c41a; }

.order-content {
  padding: 15px;
}

.order-amount {
  font-weight: bold;
  font-size: 16px;
  color: #ff4d4f;
}

.total-count {
  margin-bottom: 15px;
  font-weight: bold;
  color: #666;
}

.empty-orders {
  text-align: center;
  padding: 40px 0;
  color: #999;
}

@media (max-width: 768px) {
  .user-manage-page {
    padding: 10px;
  }

  .user-list {
    grid-template-columns: 1fr;
  }

  .user-item {
    padding: 15px;
  }

  .user-header {
    padding-bottom: 10px;
  }

  .user-actions {
    flex-direction: column;
  }

  .pagination {
    flex-direction: column;
    gap: 10px;
  }

  .pagination button {
    width: 100%;
  }

  .order-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>