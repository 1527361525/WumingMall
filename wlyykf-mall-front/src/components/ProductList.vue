<template>
  <div class="product-list">
    <div class="list-header">
      <div class="sort-options">
        <span :class="{ active: orderType === '0' }" @click="changeSort('0')">
          最新
          <el-icon v-if="orderType === '0'">
            <SortUp v-if="sortDirection === '0'" />
            <SortDown v-else />
          </el-icon>
        </span>
        <span :class="{ active: orderType === '1' }" @click="changeSort('1')">
          价格
          <el-icon v-if="orderType === '1'">
            <SortUp v-if="sortDirection === '0'" />
            <SortDown v-else />
          </el-icon>
        </span>
        <span :class="{ active: orderType === '2' }" @click="changeSort('2')">
          销量
          <el-icon v-if="orderType === '2'">
            <SortUp v-if="sortDirection === '0'" />
            <SortDown v-else />
          </el-icon>
        </span>
      </div>
      
      <el-button 
        v-if="isAdmin" 
        type="primary" 
        size="small" 
        @click="showAddProductDialog"
      >
        添加商品
      </el-button>
    </div>
    
    <div v-loading="loading" class="products-container">
      <div v-if="products.length === 0" class="empty">
        <el-empty description="暂无商品" />
      </div>
      
      <div v-else class="products-grid">
        <div 
          v-for="product in products" 
          :key="product.productId" 
          class="product-card"
          @click="showProductDetail(product.productId)"
        >
          <div class="product-image">
            <img :src="getImageUrl(product.productImage)" alt="商品图片">
          </div>
          <div class="product-info">
            <h4 class="product-name">{{ product.name }}</h4>
            <div class="product-price">¥{{ product.price }}</div>
            <div class="product-sales">销量: {{ product.totalSales }}</div>
          </div>
          
          <div v-if="isAdmin" class="admin-actions">
            <el-button type="text" @click.stop="showEditProductDialog(product)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button type="text" @click.stop="deleteProduct(product.productId)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="pagination">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="changePage"
       />
    </div>
    
    <!-- 添加/编辑商品对话框 -->
    <el-dialog 
      v-model="productDialogVisible" 
      :title="productDialogTitle"
      width="600px"
    >
      <el-form :model="productForm" label-width="80px">
        <el-form-item label="商品名称" required>
          <el-input v-model="productForm.name" />
        </el-form-item>
        <el-form-item label="分类" required>
          <!-- 修改为与分类菜单一致的分类选择器 -->
          <el-popover
            placement="bottom-start"
            trigger="click"
            width="300"
            :visible="categoryPopoverVisible"
            @update:visible="categoryPopoverVisible = $event"
            @show="onCategoryPopoverShow"
          >
            <template #reference>
              <el-input
                v-model="selectedCategoryName"
                readonly
                placeholder="请选择分类"
                suffix-icon="ArrowDown"
              />
            </template>
            
            <div class="category-selector">
              <div class="category-menu-selector">
                <el-menu
                  class="category-list-selector"
                  :default-active="productForm.categoryId"
                >
                  <el-menu-item 
                    v-for="category in flattenedCategoryTree"
                    :key="category.categoryId"
                    :index="category.categoryId"
                    :class="{ 'sub-category-item': category.level > 0 }"
                    :style="{ paddingLeft: category.level > 0 ? (20 * category.level + 20) + 'px' : '20px' }"
                    @click="selectCategory(category)"
                  >
                    <span>{{ category.name }}</span>
                    
                    <el-icon 
                      v-if="shouldShowExpandIconInSelector(category)"
                      class="expand-icon"
                      @click.stop="toggleCategoryInSelector(category)"
                    >
                      <Loading v-if="loadingCategoriesInSelector.includes(category.categoryId)" />
                      <ArrowDown v-else-if="expandedCategoriesInSelector.includes(category.categoryId)" />
                      <ArrowRight v-else />
                    </el-icon>
                  </el-menu-item>
                </el-menu>
              </div>
              
              <div v-if="flattenedCategoryTree.length === 0 && !initialLoading" class="empty-category">
                暂无分类数据
              </div>
              
              <div v-if="initialLoading" class="loading-category">
                <el-icon class="is-loading"><Loading /></el-icon>
                <span>加载中...</span>
              </div>
            </div>
          </el-popover>
          
          <!-- 隐藏的真实值 -->
          <el-input 
            v-model="productForm.categoryId" 
            type="hidden" 
            required 
          />
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="productForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" required>
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="productForm.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="productForm.productImage" :src="getImageUrl(productForm.productImage)" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useProductStore } from '@/stores/product.store'
import { useCategoryStore } from '@/stores/category.store'
import { useAuthStore } from '@/stores/auth.store'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ArrowRight, ArrowDown, Loading } from '@element-plus/icons-vue'
import defaultProductImage from '@/assets/images/default-product.jpg'


