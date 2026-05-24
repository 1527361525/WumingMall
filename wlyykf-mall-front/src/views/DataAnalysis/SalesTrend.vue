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
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-left: 20px;"
          @change="handleDateChange"
        />
        <el-button type="primary" style="margin-left: 20px;" @click="refreshData">
          <el-icon><Refresh /></el-icon>刷新数据
        </el-button>
      </div>
    </el-card>

    <!-- 销售指标卡片 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">销售额</div>
          <div class="metric-value">¥{{ salesMetrics.totalAmount }}</div>
          <div class="metric-trend" :class="salesMetrics.amountTrend > 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.amountTrend > 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.amountTrend) }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">订单数</div>
          <div class="metric-value">{{ salesMetrics.totalOrders }}</div>
          <div class="metric-trend" :class="salesMetrics.orderTrend > 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.orderTrend > 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.orderTrend) }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">客单价</div>
          <div class="metric-value">¥{{ salesMetrics.avgOrderValue }}</div>
          <div class="metric-trend" :class="salesMetrics.avgTrend > 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.avgTrend > 0" /><ArrowDown v-else /></el-icon>
            {{ Math.abs(salesMetrics.avgTrend) }}%
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="metric-card">
          <div class="metric-label">商品销量</div>
          <div class="metric-value">{{ salesMetrics.totalQuantity }}</div>
          <div class="metric-trend" :class="salesMetrics.quantityTrend > 0 ? 'up' : 'down'">
            <el-icon><ArrowUp v-if="salesMetrics.quantityTrend > 0" /><ArrowDown v-else /></el-icon>
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
          <div ref="productRankChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="category-card">
          <template #header>
            <div class="card-header">
              <span>类别销售占比</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { Refresh, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// 时间维度
const timeRange = ref('day')
const dateRange = ref([])

// 趋势类型
const trendType = ref('amount')

// 销售指标
const salesMetrics = reactive({
  totalAmount: '125,680.00',
  totalOrders: 856,
  avgOrderValue: '146.82',
  totalQuantity: 2341,
  amountTrend: 12.5,
  orderTrend: 8.3,
  avgTrend: 3.2,
  quantityTrend: 15.6
})

// 图表引用
const trendChartRef = ref(null)
const productRankChartRef = ref(null)
const categoryChartRef = ref(null)

let trendChart = null
let productRankChart = null
let categoryChart = null

// 初始化销售趋势图
const initTrendChart = () => {
  trendChart = echarts.init(trendChartRef.value)
  updateTrendChart()
}

// 更新趋势图
const updateTrendChart = () => {
  const dates = ['01-01', '01-02', '01-03', '01-04', '01-05', '01-06', '01-07']
  const data = trendType.value === 'amount' 
    ? [12000, 15000, 18000, 14000, 22000, 25000, 21000]
    : [120, 150, 180, 140, 220, 250, 210]
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: trendType.value === 'amount' ? '{b}<br/>销售额: ¥{c}' : '{b}<br/>订单数: {c}'
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
  trendChart.setOption(option)
}

// 初始化商品排行图
const initProductRankChart = () => {
  productRankChart = echarts.init(productRankChartRef.value)
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
      data: ['商品10', '商品9', '商品8', '商品7', '商品6', '商品5', '商品4', '商品3', '商品2', '商品1'],
      axisLabel: { interval: 0 }
    },
    series: [{
      data: [45, 52, 68, 75, 88, 95, 110, 125, 150, 180],
      type: 'bar',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#67c23a' },
          { offset: 1, color: '#95d475' }
        ])
      }
    }]
  }
  productRankChart.setOption(option)
}

// 初始化类别占比图
const initCategoryChart = () => {
  categoryChart = echarts.init(categoryChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center'
    },
    series: [{
      name: '类别销售占比',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['40%', '50%'],
      avoidLabelOverlap: false,
      label: {
        show: true,
        formatter: '{b}\n{d}%'
      },
      data: [
        { value: 335, name: '数码产品', itemStyle: { color: '#409eff' } },
        { value: 310, name: '服装鞋帽', itemStyle: { color: '#67c23a' } },
        { value: 234, name: '食品饮料', itemStyle: { color: '#e6a23c' } },
        { value: 135, name: '家居用品', itemStyle: { color: '#f56c6c' } },
        { value: 148, name: '美妆护肤', itemStyle: { color: '#909399' } }
      ]
    }]
  }
  categoryChart.setOption(option)
}

// 时间维度变化
const handleTimeRangeChange = (val) => {
  ElMessage.success(`已切换到${val === 'day' ? '日' : val === 'week' ? '周' : '月'}度视图`)
  refreshData()
}

// 日期变化
const handleDateChange = (val) => {
  if (val) {
    ElMessage.success('日期范围已更新')
    refreshData()
  }
}

// 趋势类型变化
const handleTrendTypeChange = () => {
  updateTrendChart()
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据已刷新')
  // 模拟数据更新
  updateTrendChart()
}

onMounted(() => {
  nextTick(() => {
    initTrendChart()
    initProductRankChart()
    initCategoryChart()
  })
  
  window.addEventListener('resize', () => {
    trendChart?.resize()
    productRankChart?.resize()
    categoryChart?.resize()
  })
})
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
