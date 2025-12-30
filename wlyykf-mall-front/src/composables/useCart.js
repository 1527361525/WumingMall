// src/composables/useCart.js
import { ref, computed } from 'vue' // 添加 computed 导入
import { useCartStore } from '@/stores/cart.store'

export const useCart = () => {
  const cartStore = useCartStore()
  const currentPage = ref(1)
  const pageSize = ref(10)
  
  // 加载购物车商品
  const loadCartItems = async () => {
    cartStore.setPagination(currentPage.value, pageSize.value)
    await cartStore.fetchCartItems()
    await cartStore.calculateTotalPrice()
  }
  
  // 删除购物车商品
  const removeCartItem = async (cartItemId) => {
    await cartStore.deleteCartItem(cartItemId)
  }
  
  // 更新购物车商品数量
  const updateQuantity = async (item, quantity) => {
    if (quantity <= 0) {
      await removeCartItem(item.cartItemId)
      return
    }
    
    // 先更新本地数据以获得更好的用户体验
    const originalQuantity = item.quantity
    const originalTotalPrice = item.totalPrice
    
    item.quantity = quantity
    item.totalPrice = item.price * quantity
    
    try {
      await cartStore.updateCartItem({
        cartItemId: item.cartItemId,
        quantity: quantity,
        selected: item.selected
      })
    } catch (error) {
      // 如果更新失败，恢复原始值
      item.quantity = originalQuantity
      item.totalPrice = originalTotalPrice
      throw error
    }
  }
  
  // 切换商品选中状态
const toggleItemSelection = async (item) => {
  // 不要直接修改 item.selected，而是将其取反的值传给后端
  const newSelectedValue = !item.selected;
  
  try {
    await cartStore.updateCartItem({
      cartItemId: item.cartItemId,
      quantity: item.quantity,
      selected: newSelectedValue
    });
    
    // 只有在更新成功后才更新本地状态
    item.selected = newSelectedValue;
  } catch (error) {
    // 如果更新失败，保持原来的状态，不需要做任何事
    throw error;
  }
}
  
  // 全选/取消全选
const toggleSelectAll = async (selected) => {
  try {
    // 创建一个 promises 数组来并行处理所有更新
    const updatePromises = cartStore.items.map(item => 
      cartStore.updateCartItem({
        cartItemId: item.cartItemId,
        quantity: item.quantity,
        selected: selected
      })
    );
    
    // 等待所有更新完成
    await Promise.all(updatePromises);
    
    // 所有更新成功后再更新本地状态
    for (const item of cartStore.items) {
      item.selected = selected;
    }
  } catch (error) {
    // 如果有任何失败，抛出错误，让 UI 层处理
    throw error;
  }
}
  
  // 清空购物车
  const clearCart = async () => {
    await cartStore.clearCart()
  }
  
  // 添加商品到购物车
  const addProductToCart = async (productData) => {
    return await cartStore.addProductToCart(productData)
  }
  
  // 计算总价
  const calculateTotalPrice = async () => {
    await cartStore.calculateTotalPrice()
  }
  
  // 改变页码
  const changePage = (page) => {
    currentPage.value = page
    loadCartItems()
  }
  
  return {
    // 状态
    items: computed(() => cartStore.items),
    total: computed(() => cartStore.pagination.total),
    totalPrice: computed(() => cartStore.totalPrice),
    loading: computed(() => cartStore.loading),
    error: computed(() => cartStore.error),
    currentPage,
    pageSize,
    
    // 方法
    loadCartItems,
    removeCartItem,
    updateQuantity,
    toggleItemSelection,
    toggleSelectAll,
    clearCart,
    addProductToCart,
    calculateTotalPrice,
    changePage
  }
}