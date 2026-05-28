<template>
  <div class="operation-log-page">
    <h1>操作日志</h1>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="filter-item">
          <label>用户ID:</label>
          <el-input
            v-model="filterForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 200px"
          />
        </div>
        <div class="filter-item">
          <label>操作类型:</label>
          <el-select
            v-model="filterForm.operationType"
            placeholder="请选择操作类型"
            clearable
            style="width: 180px"
          >
            <el-option label="LOGIN" value="LOGIN" />
            <el-option label="LOGOUT" value="LOGOUT" />
            <el-option label="PRODUCT_ADD" value="PRODUCT_ADD" />
            <el-option label="PRODUCT_UPDATE" value="PRODUCT_UPDATE" />
            <el-option label="PRODUCT_DELETE" value="PRODUCT_DELETE" />
            <el-option label="ORDER_UPDATE" value="ORDER_UPDATE" />
            <el-option label="ORDER_DELIVER" value="ORDER_DELIVER" />
            <el-option label="CATEGORY_ADD" value="CATEGORY_ADD" />
            <el-option label="CATEGORY_UPDATE" value="CATEGORY_UPDATE" />
            <el-option label="CATEGORY_DELETE" value="CATEGORY_DELETE" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>日期范围:</label>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 260px"
          />
        </div>
        <div class="filter-item">
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </div>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="log-list-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading">
        <el-icon class="loading-icon"><Loading /></el-icon> 加载中...
      </div>

      <!-- 错误信息 -->
      <div v-else-if="error" class="error">
        <el-icon><CircleClose /></el-icon>
        {{ error }}
        <el-button link type="primary" @click="loadLogList">重试</el-button>
      </div>

      <!-- 日志列表为空 -->
      <div v-else-if="logList.length === 0" class="empty-logs">
        <el-empty description="暂无操作日志" />
      </div>

      <!-- 日志列表 -->
      <div v-else>
        <div class="total-count">共 {{ total }} 条记录</div>

        <el-table
          :data="logList"
          stripe
          border
          style="width: 100%"
          class="log-table"
        >
          <el-table-column prop="logId" label="日志ID" width="150" />
          <el-table-column prop="userId" label="用户ID" width="220" />
          <el-table-column prop="userType" label="用户类型" width="130">
            <template #default="scope">
              <el-tag :type="getUserTypeTag(scope.row.userType)">
                {{ getUserTypeText(scope.row.userType) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationType" label="操作类型" width="160">
            <template #default="scope">
              <el-tag :type="getOperationTypeTag(scope.row.operationType)">
                {{ scope.row.operationType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="operationContent" label="操作内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="ipAddress" label="IP地址" width="200" />
          <el-table-column prop="operationTime" label="操作时间" width="200" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            :current-page="pageNum"
            :page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @update:current-page="handleCurrentChange"
            @update:page-size="handleSizeChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

export default {
  name: 'OperationLog',
  setup() {
    // 状态定义
    const logList = ref([])
    const total = ref(0)
    const pageNum = ref(1)
    const pageSize = ref(10)
    const loading = ref(false)
    const error = ref('')
    const dateRange = ref([])

    // 筛选表单
    const filterForm = reactive({
      userId: '',
      operationType: ''
    })

    // 加载日志列表
    const loadLogList = async () => {
      try {
        loading.value = true
        error.value = ''

        const params = {
          pageNum: pageNum.value,
          pageSize: pageSize.value,
          userId: filterForm.userId || undefined,
          operationType: filterForm.operationType || undefined,
          startDate: dateRange.value && dateRange.value[0] ? dateRange.value[0] : undefined,
          endDate: dateRange.value && dateRange.value[1] ? dateRange.value[1] : undefined
        }

        const response = await axios.post('/log/operation/list', params, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        })

        if (response.data.code === 200) {
          logList.value = response.data.data || []
          total.value = response.data.total || 0
        } else if (response.data.code === 403) {
          error.value = '您没有权限查看操作日志'
          ElMessage.error('您没有权限查看操作日志')
        } else {
          throw new Error(response.data.info || '获取操作日志失败')
        }
      } catch (err) {
        error.value = '加载操作日志失败: ' + (err.response?.data?.info || err.message || '未知错误')
        console.error('加载操作日志失败:', err)
      } finally {
        loading.value = false
      }
    }

    // 查询
    const handleSearch = () => {
      pageNum.value = 1
      loadLogList()
    }

    // 重置
    const handleReset = () => {
      filterForm.userId = ''
      filterForm.operationType = ''
      dateRange.value = []
      pageNum.value = 1
      loadLogList()
    }

    // 分页大小变化
    const handleSizeChange = (val) => {
      pageSize.value = val
      pageNum.value = 1
      loadLogList()
    }

    // 页码变化
    const handleCurrentChange = (val) => {
      pageNum.value = val
      loadLogList()
    }

    // 获取用户类型标签类型
    const getUserTypeTag = (userType) => {
      const tagMap = {
        0: '',      // 普通用户 - 默认
        1: 'warning', // 销售人员 - 黄色
        2: 'danger'   // 管理员 - 红色
      }
      return tagMap[userType] || ''
    }

    // 获取用户类型文本
    const getUserTypeText = (userType) => {
      const textMap = {
        0: '普通用户',
        1: '销售人员',
        2: '管理员'
      }
      return textMap[userType] || '未知'
    }

    // 获取操作类型标签类型
    const getOperationTypeTag = (operationType) => {
      const tagMap = {
        'LOGIN': 'success',
        'LOGOUT': 'info',
        'PRODUCT_ADD': 'success',
        'PRODUCT_UPDATE': 'warning',
        'PRODUCT_DELETE': 'danger',
        'ORDER_UPDATE': 'warning',
        'ORDER_DELIVER': 'success',
        'CATEGORY_ADD': 'success',
        'CATEGORY_UPDATE': 'warning',
        'CATEGORY_DELETE': 'danger'
      }
      return tagMap[operationType] || ''
    }

    // 生命周期钩子
    onMounted(() => {
      loadLogList()
    })

    return {
      // 状态
      logList,
      total,
      pageNum,
      pageSize,
      loading,
      error,
      filterForm,
      dateRange,
      // 方法
      loadLogList,
      handleSearch,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      getUserTypeTag,
      getUserTypeText,
      getOperationTypeTag
    }
  }
}
</script>

<style scoped>
.operation-log-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

h1 {
  margin-bottom: 20px;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

/* 筛选区域样式 */
.filter-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 20px;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-item label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

/* 日志列表容器样式 */
.log-list-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.loading,
.error,
.empty-logs {
  text-align: center;
  padding: 50px 0;
  color: #666;
}

.loading-icon {
  animation: rotating 2s linear infinite;
  margin-right: 8px;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.error {
  color: #ff4d4f;
}

.total-count {
  margin-bottom: 20px;
  font-weight: bold;
  color: #666;
  font-size: 14px;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.log-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .filter-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .filter-item {
    width: 100%;
  }

  .filter-item :deep(.el-input),
  .filter-item :deep(.el-select),
  .filter-item :deep(.el-date-picker) {
    width: 100% !important;
  }
}

@media (max-width: 768px) {
  .operation-log-page {
    padding: 10px;
  }

  .filter-section,
  .log-list-container {
    padding: 15px;
  }

  .pagination {
    justify-content: center;
  }
}
</style>
