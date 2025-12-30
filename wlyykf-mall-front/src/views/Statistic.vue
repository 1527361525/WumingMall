<!-- src/views/Statistic.vue -->
<template>
  <div class="statistic-page">
    <h1>统计分析</h1>
    
    <!-- 类型选择 -->
    <div class="type-selector">
      <el-radio-group v-model="currentType" @change="changeType">
        <el-radio-button :label="1">今日</el-radio-button>
        <el-radio-button :label="2">本周</el-radio-button>
        <el-radio-button :label="3">本月</el-radio-button>
        <el-radio-button :label="4">本年</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <!-- 错误信息 -->
    <div v-else-if="error" class="error">
      {{ error }}
      <el-button @click="loadStatistics" type="primary">重试</el-button>
    </div>
    
    <!-- 统计内容 -->
    <div v-else class="statistic-content">
      <!-- 销售额卡片 -->
      <el-card class="statistic-card sales-card">
        <template #header>
          <div class="card-header">
            <span>销售额统计</span>
          </div>
        </template>
        <div class="sales-content">
          <div class="sales-amount">¥{{ totalAmount }}</div>
        </div>
      </el-card>
      
      <!-- 订单统计 -->
      <el-card class="statistic-card order-card">
        <template #header>
          <div class="card-header">
            <span>订单统计</span>
          </div>
        </template>
        <div class="order-stats">
          <el-table :data="orderStats" style="width: 100%">
            <el-table-column prop="orderStatusName" label="订单状态" />
            <el-table-column prop="typeCount" label="数量" />
          </el-table>
        </div>
      </el-card>
      
      <!-- 热销商品 -->
      <el-card class="statistic-card products-card">
        <template #header>
          <div class="card-header">
            <span>热销商品 TOP 10</span>
          </div>
        </template>
        <div class="top-products">
          <el-table :data="topProducts" style="width: 100%">
            <el-table-column label="排名" width="80">
              <template #default="scope">
                <span :class="getRankClass(scope.$index)">
                  {{ scope.$index + 1 }}
                </span>
              </template>
            </el-table-column>
            <el-table-column label="商品图片" prop="productImage">
              <template #default="scope">
                <img :src="getImageUrl(scope.row.productImage)" class="product-image" />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="totalSales" label="销量" />
          </el-table>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { onMounted } from 'vue'
import { useStatistic } from '@/composables/useStatistic'
import { useAuthStore } from '@/stores/auth.store'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Statistic',
  setup() {
    const {
      totalAmount,
      topProducts,
      orderStats,
      loading,
      error,
      currentType,
      loadStatistics,
      changeType
    } = useStatistic()
    
    const authStore = useAuthStore()
    const router = useRouter()
    
    // 页面加载时获取统计数据
    onMounted(async () => {
      // 检查是否为管理员
      try {
        const isAdmin = await authStore.checkIsAdmin()
        if (!isAdmin) {
          ElMessage.error('当前用户无权限查看该页面')
          router.push('/')
          return
        }
        loadStatistics()
      } catch (error) {
        ElMessage.error('权限检查失败')
        router.push('/')
      }
    })

    // 在组件中添加方法处理图片URL
    const getImageUrl = (imagePath) => {
      // 如果imagePath已经是完整URL则直接返回
      if (imagePath && (imagePath.startsWith('http') || imagePath.startsWith('/file/getResource'))) {
        return imagePath
      }
      // 否则拼接基础路径
      return imagePath ? `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${imagePath}` : ''
    }
    
    // 获取排名样式
    const getRankClass = (index) => {
      return [
        'rank-number',
        {
          'rank-first': index === 0,
          'rank-second': index === 1,
          'rank-third': index === 2
        }
      ]
    }
    
    return {
      totalAmount,
      topProducts,
      orderStats,
      loading,
      error,
      currentType,
      loadStatistics,
      changeType,
      getImageUrl,
      getRankClass
    }
  }
}
</script>

<style scoped>
.statistic-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.type-selector {
  margin-bottom: 20px;
  text-align: center;
}

.loading, .error {
  text-align: center;
  padding: 50px 0;
}

.statistic-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.statistic-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.sales-content {
  text-align: center;
  padding: 20px 0;
}

.sales-amount {
  font-size: 32px;
  font-weight: bold;
  color: #ff4d4f;
}

.order-stats, .top-products {
  padding: 10px 0;
}

.product-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

/* 排名样式 */
.rank-number {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  background-color: #f0f0f0;
  font-weight: bold;
}

.rank-first {
  background-color: #ffd700;
  color: #fff;
}

.rank-second {
  background-color: #c0c0c0;
  color: #fff;
}

.rank-third {
  background-color: #cd7f32;
  color: #fff;
}

@media (max-width: 768px) {
  .statistic-content {
    grid-template-columns: 1fr;
  }
}
</style>