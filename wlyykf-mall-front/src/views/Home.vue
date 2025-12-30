<template>
  <div class="home-container">
    <div class="home-content">
      <CategoryMenu 
        :categories="categories" 
        @show-products="handleShowProducts"
        @select="handleCategorySelect"
        @refresh-categories="handleRefreshCategories"
      />
      
      <ProductList 
        ref="productListRef"
        :categories="categories"
        :category-id="selectedCategoryId"
        @show-detail="showProductDetail"
      />
    </div>
    
    <ProductDetail 
      v-model="isProductDetailVisible" 
      :product-id="selectedProductId" 
      @close="closeProductDetail" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useCategoryStore } from '@/stores/category.store'
import { useAuthStore } from '@/stores/auth.store'
import CategoryMenu from '@/components/CategoryMenu.vue'
import ProductList from '@/components/ProductList.vue'
import ProductDetail from '@/components/ProductDetail.vue'
import { ElMessage } from 'element-plus'

const authStore = useAuthStore()
const categoryStore = useCategoryStore()

const categories = ref([])
const selectedProductId = ref(null)
const isProductDetailVisible = ref(false)
const selectedCategoryId = ref(null)
const isAdmin = ref(false)
const productListRef = ref(null)

// 初始化
onMounted(async () => {
  try {
    // 检查是否为管理员并更新用户状态
    await authStore.checkIsAdmin()
    
    // 加载分类
    categories.value = await categoryStore.fetchCategories()
  } catch (error) {
    ElMessage.error('用户未登录')
  }
})

// 处理分类选择
const handleCategorySelect = (categoryId) => {
  // 确保 categoryId 是字符串
  selectedCategoryId.value = String(categoryId)
}

// 显示商品详情
const showProductDetail = (productId) => {
  // 确保 productId 是字符串
  selectedProductId.value = String(productId)
  isProductDetailVisible.value = true
}

// 关闭商品详情
const closeProductDetail = () => {
  isProductDetailVisible.value = false
  selectedProductId.value = null
}

// 处理显示商品事件
const handleShowProducts = (categoryId) => {
  // 确保 categoryId 是字符串
  selectedCategoryId.value = String(categoryId)
  // 使用 nextTick 确保响应式更新完成后再调用 fetchProducts
  nextTick(() => {
    // 调用 ProductList 组件的 fetchProducts 方法
    if (productListRef.value) {
      productListRef.value.fetchProducts()
    }
  })
}

const handleRefreshCategories = (newCategories) => {
  categories.value = newCategories
}

// 添加 nextTick import
import { nextTick } from 'vue'
</script>

<style scoped>
.home-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
}

.home-content {
  display: flex;
  gap: 20px;
  height: calc(100vh - 100px); /* 估算高度，减去顶部导航和间距 */
}

@media (max-width: 768px) {
  .home-container {
    padding: 10px;
  }
  
  .home-content {
    gap: 10px;
    height: calc(100vh - 80px);
  }
}
</style>