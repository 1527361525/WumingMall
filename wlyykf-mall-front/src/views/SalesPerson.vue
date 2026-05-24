<template>
  <div class="sales-person-page">
    <h1>销售人员管理</h1>

    <!-- 操作栏 -->
    <div class="action-bar">
      <button class="add-btn" @click="showAddModal = true">
        <span class="icon">+</span> 添加销售人员
      </button>
    </div>

    <!-- 销售人员列表 -->
    <div class="sales-person-list-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading">加载中...</div>

      <!-- 错误信息 -->
      <div v-else-if="error" class="error">
        {{ error }}
        <button @click="loadSalesPersonList">重试</button>
      </div>

      <!-- 列表为空 -->
      <div v-else-if="salesPersonList.length === 0" class="empty-list">
        <p>暂无销售人员</p>
        <button class="add-btn" @click="showAddModal = true">添加第一个销售人员</button>
      </div>

      <!-- 列表 -->
      <div v-else>
        <div class="total-count">共 {{ total }} 个销售人员</div>

        <div class="sales-person-list">
          <div v-for="person in salesPersonList" :key="person.userId" class="sales-person-item">
            <div class="person-header">
              <div class="person-avatar">
                <div class="avatar-placeholder">
                  {{ getAvatarPlaceholder(person.nickName) }}
                </div>
              </div>
              <div class="person-info">
                <div class="person-nickname">{{ formatNull(person.nickName) }}</div>
                <div class="person-id">ID: {{ person.userId }}</div>
              </div>
              <div class="role-tag">销售人员</div>
            </div>

            <div class="person-details">
              <div class="person-detail">
                <span class="detail-label">邮箱:</span>
                <span class="detail-value">{{ formatNull(person.email) }}</span>
              </div>
              <div class="person-detail">
                <span class="detail-label">手机:</span>
                <span class="detail-value">{{ formatNull(person.phone) }}</span>
              </div>
              <div class="person-detail">
                <span class="detail-label">余额:</span>
                <span class="detail-money">¥{{ formatMoney(person.money) }}</span>
              </div>
            </div>

            <div class="person-actions">
              <button
                class="reset-pwd-btn"
                @click="handleResetPassword(person.userId, person.nickName)"
                :disabled="resettingId === person.userId"
              >
                {{ resettingId === person.userId ? "重置中..." : "重置密码" }}
              </button>
              
              <button
                class="delete-btn"
                @click="handleDeletePerson(person.userId, person.nickName)"
                :disabled="deletingId === person.userId"
              >
                {{ deletingId === person.userId ? "删除中..." : "删除" }}
              </button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination">
          <button :disabled="pageNum <= 1" @click="changePage(pageNum - 1)">
            上一页
          </button>

          <span>{{ pageNum }} / {{ Math.ceil(total / pageSize) || 1 }}</span>

          <button
            :disabled="pageNum >= Math.ceil(total / pageSize)"
            @click="changePage(pageNum + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 添加销售人员弹窗 -->
    <div v-if="showAddModal" class="modal" @click="closeAddModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>添加销售人员</h2>
          <button @click="closeAddModal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="handleAddPerson">
            <div class="form-group">
              <label>邮箱 <span class="required">*</span></label>
              <input 
                type="email" 
                v-model="addForm.email" 
                placeholder="请输入邮箱"
                required
              />
            </div>
            
            <div class="form-group">
              <label>昵称 <span class="required">*</span></label>
              <input 
                type="text" 
                v-model="addForm.nickName" 
                placeholder="请输入昵称"
                required
              />
            </div>
            
            <div class="form-group">
              <label>密码 <span class="required">*</span></label>
              <input 
                type="password" 
                v-model="addForm.password" 
                placeholder="请输入密码"
                required
                minlength="6"
              />
              <span class="form-hint">密码至少6位</span>
            </div>

            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeAddModal">取消</button>
              <button type="submit" class="submit-btn" :disabled="adding">
                {{ adding ? "添加中..." : "确认添加" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 重置密码弹窗 -->
    <div v-if="showResetModal" class="modal" @click="closeResetModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>重置密码 - {{ resetTargetName }}</h2>
          <button @click="closeResetModal" class="close-btn">&times;</button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="confirmResetPassword">
            <div class="form-group">
              <label>新密码 <span class="required">*</span></label>
              <input 
                type="password" 
                v-model="resetForm.newPassword" 
                placeholder="请输入新密码"
                required
                minlength="6"
              />
              <span class="form-hint">密码至少6位</span>
            </div>

            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeResetModal">取消</button>
              <button type="submit" class="submit-btn" :disabled="resetting">
                {{ resetting ? "重置中..." : "确认重置" }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import { useUserStore } from "@/stores/user.store";

export default {
  name: "SalesPerson",
  setup() {
    const userStore = useUserStore();

    // 列表状态
    const salesPersonList = ref([]);
    const total = ref(0);
    const pageNum = ref(1);
    const pageSize = ref(10);
    const loading = ref(false);
    const error = ref("");
    const deletingId = ref(null);
    const resettingId = ref(null);

    // 添加弹窗状态
    const showAddModal = ref(false);
    const adding = ref(false);
    const addForm = reactive({
      email: "",
      nickName: "",
      password: ""
    });

    // 重置密码弹窗状态
    const showResetModal = ref(false);
    const resetting = ref(false);
    const resetTargetId = ref(null);
    const resetTargetName = ref("");
    const resetForm = reactive({
      newPassword: ""
    });

    // 加载销售人员列表
    const loadSalesPersonList = async () => {
      try {
        loading.value = true;
        error.value = "";

        const response = await userStore.getSalesPersonList(
          pageNum.value,
          pageSize.value
        );
        salesPersonList.value = response.data || [];
        total.value = response.total || 0;
      } catch (err) {
        error.value =
          "加载销售人员列表失败: " +
          (err.response?.data?.info || err.message || "未知错误");
        console.error("加载销售人员列表失败:", err);
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

    // 获取头像占位符
    const getAvatarPlaceholder = (nickName) => {
      if (!nickName) return "S";
      return nickName.charAt(0).toUpperCase();
    };

    // 添加销售人员
    const handleAddPerson = async () => {
      if (!addForm.email || !addForm.nickName || !addForm.password) {
        alert("请填写完整信息");
        return;
      }
      if (addForm.password.length < 6) {
        alert("密码至少6位");
        return;
      }

      try {
        adding.value = true;
        await userStore.addSalesPerson(
          addForm.email,
          addForm.nickName,
          addForm.password
        );
        alert("添加销售人员成功");
        closeAddModal();
        await loadSalesPersonList();
      } catch (err) {
        alert(`添加失败: ${err.response?.data?.info || err.message || "未知错误"}`);
        console.error("添加销售人员失败:", err);
      } finally {
        adding.value = false;
      }
    };

    // 关闭添加弹窗
    const closeAddModal = () => {
      showAddModal.value = false;
      addForm.email = "";
      addForm.nickName = "";
      addForm.password = "";
    };

    // 删除销售人员
    const handleDeletePerson = async (userId, nickName) => {
      if (
        !confirm(`确定要删除销售人员 "${nickName || userId}" 吗？此操作不可撤销。`)
      ) {
        return;
      }

      try {
        deletingId.value = userId;
        await userStore.deleteSalesPerson(userId);
        alert("删除成功");
        await loadSalesPersonList();
      } catch (err) {
        alert(`删除失败: ${err.response?.data?.info || err.message || "未知错误"}`);
        console.error("删除销售人员失败:", err);
      } finally {
        deletingId.value = null;
      }
    };

    // 打开重置密码弹窗
    const handleResetPassword = (userId, nickName) => {
      resetTargetId.value = userId;
      resetTargetName.value = nickName || `用户${userId}`;
      showResetModal.value = true;
    };

    // 确认重置密码
    const confirmResetPassword = async () => {
      if (!resetForm.newPassword) {
        alert("请输入新密码");
        return;
      }
      if (resetForm.newPassword.length < 6) {
        alert("密码至少6位");
        return;
      }

      try {
        resetting.value = true;
        resettingId.value = resetTargetId.value;
        await userStore.resetPassword(resetTargetId.value, resetForm.newPassword);
        alert("密码重置成功");
        closeResetModal();
      } catch (err) {
        alert(`重置失败: ${err.response?.data?.info || err.message || "未知错误"}`);
        console.error("重置密码失败:", err);
      } finally {
        resetting.value = false;
        resettingId.value = null;
      }
    };

    // 关闭重置密码弹窗
    const closeResetModal = () => {
      showResetModal.value = false;
      resetForm.newPassword = "";
      resetTargetId.value = null;
      resetTargetName.value = "";
    };

    // 分页切换
    const changePage = (newPage) => {
      pageNum.value = newPage;
      loadSalesPersonList();
    };

    // 生命周期钩子
    onMounted(() => {
      loadSalesPersonList();
    });

    return {
      // 列表状态
      salesPersonList,
      total,
      pageNum,
      pageSize,
      loading,
      error,
      deletingId,
      resettingId,
      // 添加弹窗状态
      showAddModal,
      adding,
      addForm,
      // 重置密码弹窗状态
      showResetModal,
      resetting,
      resetTargetName,
      resetForm,
      // 方法
      loadSalesPersonList,
      formatNull,
      formatMoney,
      getAvatarPlaceholder,
      handleAddPerson,
      closeAddModal,
      handleDeletePerson,
      handleResetPassword,
      confirmResetPassword,
      closeResetModal,
      changePage
    };
  },
};
</script>

<style scoped>
.sales-person-page {
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

/* 操作栏 */
.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.add-btn {
  padding: 10px 20px;
  background: #52c41a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: all 0.2s;
}

.add-btn:hover {
  background: #73d13d;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(82, 196, 26, 0.3);
}

.add-btn .icon {
  font-size: 18px;
  font-weight: bold;
}

/* 列表容器 */
.sales-person-list-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.loading,
.error,
.empty-list {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.empty-list p {
  margin-bottom: 20px;
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

/* 销售人员列表 */
.sales-person-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.sales-person-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.sales-person-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #52c41a;
}

.person-header {
  display: flex;
  align-items: center;
  gap: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.person-avatar {
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

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
  color: white;
}

.person-info {
  flex: 1;
  min-width: 0;
}

.person-nickname {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.person-id {
  font-size: 12px;
  color: #999;
  font-family: "Monaco", "Consolas", monospace;
}

.role-tag {
  padding: 4px 10px;
  background: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.person-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.person-detail {
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

.person-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.reset-pwd-btn {
  padding: 6px 16px;
  background: #faad14;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  min-width: 80px;
}

.reset-pwd-btn:hover:not(:disabled) {
  background: #ffc53d;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(250, 173, 20, 0.3);
}

.reset-pwd-btn:disabled {
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

/* 分页 */
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

/* 弹窗样式 */
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
  max-width: 450px;
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

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
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

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  transition: all 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.required {
  color: #ff4d4f;
}

.form-hint {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #999;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 25px;
}

.cancel-btn {
  padding: 8px 20px;
  border: 1px solid #d9d9d9;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.cancel-btn:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.submit-btn {
  padding: 8px 20px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.submit-btn:hover:not(:disabled) {
  background: #40a9ff;
}

.submit-btn:disabled {
  background: #d9d9d9;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .sales-person-page {
    padding: 10px;
  }

  .sales-person-list {
    grid-template-columns: 1fr;
  }

  .person-actions {
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
