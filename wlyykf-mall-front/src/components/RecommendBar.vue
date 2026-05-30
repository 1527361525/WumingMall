<template>
  <div class="recommend-bar">
    <div class="recommend-header">
      <h3 class="recommend-title">商品推荐</h3>
    </div>
    
    <div v-if="loading && recommendations.length === 0" class="recommend-loading">
      <el-skeleton :rows="5" animated />
    </div>
    
    <div v-else-if="recommendations.length === 0" class="recommend-empty">
      <el-empty description="暂无推荐商品" :image-size="60" />
    </div>
    
    <div v-else class="recommend-list">
      <div 
        v-for="item in recommendations" 
        :key="item.productId"
        class="recommend-item"
        @click="handleItemClick(item)"
      >
        <div class="recommend-image-wrapper">
          <img 
            :src="getImageUrl(item.productImage)" 
            :alt="item.name"
            class="recommend-image"
            @error="handleImageError"
          />
        </div>
        <div class="recommend-info">
          <div class="recommend-name" :title="item.name">{{ item.name }}</div>
          <div class="recommend-price">¥{{ formatPrice(item.price) }}</div>
          <div class="recommend-count">{{ item.buyCount }}人购买</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue'
import { useRecommendStore } from '@/stores/recommend.store'
import { useUserStore } from '@/stores/user.store'
import defaultProductImage from '@/assets/images/default-product.jpg'

import { ElMessage } from 'element-plus'

const props = defineProps({
  userId: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['select-product'])

const recommendStore = useRecommendStore()
const userStore = useUserStore()

const recommendations = computed(() => recommendStore.collaborativeRecommendations)
const loading = computed(() => recommendStore.loading)

// 获取当前用户ID
const currentUserId = computed(() => {
  return props.userId || userStore.user?.userId
})

// 加载推荐数据
const loadRecommendations = async () => {
  const uid = currentUserId.value
  if (!uid) {
    console.log('用户未登录，无法获取推荐')
    return
  }
  
  try {
    await recommendStore.fetchCollaborativeRecommend(uid)
  } catch (error) {
    console.error('获取推荐商品失败:', error)
    ElMessage.error('获取推荐商品失败')
  }
}

// 处理商品点击
const handleItemClick = (item) => {
  emit('select-product', item.productId)
}

// 处理图片加载失败
const handleImageError = (e) => {
  e.target.src = defaultProductImage
}

// 获取图片完整URL
const getImageUrl = (sourceName) => {
  if (!sourceName) return defaultProductImage
  if (sourceName.startsWith('http')) return sourceName
  return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(sourceName)}`
}

// 格式化价格
const formatPrice = (price) => {
  if (!price) return '0.00'
  return parseFloat(price).toFixed(2)
}

// 监听用户ID变化
watch(() => currentUserId.value, (newVal) => {
  if (newVal) {
    loadRecommendations()
  }
}, { immediate: true })

onMounted(() => {
  // 如果store中没有用户信息，先获取用户信息
  if (!userStore.user) {
    userStore.fetchCurrentUser().then(() => {
      loadRecommendations()
    }).catch(() => {
      // 获取用户信息失败，不显示推荐
    })
  } else {
    loadRecommendations()
  }
})
</script>

<style scoped>
.recommend-bar {
  width: 220px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 16px;
  height: fit-content;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.recommend-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.recommend-loading {
  padding: 20px 0;
}

.recommend-empty {
  padding: 20px 0;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommend-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.recommend-item:hover {
  background: #f5f7fa;
  border-color: #e4e7ed;
  transform: translateX(4px);
}

.recommend-image-wrapper {
  width: 60px;
  height: 60px;
  flex-shrink: 0;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
}

.recommend-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommend-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.recommend-name {
  font-size: 13px;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-all;
}

.recommend-price {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
  margin-top: 4px;
}

.recommend-count {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

/* 滚动条样式 */
.recommend-bar::-webkit-scrollbar {
  width: 4px;
}

.recommend-bar::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 2px;
}

.recommend-bar::-webkit-scrollbar-track {
  background: transparent;
}

@media (max-width: 1200px) {
  .recommend-bar {
    width: 200px;
  }
}

@media (max-width: 992px) {
  .recommend-bar {
    display: none;
  }
}
</style>
