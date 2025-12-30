// src/composables/useStatistic.js
import { ref, computed } from 'vue'
import { useStatisticStore } from '@/stores/statistic.store'

export const useStatistic = () => {
  const statisticStore = useStatisticStore()
  const currentType = ref(1) // 默认查询日统计数据
  
  // 加载所有统计数据
  const loadStatistics = async (type = currentType.value) => {
    currentType.value = type
    try {
      await Promise.all([
        statisticStore.fetchTotalAmount(type),
        statisticStore.fetchTopProducts(type),
        statisticStore.fetchOrderStats(type)
      ])
    } catch (error) {
      throw error
    }
  }
  
  // 更改查询类型
  const changeType = (type) => {
    loadStatistics(type)
  }

  return {
    // 状态
    totalAmount: computed(() => statisticStore.totalAmount),
    topProducts: computed(() => statisticStore.topProducts),
    orderStats: computed(() => statisticStore.orderStats),
    loading: computed(() => statisticStore.loading),
    error: computed(() => statisticStore.error),
    currentType,
    
    // 方法
    loadStatistics,
    changeType
  }
}