const props = defineProps({
  categories: Array,
  categoryId: [String, Number]
})

const emit = defineEmits(['show-detail'])

const authStore = useAuthStore()
const productStore = useProductStore()
const categoryStore = useCategoryStore()

const isAdmin = computed(() => authStore.user?.isAdmin || false)
const productDialogVisible = ref(false)
const productDialogTitle = ref('添加商品')
const productForm = ref({
  productId: null,
  name: '',
  categoryId: '',
  price: 0,
  stock: 0,
  description: '',
  productImage: ''
})

// 分类选择器相关
const categoryPopoverVisible = ref(false)
const expandedCategoriesInSelector = ref([])
const loadingCategoriesInSelector = ref([])
const categoryTreeData = ref([])
const initialLoading = ref(false) // 初始加载状态

// 初始化数据
const loading = ref(false)
const products = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const orderType = ref('0')
const sortDirection = ref('0')

// 获取选中的分类名称
const selectedCategoryName = computed(() => {
  if (!productForm.value.categoryId) return ''
  
  const findCategoryName = (categories) => {
    for (const category of categories) {
      if (category.categoryId === productForm.value.categoryId) {
        return category.name
      }
      if (category.children && category.children.length > 0) {
        const name = findCategoryName(category.children)
        if (name) return name
      }
    }
    return ''
  }
  
  return findCategoryName(categoryTreeData.value)
})

// 展平分类树
const flattenedCategoryTree = computed(() => {
  const result = []
  
  const flatten = (categories, level = 0) => {
    if (!categories) return
    
    categories.forEach(category => {
      result.push({
        ...category,
        level: level
      })
      
      // 如果分类已展开且有子分类，则添加子分类
      if (expandedCategoriesInSelector.value.includes(category.categoryId) && 
          category.children && category.children.length > 0) {
        flatten(category.children, level + 1)
      }
    })
  }
  
  flatten(categoryTreeData.value)
  return result
})

// 判断是否应该显示展开图标（在选择器中）
const shouldShowExpandIconInSelector = (category) => {
  // 如果正在加载，显示加载图标
  if (loadingCategoriesInSelector.value.includes(category.categoryId)) {
    return true;
  }
  
  // 如果已经展开，显示展开图标
  if (expandedCategoriesInSelector.value.includes(category.categoryId)) {
    return true;
  }
  
  // 根据 hasChildren 字段判断是否应该显示展开图标
  if (category.hasChildren !== undefined) {
    return category.hasChildren;
  }
  
  // 回退到原来的逻辑
  if (category.children && category.children.length > 0) {
    return true;
  }
  
  return false;
}

