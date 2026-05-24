<template>
  <div class="sales-trend-page">
    <h2 class="page-title">销售趋势分析</h2>
    
    <!-- 时间筛选 -->
    <el-card class="filter-card">
      <div class="time-filter">
        <span class="filter-label">时间维度：</span>
        <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
          <el-radio-button label="day">日</el-radio-button>
          <el-radio-button label="week">周</el-radio-button>
          <el-radio-button label="month">月</el-radio-button>
        </el-radio-group>
        <el-button type="primary" style="margin-left: 20px;" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>刷新数据
        </el-button>
      </div>
    </el-card>

    <!-- 销售指标卡片 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">销售额</div>
          <div class="metric-value">¥{{ formatNumber(salesMetrics.totalAmount) }}</div>
          <div class="metric-trend" :class="salesMetrics.amountTrend >= 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.amountTrend >= 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.amountTrend) }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">订单数</div>
          <div class="metric-value">{{ formatNumber(salesMetrics.totalOrders) }}</div>
          <div class="metric-trend" :class="salesMetrics.orderTrend >= 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.orderTrend >= 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.orderTrend) }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">客单价</div>
          <div class="metric-value">¥{{ formatNumber(salesMetrics.avgOrderValue) }}</div>
          <div class="metric-trend" :class="salesMetrics.avgTrend >= 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.avgTrend >= 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.avgTrend) }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">商品销量</div>
          <div class="metric-value">{{ formatNumber(salesMetrics.totalQuantity) }}</div>
          <div class="metric-trend" :class="salesMetrics.quantityTrend >= 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.quantityTrend >= 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.quantityTrend) }}%
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 销售趋势图 -->
    <el-card class="trend-card">
      <template #header>
        <div class="card-header">
          <span>销售趋势</span>
          <el-radio-group v-model="trendType" size="small" @change="handleTrendTypeChange">
            <el-radio-button label="amount">销售额</el-radio-button>
            <el-radio-button label="orders">订单数</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="trendChartRef" class="chart-container"></div>
    </el-card>

    <!-- 商品销量排行和类别销售占比 -->
    <el-row :gutter="20" class="bottom-row">
      <el-col :span="12">
        <el-card class="rank-card">
          <template #header>
            <div class="card-header">
              <span>商品销量TOP10</span>
            </div>
          </template>
          <div v-if="productTopN.length > 0" ref="productRankChartRef" class="chart-container"></div>
          <el-empty v-else description="暂无商品销量数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="category-card">
          <template #header>
            <div class="card-header">
              <span>类别销售占比</span>
            </div>
          </template>
          <div v-if="categorySales.length > 0" ref="categoryChartRef" class="chart-container"></div>
          <el-empty v-else description="暂无类别销售数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed, watch } from 'vue'
