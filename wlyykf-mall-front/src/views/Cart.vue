<!-- src/views/Cart.vue -->
<template>
  <div class="cart-page">
    <h1>购物车</h1>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <!-- 错误信息 -->
    <div v-else-if="error" class="error">
      {{ error }}
      <button @click="loadCartItems">重试</button>
    </div>
    
    <!-- 购物车为空 -->
    <div v-else-if="items.length === 0" class="empty-cart">
      <p>购物车为空</p>
      <router-link to="/" class="continue-shopping">继续购物</router-link>
    </div>
    
    <!-- 购物车商品列表 -->
    <div v-else>
      <div class="cart-header">
        <label class="select-all">
          <input 
            type="checkbox" 
            :checked="isAllSelected" 
            @change="toggleSelectAll($event.target.checked)"
          />
          全选
        </label>
        <button @click="clearCart" class="clear-cart">清空购物车</button>
      </div>
      
      <div class="cart-items">
        <div 
          v-for="item in items" 
          :key="item.cartItemId" 
          class="cart-item"
        >
          <!-- 修改这一部分：移除 v-model，只使用 change 事件 -->
          <input 
            type="checkbox" 
            :checked="item.selected" 
            @change="toggleItemSelection(item)"
          />
          
          <!-- 修复图片显示问题 -->
          <img 
            :src="getImageUrl(item.productImage)" 
            :alt="item.productName" 
            class="product-image"
          />
          
          <div class="product-info">
            <h3>{{ item.productName }}</h3>
            <p>单价: ¥{{ item.price }}</p>
          </div>
          
          <div class="quantity-control">
            <button @click="updateQuantity(item, item.quantity - 1)">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="updateQuantity(item, item.quantity + 1)">+</button>
          </div>
          
          <div class="item-total">
            小计: ¥{{ item.totalPrice }}
          </div>
          
          <button @click="removeCartItem(item.cartItemId)" class="delete-btn">
            删除
          </button>
        </div>
      </div>
      
      <div class="cart-summary">
        <div class="summary-row">
          <span>总计:</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>
        
        <button 
          @click="checkout" 
          class="checkout-btn"
          :disabled="!hasSelectedItems || submitting"
          :loading="submitting"
        >
          去结算
        </button>
      </div>
    </div>
    
    <!-- 结算对话框 -->
    <el-dialog 
      v-model="checkoutDialogVisible" 
      title="确认订单" 
      width="500px"
      :before-close="handleCloseDialog"
    >
      <el-form 
        :model="checkoutForm" 
        :rules="checkoutRules" 
        ref="checkoutFormRef"
        label-width="80px"
      >
        <el-form-item label="收货地址" prop="address">
          <el-input 
            v-model="checkoutForm.address" 
            type="textarea"
            placeholder="请输入收货地址"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="checkoutDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmCheckout" 
          :loading="submitting"
        >
          确认下单
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<!-- src/views/Cart.vue -->
<script>
import { computed, onMounted, ref, reactive } from 'vue'
import { useCart } from '@/composables/useCart'
import { useOrderStore } from '@/stores/order.store'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

export default {
  name: 'Cart',
  setup() {
    const {
      items,
      total,
      totalPrice,
      loading,
      error,
      loadCartItems,
      removeCartItem,
      updateQuantity,
      toggleItemSelection,
      toggleSelectAll,
      clearCart,
      changePage
    } = useCart()
    
    const orderStore = useOrderStore()
    const router = useRouter()
    const checkoutFormRef = ref(null)
    const checkoutDialogVisible = ref(false)
    const submitting = ref(false)
    
    const checkoutForm = reactive({
      address: ''
    })
    
    const checkoutRules = {
      address: [
        { required: true, message: '请输入收货地址', trigger: 'blur' }
      ]
    }
    
    // 处理图片URL
    const getImageUrl = (imagePath) => {
      if (!imagePath) return ''
      if (imagePath.startsWith('http')) return imagePath
      return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(imagePath)}`
    }
    
    // 是否全选
    const isAllSelected = computed(() => {
      return items.value && items.value.length > 0 && items.value.every(item => item.selected)
    })
    
    // 是否有选中项
    const hasSelectedItems = computed(() => {
      return items.value && items.value.some(item => item.selected)
    })
    
    // 去结算 - 打开对话框
    const checkout = () => {
      checkoutForm.address = ''
      checkoutDialogVisible.value = true
    }
    
    // 确认下单
    const confirmCheckout = async () => {
      if (!checkoutFormRef.value) return
      
      await checkoutFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            submitting.value = true
            const result = await orderStore.submitOrder(checkoutForm.address)
            ElMessage.success('下单成功')
            checkoutDialogVisible.value = false
            // 跳转到订单页面
            router.push('/order')
          } catch (error) {
            ElMessage.error(error.message || '下单失败')
          } finally {
            submitting.value = false
          }
        }
      })
    }
    
    // 关闭对话框前的处理
    const handleCloseDialog = (done) => {
      if (submitting.value) {
        ElMessage.warning('正在提交订单，请稍候...')
        return
      }
      done()
    }
    
    // 页面加载时获取购物车数据
    onMounted(() => {
      console.log('加载购物车数据')
      loadCartItems()
    })
    
    return {
      items,
      total,
      totalPrice,
      loading,
      error,
      isAllSelected,
      hasSelectedItems,
      loadCartItems,
      removeCartItem,
      updateQuantity,
      toggleItemSelection,
      toggleSelectAll,
      clearCart,
      changePage,
      checkout,
      getImageUrl,
      // 结算相关
      checkoutDialogVisible,
      checkoutForm,
      checkoutFormRef,
      checkoutRules,
      confirmCheckout,
      submitting,
      handleCloseDialog
    }
  }
}
</script>

<style scoped>
.cart-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.loading, .error {
  text-align: center;
  padding: 50px 0;
}

.error button {
  margin-left: 10px;
  padding: 5px 10px;
}

.empty-cart {
  text-align: center;
  padding: 50px 0;
}

.continue-shopping {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #1890ff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.clear-cart {
  background: #ff4d4f;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.cart-items {
  margin: 20px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  gap: 15px;
}

.product-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.product-info {
  flex: 1;
}

.product-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity-control button {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
}

.quantity-control span {
  min-width: 30px;
  text-align: center;
}

.item-total {
  font-weight: bold;
  min-width: 100px;
  text-align: right;
}

.delete-btn {
  background: #ff4d4f;
  color: white;
  border: none;
  padding: 8px 15px;
  cursor: pointer;
  border-radius: 4px;
}

.cart-summary {
  position: sticky;
  bottom: 0;
  background: white;
  padding: 20px;
  border-top: 2px solid #eee;
  box-shadow: 0 -2px 5px rgba(0,0,0,0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.summary-row {
  font-size: 18px;
}

.total-price {
  font-weight: bold;
  color: #ff4d4f;
  font-size: 20px;
}

.checkout-btn {
  padding: 12px 30px;
  background: #ff4d4f;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.checkout-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>