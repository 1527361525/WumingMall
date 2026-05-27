<template>
  <div class="user-portrait-page">
    <h2 class="page-title">用户画像分析</h2>
    
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="overview-cards" v-loading="overviewLoading">
      <el-col :span="8">
        <el-card class="overview-card" @click="fetchOverviewData" style="cursor: pointer;">
          <div class="card-icon" style="background-color: #409eff;">
            <el-icon><User /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">总用户数</div>
            <div class="card-value">{{ overviewData.totalUsers }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="overview-card" @click="fetchOverviewData" style="cursor: pointer;">
          <div class="card-icon" style="background-color: #67c23a;">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">有购买记录用户</div>
            <div class="card-value">{{ overviewData.purchaseUsers }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="overview-card" @click="fetchOverviewData" style="cursor: pointer;">
          <div class="card-icon" style="background-color: #e6a23c;">
            <el-icon><Money /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-label">人均消费</div>
            <div class="card-value">¥{{ overviewData.avgConsumption }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 地域分布 -->
      <el-col :span="12">
        <el-card class="chart-card" v-loading="regionLoading">
          <template #header>
            <div class="card-header">
              <span>用户地域分布</span>
              <el-button type="primary" size="small" @click="refreshRegionData">
                <el-icon><Refresh /></el-icon>刷新
              </el-button>
            </div>
          </template>
          <div ref="regionChartRef" class="chart-container"></div>
          <el-empty v-if="!regionLoading && regionData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
      
      <!-- 购买力分层 -->
      <el-col :span="12">
        <el-card class="chart-card" v-loading="powerLoading">
          <template #header>
            <div class="card-header">
              <span>用户购买力分层</span>
              <el-button type="primary" size="small" @click="refreshPowerData">
                <el-icon><Refresh /></el-icon>刷新
              </el-button>
            </div>
          </template>
          <div ref="powerChartRef" class="chart-container"></div>
          <el-empty v-if="!powerLoading && powerData.length === 0" description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户偏好查询 -->
    <el-card class="preference-card">
      <template #header>
        <div class="card-header">
          <span>用户购买偏好查询</span>
        </div>
      </template>
      
      <div class="preference-query">
        <el-form :inline="true" :model="queryForm">
          <el-form-item label="用户ID">
            <el-input v-model="queryForm.userId" placeholder="请输入用户ID" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="queryUserPreference" :loading="preferenceLoading">
              <el-icon><Search /></el-icon>查询
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-if="preferenceData.length > 0" class="preference-result" v-loading="preferenceLoading">
        <h4>该用户购买偏好</h4>
        <div ref="preferenceChartRef" class="chart-container" style="height: 300px;"></div>
      </div>
      
      <el-empty v-else-if="hasQueried && !preferenceLoading" description="该用户暂无购买记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { User, ShoppingCart, Money, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { useAnalysisStore } from '@/stores/analysis.store'

const analysisStore = useAnalysisStore()

// 数据概览
const overviewData = reactive({
  totalUsers: '-',
  purchaseUsers: '-',
  avgConsumption: '-'
})

// 概览数据加载状态
const overviewLoading = ref(false)

// 查询表单
const queryForm = reactive({
  userId: ''
})

const hasQueried = ref(false)

// 加载状态
const regionLoading = ref(false)
const powerLoading = ref(false)
const preferenceLoading = ref(false)

// 后端数据
const regionData = computed(() => analysisStore.regionDistribution)
const powerData = computed(() => analysisStore.purchasingPower)
const preferenceData = computed(() => analysisStore.userPreference)

// 图表引用
const regionChartRef = ref(null)
const powerChartRef = ref(null)
const preferenceChartRef = ref(null)

let regionChart = null
let powerChart = null
let preferenceChart = null

// 初始化地域分布图表
const initRegionChart = (data) => {
  if (!regionChart) {
    regionChart = echarts.init(regionChartRef.value)
  }
  
  const provinces = data.map(item => item.province)
  const counts = data.map(item => item.count)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: provinces,
      axisLabel: { interval: 0, rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: '用户数',
      minInterval: 1
    },
    series: [{
      data: counts,
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      }
    }]
  }
  regionChart.setOption(option, true)
}

// 初始化购买力分层图表
const initPowerChart = (data) => {
  if (!powerChart) {
    powerChart = echarts.init(powerChartRef.value)
  }
  
  // 定义层级顺序和颜色
  const levelOrder = ['低消费', '中消费', '高消费']
  const levelColors = {
    '低消费': '#67c23a',
    '中消费': '#e6a23c',
    '高消费': '#f56c6c'
  }
  
  // 按固定顺序整理数据
  const chartData = levelOrder.map(level => {
    const item = data.find(d => d.level === level)
    return {
      name: `${level}(${getLevelRange(level)})`,
      value: item ? item.user_count : 0,
      itemStyle: { color: levelColors[level] }
    }
  }).filter(item => item.value > 0)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}人 ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '购买力分层',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        formatter: '{b}\n{c}人'
      },
      data: chartData
    }]
  }
  powerChart.setOption(option, true)
}