import { Refresh, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { useAnalysisStore } from '@/stores/analysis.store'
import axios from 'axios'

const analysisStore = useAnalysisStore()
const loading = computed(() => analysisStore.loading)

// 时间维度
const timeRange = ref('day')

// 趋势类型
const trendType = ref('amount')

// 销售指标
const salesMetrics = reactive({
  totalAmount: 0,
  totalOrders: 0,
  avgOrderValue: 0,
  totalQuantity: 0,
  amountTrend: 0,
  orderTrend: 0,
  avgTrend: 0,
  quantityTrend: 0
})

// 销售趋势数据
const salesTrendData = ref([])

// 商品销量排行
const productTopN = ref([])

// 类别销售统计
const categorySales = computed(() => analysisStore.categorySales)

// 图表引用
const trendChartRef = ref(null)
const productRankChartRef = ref(null)
const categoryChartRef = ref(null)

let trendChart = null
let productRankChart = null
let categoryChart = null

// 格式化数字
const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取时间维度显示文本
const getTimeRangeText = () => {
  const map = { day: '日', week: '周', month: '月' }
  return map[timeRange.value] || '日'
}

// 计算销售指标
const calculateMetrics = (data) => {
  if (!data || data.length === 0) {
    salesMetrics.totalAmount = 0
    salesMetrics.totalOrders = 0
    salesMetrics.totalQuantity = 0
    salesMetrics.avgOrderValue = 0
    return
  }

  // 计算总计
  let totalAmount = 0
  let totalOrders = 0
  let totalQuantity = 0

  data.forEach(item => {
    totalAmount += parseFloat(item.sales_amount || 0)
    totalOrders += parseInt(item.order_count || 0)
    totalQuantity += parseInt(item.sales_quantity || 0)
  })

  salesMetrics.totalAmount = totalAmount
  salesMetrics.totalOrders = totalOrders
  salesMetrics.totalQuantity = totalQuantity
  salesMetrics.avgOrderValue = totalOrders > 0 ? (totalAmount / totalOrders).toFixed(2) : 0

  // 计算环比趋势（最后一个周期 vs 倒数第二个周期）
  if (data.length >= 2) {
    const current = data[data.length - 1]
    const previous = data[data.length - 2]

    const currentAmount = parseFloat(current.sales_amount || 0)
    const previousAmount = parseFloat(previous.sales_amount || 0)
    salesMetrics.amountTrend = previousAmount > 0 
      ? ((currentAmount - previousAmount) / previousAmount * 100).toFixed(1) 
      : 0

    const currentOrders = parseInt(current.order_count || 0)
    const previousOrders = parseInt(previous.order_count || 0)
    salesMetrics.orderTrend = previousOrders > 0 
      ? ((currentOrders - previousOrders) / previousOrders * 100).toFixed(1) 
      : 0

    const currentQuantity = parseInt(current.sales_quantity || 0)
    const previousQuantity = parseInt(previous.sales_quantity || 0)
    salesMetrics.quantityTrend = previousQuantity > 0 
      ? ((currentQuantity - previousQuantity) / previousQuantity * 100).toFixed(1) 
      : 0

    const currentAvg = currentOrders > 0 ? currentAmount / currentOrders : 0
    const previousAvg = previousOrders > 0 ? previousAmount / previousOrders : 0
    salesMetrics.avgTrend = previousAvg > 0 
      ? ((currentAvg - previousAvg) / previousAvg * 100).toFixed(1) 
      : 0
  }
}

// 获取销售趋势数据
const fetchSalesTrendData = async () => {
  try {
    const response = await analysisStore.fetchSalesTrend(timeRange.value)
    if (response.code === 200) {
      salesTrendData.value = response.data || []
      calculateMetrics(salesTrendData.value)
      updateTrendChart()
    }
  } catch (error) {
    ElMessage.error('获取销售趋势数据失败')
  }
}

// 获取商品销量排行
const fetchProductTopN = async () => {
  try {
    // 根据当前时间维度转换type参数：day->1, week->2, month->3
    const typeMap = { day: 1, week: 2, month: 3 }
    const type = typeMap[timeRange.value] || 1
    
    const response = await axios.get('/statistic/getProductTopN', {
      params: { type, n: 10 },
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (response.data.code === 200) {
      productTopN.value = response.data.data || []
      updateProductRankChart()
    }
  } catch (error) {
    ElMessage.error('获取商品销量排行失败')
  }
}

// 获取类别销售统计
const fetchCategorySalesData = async () => {
  try {
    await analysisStore.fetchCategorySales()
    updateCategoryChart()
  } catch (error) {
    ElMessage.error('获取类别销售统计失败')
  }
}

// 初始化销售趋势图
const initTrendChart = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    updateTrendChart()
  }
}

// 更新趋势图
const updateTrendChart = () => {
  if (!trendChart || !salesTrendData.value.length) return

  // 处理日期显示
  const dates = salesTrendData.value.map(item => {
    const date = item.date || item.week || item.month
    if (timeRange.value === 'day') {
      // 日趋势：显示 MM-DD
      return date.substring(5)
    } else if (timeRange.value === 'week') {
      // 周趋势：显示第X周
      return date + '周'
    } else {
      // 月趋势：显示 YYYY-MM
      return date
    }
  })

  const data = trendType.value === 'amount' 
    ? salesTrendData.value.map(item => parseFloat(item.sales_amount || 0))
    : salesTrendData.value.map(item => parseInt(item.order_count || 0))
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: trendType.value === 'amount' 
        ? '{b}<br/>销售额: ¥{c}' 
        : '{b}<br/>订单数: {c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates
    },
    yAxis: {
      type: 'value',
      name: trendType.value === 'amount' ? '销售额(元)' : '订单数'
    },
    series: [{
      data: data,
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      itemStyle: { color: '#409eff' }
    }]
  }
  trendChart.setOption(option, true)
}