// 在选择器中切换分类展开/收起
const toggleCategoryInSelector = async (category) => {
  // 如果正在加载，不处理
  if (loadingCategoriesInSelector.value.includes(category.categoryId)) {
    return;
  }
  
  const categoryId = category.categoryId;
  
  // 如果已经展开，则收起
  if (expandedCategoriesInSelector.value.includes(categoryId)) {
    const index = expandedCategoriesInSelector.value.indexOf(categoryId);
    expandedCategoriesInSelector.value.splice(index, 1);
    return;
  }
  
  // 检查 hasChildren 字段，如果明确为 false，则不展开
  if (category.hasChildren === false) {
    return;
  }
  
  // 标记为正在加载
  loadingCategoriesInSelector.value.push(categoryId);
  
  try {
    const children = await categoryStore.fetchCategories(categoryId);
    
    // 移除加载状态
    const loadingIndex = loadingCategoriesInSelector.value.indexOf(categoryId);
    if (loadingIndex > -1) {
      loadingCategoriesInSelector.value.splice(loadingIndex, 1);
    }
    
    // 使用深度拷贝确保响应式更新
    const updatedCategories = JSON.parse(JSON.stringify(categoryTreeData.value));
    
    const updateCategoryChildren = (categories) => {
      for (let i = 0; i < categories.length; i++) {
        if (categories[i].categoryId === categoryId) {
          categories[i].children = children;
          // 更新 hasChildren 字段
          categories[i].hasChildren = children && children.length > 0;
          return true;
        }
        if (categories[i].children && categories[i].children.length > 0) {
          if (updateCategoryChildren(categories[i].children)) {
            return true;
          }
        }
      }
      return false;
    };
    
    if (updateCategoryChildren(updatedCategories)) {
      categoryTreeData.value = updatedCategories;
    }
    
    // 只有当确实有子分类时才展开
    if (children && children.length > 0) {
      expandedCategoriesInSelector.value.push(categoryId);
    }
  } catch (error) {
    const loadingIndex = loadingCategoriesInSelector.value.indexOf(categoryId);
    if (loadingIndex > -1) {
      loadingCategoriesInSelector.value.splice(loadingIndex, 1);
    }
    ElMessage.error('获取子分类失败');
    console.error('获取子分类失败:', error);
  }
}

// 选择分类
const selectCategory = (category) => {
  productForm.value.categoryId = category.categoryId
  categoryPopoverVisible.value = false
}

// 弹出框显示时加载顶级分类
const onCategoryPopoverShow = async () => {
  // 只有当还没有加载过数据时才加载顶级分类
  if (categoryTreeData.value.length === 0 && !initialLoading.value) {
    initialLoading.value = true
    try {
      const topLevelCategories = await categoryStore.fetchCategories(0)
      categoryTreeData.value = topLevelCategories || []
    } catch (error) {
      ElMessage.error('获取分类数据失败')
    } finally {
      initialLoading.value = false
    }
  }
}

