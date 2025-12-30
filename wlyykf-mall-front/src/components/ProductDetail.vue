<template>
  <div>
    <el-dialog 
      v-model="visible" 
      :title="product.name" 
      width="600px"
      @close="close"
    >
      <div class="product-detail">
        <!-- 商品图片和信息展示部分保持不变 -->
        <div class="product-image">
          <img :src="getImageUrl(product.productImage)" alt="商品图片">
        </div>
        
        <div class="product-info">
          <div class="info-item">
            <label>价格：</label>
            <span class="price">¥{{ product.price }}</span>
          </div>
          
          <div class="info-item">
            <label>销量：</label>
            <span>{{ product.totalSales }}</span>
          </div>
          
          <div class="info-item">
            <label>描述：</label>
            <p>{{ product.description || '暂无描述' }}</p>
          </div>
          
          <div class="actions">
            <el-button type="primary" @click="showQuantityDialog">加入购物车</el-button>
            <el-button @click="showBuyNowDialog">立即购买</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
    
    <!-- 数量选择弹窗（用于加入购物车） -->
    <el-dialog 
      v-model="quantityDialogVisible" 
      title="选择购买数量" 
      width="300px"
      @close="closeQuantityDialog"
    >
      <div class="quantity-selector">
        <el-input-number 
          v-model="selectedQuantity" 
          :min="1" 
          :max="99"
          size="large"
        />
      </div>
      <template #footer>
        <el-button @click="closeQuantityDialog">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmAddToCart" 
          :loading="cartLoading"
        >
          确认
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 立即购买地址输入弹窗 -->
    <el-dialog 
      v-model="buyNowDialogVisible" 
      title="确认购买信息" 
      width="400px"
      @close="closeBuyNowDialog"
    >
      <el-form>
        <el-form-item label="购买数量">
          <el-input-number 
            v-model="buyQuantity" 
            :min="1" 
            :max="99"
            size="small"
          />
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input 
            v-model="buyAddress" 
            placeholder="请输入收货地址"
            type="textarea"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="closeBuyNowDialog">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmBuyNow" 
          :loading="buyLoading"
        >
          确认购买
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useProductStore } from '@/stores/product.store'
import { useCartStore } from '@/stores/cart.store'
import { useOrder } from '@/composables/useOrder'
import { ElMessage } from 'element-plus'
import defaultProductImage from '@/assets/images/default-product.jpg'

const props = defineProps({
  productId: String,
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue', 'close'])

const productStore = useProductStore()
const cartStore = useCartStore()
const { buyNow } = useOrder() // 使用新添加的buyNow方法

const visible = ref(false)
const cartLoading = ref(false)
const buyLoading = ref(false)

// 原有的加入购物车相关状态
const quantityDialogVisible = ref(false)
const selectedQuantity = ref(1)

// 新增立即购买相关状态
const buyNowDialogVisible = ref(false)
const buyQuantity = ref(1)
const buyAddress = ref('')

const product = ref({
  name: '',
  productImage: '',
  price: 0,
  totalSales: 0,
  description: ''
})

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
})

// 监听productId变化
watch(() => props.productId, async (newVal) => {
  if (newVal) {
    try {
      product.value = await productStore.fetchProductDetail(newVal)
    } catch (error) {
      ElMessage.error('获取商品详情失败')
    }
  }
}, { immediate: true })

// 监听visible变化
watch(visible, (newVal) => {
  emit('update:modelValue', newVal)
})

// 关闭弹窗
const close = () => {
  visible.value = false
  emit('close')
}

// 显示数量选择弹窗（加入购物车）
const showQuantityDialog = () => {
  selectedQuantity.value = 1
  quantityDialogVisible.value = true
}

// 关闭数量选择弹窗（加入购物车）
const closeQuantityDialog = () => {
  quantityDialogVisible.value = false
  selectedQuantity.value = 1
}

// 确认加入购物车
const confirmAddToCart = async () => {
  try {
    cartLoading.value = true
    await cartStore.addProductToCart({
      productId: props.productId,
      quantity: selectedQuantity.value
    })
    ElMessage.success('已加入购物车')
    closeQuantityDialog()
  } catch (error) {
    ElMessage.error(error.message || '加入购物车失败')
  } finally {
    cartLoading.value = false
  }
}

// 显示立即购买弹窗
const showBuyNowDialog = () => {
  buyQuantity.value = 1
  buyAddress.value = ''
  buyNowDialogVisible.value = true
}

// 关闭立即购买弹窗
const closeBuyNowDialog = () => {
  buyNowDialogVisible.value = false
  buyQuantity.value = 1
  buyAddress.value = ''
}

// 确认立即购买
const confirmBuyNow = async () => {
  if (!buyAddress.value.trim()) {
    ElMessage.warning('请输入收货地址')
    return
  }

  try {
    buyLoading.value = true
    const result = await buyNow({
      productId: props.productId,
      quantity: buyQuantity.value.toString(),
      address: buyAddress.value
    })
    
    ElMessage.success('购买成功')
    closeBuyNowDialog()
    close() // 关闭商品详情弹窗
    
    // 可以在这里添加跳转到订单页面的逻辑
    // 例如: router.push('/orders')
  } catch (error) {
    ElMessage.error(error.message || '购买失败')
  } finally {
    buyLoading.value = false
  }
}

// 获取图片完整URL
const getImageUrl = (sourceName) => {
  if (!sourceName) return defaultProductImage
  if (sourceName.startsWith('http')) return sourceName
  return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(sourceName)}`
}
</script>

<style scoped>
/* 样式部分保持不变 */
.product-detail {
  display: flex;
  gap: 20px;
}

.product-image {
  width: 200px;
  height: 200px;
  border: 1px solid #eee;
  border-radius: 4px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  flex: 1;
}

.product-info .info-item {
  margin-bottom: 15px;
}

.product-info .info-item label {
  font-weight: bold;
  color: #666;
}

.product-info .info-item .price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.product-info .info-item p {
  margin: 5px 0 0;
  color: #666;
}

.actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.quantity-selector {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>