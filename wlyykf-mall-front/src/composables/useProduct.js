import { ref } from 'vue'
import { useProductStore } from '@/stores/product.store'

export const useProduct = () => {
  const productStore = useProductStore()
  const currentPage = ref(1)
  const pageSize = ref(10)
  const orderType = ref('0') // 0:上架时间, 1:价格, 2:销量
  const sortDirection = ref('1') // 0:升序, 1:降序
  const categoryId = ref(null)
  
  // 加载商品
  const loadProducts = async () => {
    const params = {
      categoryId: categoryId.value,
      orderType: orderType.value,
      sortDirection: sortDirection.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    await productStore.fetchProducts(params)
  }
  
  // 改变排序方式
  const changeSort = (type) => {
    if (orderType.value === type) {
      sortDirection.value = sortDirection.value === '1' ? '0' : '1'
    } else {
      orderType.value = type
      sortDirection.value = '1' // 默认降序
    }
    loadProducts()
  }
  
  // 改变分类
  const changeCategory = (id) => {
    categoryId.value = id
    currentPage.value = 1
    loadProducts()
  }
  
  // 改变页码
  const changePage = (page) => {
    currentPage.value = page
    loadProducts()
  }
  
  return {
    products: productStore.products,
    total: productStore.total,
    loading: productStore.loading,
    error: productStore.error,
    currentPage,
    pageSize,
    orderType,
    sortDirection,
    categoryId,
    loadProducts,
    changeSort,
    changeCategory,
    changePage
  }
}