// 获取层级范围描述
const getLevelRange = (level) => {
  const ranges = {
    '低消费': '<100',
    '中消费': '100-500',
    '高消费': '>500'
  }
  return ranges[level] || ''
}

// 初始化偏好图表
const initPreferenceChart = (data) => {
  if (!preferenceChart) {
    preferenceChart = echarts.init(preferenceChartRef.value)
  }
  
  const categories = data.map(item => item.category_name)
  const counts = data.map(item => item.buy_count)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categories
    },
    yAxis: {
      type: 'value',
      name: '购买次数',
      minInterval: 1,
      interval: 1
    },
    series: [{
      data: counts,
      type: 'bar',
      itemStyle: { color: '#409eff' }
    }]
  }
  preferenceChart.setOption(option, true)
}

// 获取地域分布数据
const refreshRegionData = async () => {
  regionLoading.value = true
  try {
    await analysisStore.fetchRegionDistribution()
    if (regionData.value.length > 0) {
      nextTick(() => {
        initRegionChart(regionData.value)
      })
    }
    ElMessage.success('地域数据已刷新')
  } catch (error) {
    ElMessage.error('获取地域分布失败')
  } finally {
    regionLoading.value = false
  }
}

// 获取购买力分层数据
const refreshPowerData = async () => {
  powerLoading.value = true
  try {
    await analysisStore.fetchPurchasingPower()
    if (powerData.value.length > 0) {
      nextTick(() => {
        initPowerChart(powerData.value)
      })
    }
    ElMessage.success('购买力数据已刷新')
  } catch (error) {
    ElMessage.error('获取购买力分层失败')
  } finally {
    powerLoading.value = false
  }
}

// 查询用户偏好
const queryUserPreference = async () => {
  if (!queryForm.userId) {
    ElMessage.warning('请输入用户ID')
    return
  }
  
  hasQueried.value = true
  preferenceLoading.value = true
  
  try {
    await analysisStore.fetchUserPreference(queryForm.userId)
    if (preferenceData.value.length > 0) {
      nextTick(() => {
        initPreferenceChart(preferenceData.value)
      })
    }
  } catch (error) {
    ElMessage.error('获取用户偏好失败')
  } finally {
    preferenceLoading.value = false
  }
}

// 获取概览数据
const fetchOverviewData = async () => {
  overviewLoading.value = true
  try {
    await analysisStore.fetchUserOverview()
    const data = analysisStore.userOverview
    overviewData.totalUsers = data.totalUsers || 0
    overviewData.purchaseUsers = data.purchaseUsers || 0
    overviewData.avgConsumption = (data.avgConsumption || 0).toFixed(2)
  } catch (error) {
    ElMessage.error('获取用户概览数据失败')
  } finally {
    overviewLoading.value = false
  }
}

onMounted(() => {
  // 初始加载数据
  fetchOverviewData()
  refreshRegionData()
  refreshPowerData()
  
  window.addEventListener('resize', () => {
    regionChart?.resize()
    powerChart?.resize()
    preferenceChart?.resize()
  })
})
</script>

<style lang="scss" scoped>
.user-portrait-page {
  .page-title {
    margin: 0 0 20px;
    font-size: 24px;
    color: #303133;
  }
  
  .overview-cards {
    margin-bottom: 20px;
    
    .overview-card {
      display: flex;
      align-items: center;
      padding: 10px;
      
      .card-icon {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 15px;
        
        .el-icon {
          font-size: 30px;
          color: #fff;
        }
      }
      
      .card-content {
        .card-label {
          font-size: 14px;
          color: #909399;
          margin-bottom: 5px;
        }
        
        .card-value {
          font-size: 24px;
          font-weight: bold;
          color: #303133;
        }
      }
    }
  }
  
  .chart-row {
    margin-bottom: 20px;
  }
  
  .chart-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .chart-container {
      height: 350px;
    }
  }
  
  .preference-card {
    .preference-query {
      margin-bottom: 20px;
      padding: 20px;
      background-color: #f5f7fa;
      border-radius: 4px;
    }
    
    .preference-result {
      h4 {
        margin: 0 0 15px;
        color: #303133;
      }
    }
  }
}
</style>