// 初始化商品排行图
const initProductRankChart = () => {
  if (productRankChartRef.value) {
    productRankChart = echarts.init(productRankChartRef.value)
    updateProductRankChart()
  }
}

// 更新商品排行图
const updateProductRankChart = () => {
  if (!productRankChart || productTopN.value.length === 0) return

  // 取前10个，并反转顺序（从下到上显示）
  const top10 = [...productTopN.value].slice(0, 10).reverse()
  const names = top10.map(item => item.name)
  const values = top10.map(item => item.totalSales || 0)

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
      type: 'value',
      name: '销量'
    },
    yAxis: {
      type: 'category',
      data: names,
      axisLabel: { interval: 0 }
    },
    series: [{
      data: values,
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ])
      }
    }]
  }
  productRankChart.setOption(option, true)
}

// 初始化类别占比图
const initCategoryChart = () => {
  if (categoryChartRef.value) {
    categoryChart = echarts.init(categoryChartRef.value)
    updateCategoryChart()
  }
}

// 更新类别占比图
const updateCategoryChart = () => {
  if (!categoryChart || categorySales.value.length === 0) return

  // 过滤掉没有销售数据的类别，并按销售额排序
  const validCategories = categorySales.value
    .filter(item => parseFloat(item.sales_amount || 0) > 0)
    .sort((a, b) => parseFloat(b.sales_amount || 0) - parseFloat(a.sales_amount || 0))

  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#7238dd', '#00ced1', '#ff69b4']

  const data = validCategories.map((item, index) => ({
    value: parseFloat(item.sales_amount || 0),
    name: item.category_name,
    itemStyle: { color: colors[index % colors.length] }
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      textStyle: {
        fontSize: 12
      }
    },
    series: [{
      name: '类别销售占比',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      data: data
    }]
  }
  categoryChart.setOption(option, true)
}

// 时间维度变化
const handleTimeRangeChange = (val) => {
  ElMessage.success(`已切换到${val === 'day' ? '日' : val === 'week' ? '周' : '月'}度视图`)
  refreshData()
}

// 趋势类型变化
const handleTrendTypeChange = () => {
  updateTrendChart()
}

// 刷新数据
const refreshData = async () => {
  await Promise.all([
    fetchSalesTrendData(),
    fetchProductTopN(),
    fetchCategorySalesData()
  ])
  ElMessage.success('数据已刷新')
}

onMounted(() => {
  nextTick(() => {
    initTrendChart()
    initProductRankChart()
    initCategoryChart()
    refreshData()
  })
  
  window.addEventListener('resize', () => {
    trendChart?.resize()
    productRankChart?.resize()
    categoryChart?.resize()
  })
})

// 监听数据变化，更新图表
watch(() => analysisStore.categorySales, () => {
  updateCategoryChart()
}, { deep: true })
</script>

<style lang="scss" scoped>
.sales-trend-page {
  .page-title {
    margin: 0 0 20px;
    font-size: 24px;
    color: #303133;
  }
  
  .filter-card {
    margin-bottom: 20px;
    
    .time-filter {
      display: flex;
      align-items: center;
      
      .filter-label {
        margin-right: 10px;
        color: #606266;
      }
    }
  }
  
  .metrics-row {
    margin-bottom: 20px;
    
    .metric-card {
      text-align: center;
      
      .metric-label {
        font-size: 14px;
        color: #909399;
        margin-bottom: 10px;
      }
      
      .metric-value {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 10px;
      }
      
      .metric-trend {
        font-size: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        
        &.up {
          color: #67c23a;
        }
        
        &.down {
          color: #f56c6c;
        }
      }
    }
  }
  
  .trend-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .chart-container {
      height: 400px;
    }
  }
  
  .bottom-row {
    .rank-card,
    .category-card {
      .card-header {
        font-weight: bold;
      }
      
      .chart-container {
        height: 350px;
      }
    }
  }
}
</style>
