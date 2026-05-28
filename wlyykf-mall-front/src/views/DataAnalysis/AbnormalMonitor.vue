<template>
  <div class="abnormal-monitor-page">
    <h2 class="page-title">销售异常监控</h2>
    
    <!-- 异常概览 -->
    <el-row :gutter="20" class="overview-row">
      <el-col :span="6">
        <el-card class="overview-card warning">
          <div class="card-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ abnormalOverview.warningCount }}</div>
            <div class="card-label">预警项</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card danger">
          <div class="card-icon">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ abnormalOverview.errorCount }}</div>
            <div class="card-label">异常项</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card normal">
          <div class="card-icon">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ abnormalOverview.normalCount }}</div>
            <div class="card-label">正常项</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="overview-card info">
          <div class="card-icon">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-value">{{ abnormalOverview.monitorRate }}</div>
            <div class="card-label">监控频率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 实时监控面板 -->
    <el-card class="realtime-card">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <span class="title">实时监控</span>
            <el-tag type="success" effect="dark" class="status-tag">
              <el-icon><VideoPlay /></el-icon>监控中
            </el-tag>
          </div>
          <div class="header-right">
            <el-button type="primary" size="small" @click="refreshRealtimeData">
              <el-icon><Refresh /></el-icon>刷新
            </el-button>
            <el-button type="warning" size="small" @click="showThresholdConfig">
              <el-icon><Setting /></el-icon>阈值配置
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="realtime-metrics">
        <el-row :gutter="30">
          <el-col :span="8">
            <div class="metric-box">
              <div class="metric-title">今日销售额</div>
              <div class="metric-value" :class="getStatusClass(realtimeData.amountStatus)">
                ¥{{ realtimeData.todayAmount }}
              </div>
              <div class="metric-compare">
                较昨日: 
                <span :class="realtimeData.amountCompare > 0 ? 'up' : 'down'">
                  {{ realtimeData.amountCompare > 0 ? '+' : '' }}{{ realtimeData.amountCompare }}%
                </span>
              </div>
              <el-progress 
                :percentage="realtimeData.amountProgress" 
                :status="realtimeData.amountStatus"
                :stroke-width="8"
              />
            </div>
          </el-col>
          <el-col :span="8">
            <div class="metric-box">
              <div class="metric-title">今日订单数</div>
              <div class="metric-value" :class="getStatusClass(realtimeData.orderStatus)">
                {{ realtimeData.todayOrders }}
              </div>
              <div class="metric-compare">
                较昨日: 
                <span :class="realtimeData.orderCompare > 0 ? 'up' : 'down'">
                  {{ realtimeData.orderCompare > 0 ? '+' : '' }}{{ realtimeData.orderCompare }}%
                </span>
              </div>
              <el-progress 
                :percentage="realtimeData.orderProgress" 
                :status="realtimeData.orderStatus"
                :stroke-width="8"
              />
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 异常记录列表 -->
    <el-card class="abnormal-list-card">
      <template #header>
        <div class="card-header">
          <span>异常记录</span>
          <div class="filter-group">
            <el-select v-model="filterStatus" placeholder="异常类型" style="width: 120px; margin-right: 10px;">
              <el-option label="全部" value="" />
              <el-option label="预警" value="warning" />
              <el-option label="异常" value="error" />
            </el-select>
            <el-date-picker
              v-model="filterDate"
              type="date"
              placeholder="选择日期"
              style="width: 150px;"
            />
          </div>
        </div>
      </template>
      
      <el-table :data="abnormalList" style="width: 100%" v-loading="loading">
        <el-table-column prop="time" label="时间" width="180" />
        <el-table-column prop="type" label="监控项" width="120" />
        <el-table-column prop="currentValue" label="当前值" width="150">
          <template #default="{ row }">
            <span :class="getRowClass(row)">{{ row.currentValue }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="expectedValue" label="预期值" width="150" />
        <el-table-column prop="deviation" label="偏差" width="120">
          <template #default="{ row }">
            <span :class="row.deviation > 0 ? 'up' : 'down'">
              {{ row.deviation > 0 ? '+' : '' }}{{ row.deviation }}%
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="level" label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="row.level === 'error' ? 'danger' : 'warning'" effect="dark">
              {{ row.level === 'error' ? '异常' : '预警' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 阈值配置对话框 -->
    <el-dialog v-model="thresholdDialogVisible" title="异常阈值配置" width="500px">
      <el-form :model="thresholdForm" label-width="150px">
        <el-form-item label="销售额波动阈值">
          <el-input-number v-model="thresholdForm.amountThreshold" :min="1" :max="100" />
          <span class="unit">%</span>
        </el-form-item>
        <el-form-item label="订单数波动阈值">
          <el-input-number v-model="thresholdForm.orderThreshold" :min="1" :max="100" />
          <span class="unit">%</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="thresholdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveThreshold">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
import {
  Warning, CircleClose, SuccessFilled, Timer,
  VideoPlay, Refresh, Setting
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 获取 axios 实例
const { proxy } = getCurrentInstance()
const axios = proxy.$axios

// 异常概览
const abnormalOverview = reactive({
  warningCount: 0,
  errorCount: 0,
  normalCount: 0,
  monitorRate: '实时'
})

// 实时数据
const realtimeData = reactive({
  todayAmount: '0.00',
  amountCompare: 0,
  amountProgress: 0,
  amountStatus: 'success',
  todayOrders: 0,
  orderCompare: 0,
  orderProgress: 0,
  orderStatus: 'success'
})

// 筛选条件
const filterStatus = ref('')
const filterDate = ref('')
const loading = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 异常列表
const abnormalList = ref([])

// 阈值配置对话框
const thresholdDialogVisible = ref(false)
const thresholdForm = reactive({
  amountThreshold: 30,
  orderThreshold: 30
})

// 获取状态样式
const getStatusClass = (status) => {
  const map = {
    'success': 'status-normal',
    'warning': 'status-warning',
    'exception': 'status-danger'
  }
  return map[status] || ''
}

// 获取行样式
const getRowClass = (row) => {
  return row.level === 'error' ? 'text-danger' : 'text-warning'
}

// 格式化金额
const formatAmount = (amount) => {
  return amount.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取异常检测数据
const fetchAbnormalDetection = async () => {
  try {
    const response = await axios.get('/analysis/abnormal/sales', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const { code, data } = response.data
    if (code === 200 && data) {
      // 更新概览统计
      let warningCount = 0
      let errorCount = 0

      // 检查环比异常
      if (data.dayOverDayChange) {
        if (data.dayOverDayChange.isOrderCountAbnormal || data.dayOverDayChange.isSalesAmountAbnormal) {
          errorCount++
        }
      }
      // 检查同比异常
      if (data.weekOverWeekChange) {
        if (data.weekOverWeekChange.isOrderCountAbnormal || data.weekOverWeekChange.isSalesAmountAbnormal) {
          errorCount++
        }
      }

      // 设置概览数据
      abnormalOverview.warningCount = warningCount
      abnormalOverview.errorCount = errorCount
      abnormalOverview.normalCount = errorCount === 0 ? 2 : 0
      abnormalOverview.monitorRate = '实时'

      // 构建异常列表
      const list = []
      const now = new Date().toLocaleString('zh-CN')

      // 环比异常
      if (data.dayOverDayChange) {
        const dayChange = data.dayOverDayChange
        const today = data.today
        const yesterday = data.yesterday

        // 销售额环比异常
        if (dayChange.isSalesAmountAbnormal) {
          const deviation = dayChange.salesAmountChangeRate
          list.push({
            time: now,
            type: '销售额(环比)',
            currentValue: '¥' + formatAmount(today.salesAmount),
            expectedValue: '¥' + formatAmount(yesterday.salesAmount),
            deviation: deviation,
            level: Math.abs(deviation) >= 50 ? 'error' : 'warning',
            description: `今日销售额较昨日同期${deviation > 0 ? '增长' : '下降'}${Math.abs(deviation).toFixed(2)}%，触发异常告警`
          })
        }

        // 订单数环比异常
        if (dayChange.isOrderCountAbnormal) {
          const deviation = dayChange.orderCountChangeRate
          list.push({
            time: now,
            type: '订单数(环比)',
            currentValue: today.orderCount.toString(),
            expectedValue: yesterday.orderCount.toString(),
            deviation: deviation,
            level: Math.abs(deviation) >= 50 ? 'error' : 'warning',
            description: `今日订单数较昨日同期${deviation > 0 ? '增长' : '下降'}${Math.abs(deviation).toFixed(2)}%，触发异常告警`
          })
        }
      }

      // 同比异常
      if (data.weekOverWeekChange) {
        const weekChange = data.weekOverWeekChange
        const today = data.today
        const lastWeek = data.lastWeekSameDay

        // 销售额同比异常
        if (weekChange.isSalesAmountAbnormal) {
          const deviation = weekChange.salesAmountChangeRate
          list.push({
            time: now,
            type: '销售额(同比)',
            currentValue: '¥' + formatAmount(today.salesAmount),
            expectedValue: '¥' + formatAmount(lastWeek.salesAmount),
            deviation: deviation,
            level: Math.abs(deviation) >= 50 ? 'error' : 'warning',
            description: `今日销售额较上周同日${deviation > 0 ? '增长' : '下降'}${Math.abs(deviation).toFixed(2)}%，触发异常告警`
          })
        }

        // 订单数同比异常
        if (weekChange.isOrderCountAbnormal) {
          const deviation = weekChange.orderCountChangeRate
          list.push({
            time: now,
            type: '订单数(同比)',
            currentValue: today.orderCount.toString(),
            expectedValue: lastWeek.orderCount.toString(),
            deviation: deviation,
            level: Math.abs(deviation) >= 50 ? 'error' : 'warning',
            description: `今日订单数较上周同日${deviation > 0 ? '增长' : '下降'}${Math.abs(deviation).toFixed(2)}%，触发异常告警`
          })
        }
      }

      abnormalList.value = list
      total.value = list.length

      // 更新阈值显示
      if (data.threshold) {
        thresholdForm.amountThreshold = data.threshold
        thresholdForm.orderThreshold = data.threshold
      }
    }
  } catch (error) {
    ElMessage.error('获取异常检测数据失败')
    console.error('获取异常检测数据失败:', error)
  }
}

// 获取实时数据
const fetchRealtimeData = async () => {
  try {
    const response = await axios.get('/analysis/abnormal/realtime', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const { code, data } = response.data
    if (code === 200 && data) {
      // 更新实时数据
      realtimeData.todayAmount = formatAmount(data.salesAmount)
      realtimeData.todayOrders = data.orderCount

      // 获取对比数据计算变化率
      const abnormalRes = await axios.get('/analysis/abnormal/sales', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      if (abnormalRes.data.code === 200 && abnormalRes.data.data) {
        const abnormalData = abnormalRes.data.data

        // 更新环比数据
        if (abnormalData.dayOverDayChange) {
          realtimeData.amountCompare = abnormalData.dayOverDayChange.salesAmountChangeRate || 0
          realtimeData.orderCompare = abnormalData.dayOverDayChange.orderCountChangeRate || 0
        }

        // 更新状态
        const threshold = abnormalData.threshold || 30
        realtimeData.amountStatus = Math.abs(realtimeData.amountCompare) >= threshold ? 'exception' : 'success'
        realtimeData.orderStatus = Math.abs(realtimeData.orderCompare) >= threshold ? 'warning' : 'success'

        // 更新进度条
        realtimeData.amountProgress = Math.min(100, Math.max(0, 100 - Math.abs(realtimeData.amountCompare)))
        realtimeData.orderProgress = Math.min(100, Math.max(0, 100 - Math.abs(realtimeData.orderCompare)))
      }

      ElMessage.success('实时数据已刷新')
    }
  } catch (error) {
    ElMessage.error('获取实时数据失败')
    console.error('获取实时数据失败:', error)
  }
}

// 刷新实时数据
const refreshRealtimeData = () => {
  fetchRealtimeData()
}

// 显示阈值配置
const showThresholdConfig = () => {
  thresholdDialogVisible.value = true
}

// 保存阈值
const saveThreshold = () => {
  ElMessage.success('阈值配置已保存(仅前端展示，后端阈值为30%)')
  thresholdDialogVisible.value = false
}

// 查看详情
const handleDetail = (row) => {
  ElMessage.info(`查看${row.type}详情`)
}

// 分页变化
const handleSizeChange = (val) => {
  pageSize.value = val
}

const handleCurrentChange = (val) => {
  currentPage.value = val
}

onMounted(() => {
  loading.value = true
  Promise.all([fetchAbnormalDetection(), fetchRealtimeData()]).finally(() => {
    loading.value = false
  })
})
</script>

<style lang="scss" scoped>
.abnormal-monitor-page {
  .page-title {
    margin: 0 0 20px;
    font-size: 24px;
    color: #303133;
  }
  
  .overview-row {
    margin-bottom: 20px;
    
    .overview-card {
      display: flex;
      align-items: center;
      padding: 10px;
      
      .card-icon {
        width: 50px;
        height: 50px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        
        .el-icon {
          font-size: 24px;
          color: #fff;
        }
      }
      
      .card-content {
        .card-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
          margin-bottom: 5px;
        }
        
        .card-label {
          font-size: 14px;
          color: #909399;
        }
      }
      
      &.warning .card-icon {
        background-color: #e6a23c;
      }
      
      &.danger .card-icon {
        background-color: #f56c6c;
      }
      
      &.normal .card-icon {
        background-color: #67c23a;
      }
      
      &.info .card-icon {
        background-color: #909399;
      }
    }
  }
  
  .realtime-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .title {
          font-weight: bold;
        }
        
        .status-tag {
          .el-icon {
            margin-right: 4px;
          }
        }
      }
      
      .header-right {
        display: flex;
        gap: 10px;
      }
    }
    
    .realtime-metrics {
      .metric-box {
        text-align: center;
        padding: 20px;
        border-radius: 8px;
        background-color: #f5f7fa;
        
        .metric-title {
          font-size: 14px;
          color: #909399;
          margin-bottom: 15px;
        }
        
        .metric-value {
          font-size: 32px;
          font-weight: bold;
          margin-bottom: 10px;
          
          &.status-normal {
            color: #67c23a;
          }
          
          &.status-warning {
            color: #e6a23c;
          }
          
          &.status-danger {
            color: #f56c6c;
          }
        }
        
        .metric-compare {
          font-size: 14px;
          color: #606266;
          margin-bottom: 15px;
          
          .up {
            color: #67c23a;
          }
          
          .down {
            color: #f56c6c;
          }
        }
      }
    }
  }
  
  .abnormal-list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .text-danger {
      color: #f56c6c;
      font-weight: bold;
    }
    
    .text-warning {
      color: #e6a23c;
      font-weight: bold;
    }
    
    .up {
      color: #67c23a;
    }
    
    .down {
      color: #f56c6c;
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

.unit {
  margin-left: 8px;
  color: #606266;
}
</style>