// 获取商品数据
const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await productStore.fetchProducts({
      categoryId: props.categoryId,
      orderType: orderType.value,
      sortDirection: sortDirection.value,
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    products.value = res.data
    total.value = res.total
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 改变排序
const changeSort = (type) => {
  if (orderType.value === type) {
    // 如果点击的是当前排序字段，则切换方向
    sortDirection.value = sortDirection.value === '0' ? '1' : '0'
  } else {
    // 如果点击的是其他排序字段，则设置为该字段并默认升序
    orderType.value = type
    sortDirection.value = '0'
  }
  // 重新获取数据
  currentPage.value = 1
  fetchProducts()
}

// 改变页码
const changePage = (page) => {
  currentPage.value = page
  fetchProducts()
}

// 显示商品详情
const showProductDetail = (productId) => {
  emit('show-detail', productId)
}

// 显示添加商品对话框
const showAddProductDialog = () => {
  productForm.value = {
    productId: null,
    name: '',
    categoryId: '',
    price: 0,
    stock: 0,
    description: '',
    productImage: ''
  }
  productDialogTitle.value = '添加商品'
  productDialogVisible.value = true
  // 重置分类选择器状态
  categoryTreeData.value = []
  expandedCategoriesInSelector.value = []
  loadingCategoriesInSelector.value = []
}

// 显示编辑商品对话框
const showEditProductDialog = async (product) => {
  try {
    // 从后端获取商品详细信息用于编辑回显
    const productDetail = await productStore.fetchProductForUpdate(product.productId)
    
    productForm.value = {
      productId: productDetail.productId,
      name: productDetail.name,
      categoryId: productDetail.categoryId,
      price: productDetail.price,
      stock: productDetail.stock,
      description: productDetail.description,
      productImage: productDetail.productImage
    }
    
    productDialogTitle.value = '编辑商品'
    productDialogVisible.value = true
    // 重置分类选择器状态
    categoryTreeData.value = []
    expandedCategoriesInSelector.value = []
    loadingCategoriesInSelector.value = []
  } catch (error) {
    ElMessage.error('获取商品信息失败')
  }
}

// 保存商品
const saveProduct = async () => {
  try {
    if (productForm.value.productId) {
      await productStore.updateProduct(productForm.value)
      ElMessage.success('商品更新成功')
    } else {
      await productStore.addProduct(productForm.value)
      ElMessage.success('商品添加成功')
    }
    productDialogVisible.value = false
    // 刷新商品数据
    await fetchProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除商品
const deleteProduct = (productId) => {
  ElMessageBox.confirm('确定删除此商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await productStore.deleteProduct(productId)
      ElMessage.success('商品删除成功')
      // 刷新商品数据
      await fetchProducts()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 图片上传相关
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/file/uploadImage`
const uploadHeaders = {
  'Authorization': `Bearer ${localStorage.getItem('token')}`
}

const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    productForm.value.productImage = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.info || '图片上传失败')
  }
}

const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片必须是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 获取图片完整URL
const getImageUrl = (sourceName) => {
  if (!sourceName) return defaultProductImage
  if (sourceName.startsWith('http')) return sourceName
  return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(sourceName)}`
}

defineExpose({
  fetchProducts
})

// 监听分类变化
// 注意：如果需要监听props.categoryId变化，需要引入watch
</script>

<style scoped>
.product-list {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-shrink: 0;
}

.sort-options {
  display: flex;
  gap: 20px;
}

.sort-options span {
  cursor: pointer;
  color: #999;
  display: flex;
  align-items: center;
  gap: 5px;
}

.sort-options span.active {
  color: #409eff;
  font-weight: bold;
}

.products-container {
  flex: 1;
  min-height: 500px;
  overflow-y: auto;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 25px;
  padding: 10px 0;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.product-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 15px;
}

.product-info .product-name {
  margin: 0 0 10px;
  font-size: 16px;
  height: 44px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2; /* 添加标准属性 */
  -webkit-box-orient: vertical;
  font-weight: 500;
}

.product-info .product-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 18px;
  margin-bottom: 5px;
}

.product-info .product-sales {
  color: #999;
  font-size: 14px;
}

.admin-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  padding: 5px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
  flex-shrink: 0;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

/* 新增的分类选择器样式 */
.category-selector {
  max-height: 300px;
  overflow-y: auto;
}

.category-menu-selector {
  width: 100%;
}

.category-list-selector {
  border-right: none;
}

.category-list-selector .el-menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sub-category-item {
  background-color: #f9f9f9;
}

.expand-icon {
  margin-left: auto;
  cursor: pointer;
  color: #999;
  transition: transform 0.2s;
}

.expand-icon:hover {
  color: #409eff;
}

.empty-category {
  text-align: center;
  padding: 20px;
  color: #999;
}

.loading-category {
  text-align: center;
  padding: 20px;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 20px;
  }
  
  .product-image {
    height: 180px;
  }
}

@media (max-width: 768px) {
  .product-list {
    padding: 15px;
  }
  
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 15px;
  }
  
  .product-image {
    height: 150px;
  }
  
  .product-info {
    padding: 10px;
  }
  
  .product-info .product-name {
    font-size: 14px;
    height: 36px;
  }
  
  .product-info .product-price {
    font-size: 16px;
  }
}
</style>