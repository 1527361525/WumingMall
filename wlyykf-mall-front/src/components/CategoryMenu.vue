<template>
  <div class="category-menu">
    <div class="menu-header">
      <h3>商品分类</h3>
      <el-button 
        v-if="isAdmin" 
        type="primary" 
        size="small" 
        @click="showAddCategoryDialog"
      >
        添加分类
      </el-button>
    </div>
    
    <el-menu
      :default-active="activeCategory?.categoryId"
      class="category-list"
      @select="handleSelect"
    >
      <el-menu-item 
        v-for="category in flattenedCategories"
        :key="category.categoryId"
        :index="category.categoryId"
        :class="{ 'sub-category-item': category.level > 0 }"
        :style="{ paddingLeft: category.level > 0 ? (20 * category.level + 20) + 'px' : '20px' }"
      >
        <span>{{ category.name }}</span>
        
        <!-- 修改部分：优化展开图标显示逻辑 -->
        <el-icon 
          v-if="shouldShowExpandIcon(category)"
          class="expand-icon"
          @click.stop="toggleCategory(category)"
        >
          <Loading v-if="loadingCategories.includes(category.categoryId)" />
          <ArrowDown v-else-if="expandedCategories.includes(category.categoryId)" />
          <ArrowRight v-else />
        </el-icon>
        
        <template v-if="isAdmin">
          <el-button 
            type="text" 
            size="small" 
            @click.stop="showEditCategoryDialog(category)"
          >
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-button 
            type="text" 
            size="small" 
            @click.stop="deleteCategory(category.categoryId)"
          >
            <el-icon><Delete /></el-icon>
          </el-button>
        </template>
      </el-menu-item>
    </el-menu>
    
    <!-- 添加/编辑分类对话框 -->
    <el-dialog 
      v-model="categoryDialogVisible" 
      :title="categoryDialogTitle"
      width="500px"
    >
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="categoryForm.name" />
        </el-form-item>
        <el-form-item label="父分类">
          <div class="custom-tree-select">
            <div 
              class="tree-select-trigger" 
              @click="toggleParentCategorySelector"
            >
              <span>{{ selectedParentCategoryName || '请选择' }}</span>
              <el-icon class="arrow-icon">
                <ArrowDown />
              </el-icon>
            </div>
            
            <div v-show="showParentCategoryTree" class="tree-select-dropdown">
              <div class="tree-select-option" @click="selectParentCategory({ categoryId: 0, name: '顶级分类' })">
                <span>顶级分类</span>
              </div>
              <div 
                v-for="category in parentCategoryOptions"
                :key="category.categoryId"
                class="tree-select-option"
                :class="{ 'has-children': shouldShowExpandIconForParent(category) }"
                :style="{ paddingLeft: (20 * category.level + 10) + 'px' }"
                @click="selectParentCategory(category)"
              >
                <span>{{ category.name }}</span>
                <el-icon 
                  v-if="shouldShowExpandIconForParent(category)"
                  class="expand-icon"
                  @click.stop="toggleParentCategory(category)"
                >
                  <Loading v-if="parentLoadingCategories.includes(category.categoryId)" />
                  <ArrowDown v-else-if="parentExpandedCategories.includes(category.categoryId)" />
                  <ArrowRight v-else />
                </el-icon>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="1" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useCategoryStore } from '@/stores/category.store'
import { useAuthStore } from '@/stores/auth.store'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight, ArrowDown, Edit, Delete, Loading } from '@element-plus/icons-vue'

const props = defineProps({
  categories: Array
})

const emit = defineEmits(['select', 'show-products'])

const authStore = useAuthStore()
const categoryStore = useCategoryStore()

const isAdmin = computed(() => authStore.user?.isAdmin || false)
const categoryDialogVisible = ref(false)
const categoryDialogTitle = ref('添加分类')
const categoryForm = ref({
  categoryId: null,
  name: '',
  parentId: 0,
  sort: 1
})

// 管理展开的分类ID
const expandedCategories = ref([])
// 管理正在加载的分类ID
const loadingCategories = ref([])

// 父分类选择器相关状态
const showParentCategoryTree = ref(false)
const parentExpandedCategories = ref([])
const parentLoadingCategories = ref([])
const parentCategoryData = ref([])
const isParentCategoryFirstLoad = ref(true)

// 存储分类数据的响应式副本
const categoryData = ref(props.categories || [])

// 监听props.categories的变化
watch(() => props.categories, (newCategories) => {
  categoryData.value = newCategories || []
  // 重置展开状态
  expandedCategories.value = []
  // 同步父分类数据
  parentCategoryData.value = JSON.parse(JSON.stringify(newCategories || []))
}, { deep: true })

// 所有分类（用于选择父分类）
const allCategories = computed(() => {
  const flatten = (categories) => {
    return categories.reduce((acc, category) => {
      acc.push(category)
      if (category.children && category.children.length) {
        acc = acc.concat(flatten(category.children))
      }
      return acc
    }, [])
  }
  return flatten(categoryData.value || [])
})

// 展平的分类列表（包含层级信息）
const flattenedCategories = computed(() => {
  const result = []
  
  const flatten = (categories, level = 0) => {
    if (!categories) return
    
    categories.forEach(category => {
      result.push({
        ...category,
        level: level
      })
      
      // 如果分类已展开且有子分类，则添加子分类
      if (expandedCategories.value.includes(category.categoryId) && category.children && category.children.length > 0) {
        flatten(category.children, level + 1)
      }
    })
  }
  
  if (categoryData.value) {
    flatten(categoryData.value)
  }
  
  return result
})

// 父分类选项（展平的分类列表）
const parentCategoryOptions = computed(() => {
  const result = []
  
  const flatten = (categories, level = 0) => {
    if (!categories) return
    
    categories.forEach(category => {
      result.push({
        ...category,
        level: level
      })
      
      // 如果分类已展开且有子分类，则添加子分类
      if (parentExpandedCategories.value.includes(category.categoryId) && category.children && category.children.length > 0) {
        flatten(category.children, level + 1)
      }
    })
  }
  
  if (parentCategoryData.value) {
    flatten(parentCategoryData.value)
  }
  
  return result
})

// 选中的父分类名称
const selectedParentCategoryName = computed(() => {
  if (categoryForm.value.parentId === 0) {
    return '顶级分类'
  }
  
  const findCategoryName = (categories) => {
    for (const category of categories) {
      if (category.categoryId === categoryForm.value.parentId) {
        return category.name
      }
      if (category.children && category.children.length) {
        const found = findCategoryName(category.children)
        if (found) return found
      }
    }
    return null
  }
  
  return findCategoryName(parentCategoryData.value) || '请选择'
})

// 修改部分：判断是否应该显示展开图标
const shouldShowExpandIcon = (category) => {
  // 如果正在加载，显示加载图标
  if (loadingCategories.value.includes(category.categoryId)) {
    return true;
  }
  
  // 如果已经展开，显示展开图标
  if (expandedCategories.value.includes(category.categoryId)) {
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

// 父分类选择器中判断是否应该显示展开图标
const shouldShowExpandIconForParent = (category) => {
  // 如果正在加载，显示加载图标
  if (parentLoadingCategories.value.includes(category.categoryId)) {
    return true;
  }
  
  // 如果已经展开，显示展开图标
  if (parentExpandedCategories.value.includes(category.categoryId)) {
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

// 切换分类展开状态
const toggleCategory = async (category) => {
  // 如果正在加载，不处理
  if (loadingCategories.value.includes(category.categoryId)) {
    return;
  }
  
  const categoryId = category.categoryId;
  
  // 如果已经展开，则收起
  if (expandedCategories.value.includes(categoryId)) {
    const index = expandedCategories.value.indexOf(categoryId);
    expandedCategories.value.splice(index, 1);
    return;
  }
  
  // 检查 hasChildren 字段，如果明确为 false，则不展开
  if (category.hasChildren === false) {
    return;
  }
  
  // 标记为正在加载
  loadingCategories.value.push(categoryId);
  
  try {
    const children = await categoryStore.fetchCategories(categoryId);
    
    // 移除加载状态
    const loadingIndex = loadingCategories.value.indexOf(categoryId);
    if (loadingIndex > -1) {
      loadingCategories.value.splice(loadingIndex, 1);
    }
    
    // 使用深度拷贝确保响应式更新
    const updatedCategories = JSON.parse(JSON.stringify(categoryData.value));
    
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
      categoryData.value = updatedCategories;
    }
    
    // 只有当确实有子分类时才展开
    if (children && children.length > 0) {
      expandedCategories.value.push(categoryId);
    }
  } catch (error) {
    const loadingIndex = loadingCategories.value.indexOf(categoryId);
    if (loadingIndex > -1) {
      loadingCategories.value.splice(loadingIndex, 1);
    }
    ElMessage.error('获取子分类失败');
    console.error('获取子分类失败:', error);
  }
}

// 父分类选择器中切换分类展开状态
const toggleParentCategory = async (category) => {
  // 如果正在加载，不处理
  if (parentLoadingCategories.value.includes(category.categoryId)) {
    return;
  }
  
  const categoryId = category.categoryId;
  
  // 如果已经展开，则收起
  if (parentExpandedCategories.value.includes(categoryId)) {
    const index = parentExpandedCategories.value.indexOf(categoryId);
    parentExpandedCategories.value.splice(index, 1);
    return;
  }
  
  // 检查 hasChildren 字段，如果明确为 false，则不展开
  if (category.hasChildren === false) {
    return;
  }
  
  // 标记为正在加载
  parentLoadingCategories.value.push(categoryId);
  
  try {
    const children = await categoryStore.fetchCategories(categoryId);
    
    // 移除加载状态
    const loadingIndex = parentLoadingCategories.value.indexOf(categoryId);
    if (loadingIndex > -1) {
      parentLoadingCategories.value.splice(loadingIndex, 1);
    }
    
    // 使用深度拷贝确保响应式更新
    const updatedCategories = JSON.parse(JSON.stringify(parentCategoryData.value));
    
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
      parentCategoryData.value = updatedCategories;
    }
    
    // 只有当确实有子分类时才展开
    if (children && children.length > 0) {
      parentExpandedCategories.value.push(categoryId);
    }
  } catch (error) {
    const loadingIndex = parentLoadingCategories.value.indexOf(categoryId);
    if (loadingIndex > -1) {
      parentLoadingCategories.value.splice(loadingIndex, 1);
    }
    ElMessage.error('获取子分类失败');
    console.error('获取子分类失败:', error);
  }
}

// 切换父分类选择器显示状态
const toggleParentCategorySelector = async () => {
  showParentCategoryTree.value = !showParentCategoryTree.value;
  
  // 第一次点击时加载顶层分类
  if (isParentCategoryFirstLoad.value) {
    try {
      const topLevelCategories = await categoryStore.fetchCategories(0);
      
      // 更新父分类数据
      parentCategoryData.value = topLevelCategories || [];
      
      // 标记已加载过
      isParentCategoryFirstLoad.value = false;
    } catch (error) {
      ElMessage.error('获取分类失败');
      console.error('获取分类失败:', error);
    }
  }
}

// 选择父分类
const selectParentCategory = (category) => {
  categoryForm.value.parentId = category.categoryId;
  showParentCategoryTree.value = false;
}

// 处理分类选择
const handleSelect = (categoryId) => {
  emit('select', categoryId)
  // 直接触发显示该分类下的商品
  emit('show-products', categoryId)
}

// 显示添加分类对话框
const showAddCategoryDialog = () => {
  categoryForm.value = {
    categoryId: null,
    name: '',
    parentId: 0,
    sort: 1
  }
  categoryDialogTitle.value = '添加分类'
  categoryDialogVisible.value = true
  
  // 重置父分类选择器状态
  isParentCategoryFirstLoad.value = true
  parentCategoryData.value = []
  parentExpandedCategories.value = []
}

// 显示添加子分类对话框
const showAddSubCategoryDialog = (parentCategory) => {
  categoryForm.value = {
    categoryId: null,
    name: '',
    parentId: parentCategory.categoryId,
    sort: 1
  }
  categoryDialogTitle.value = '添加子分类'
  categoryDialogVisible.value = true
}

// 显示编辑分类对话框
const showEditCategoryDialog = (category) => {
  categoryForm.value = {
    categoryId: category.categoryId,
    name: category.name,
    parentId: category.parentId || 0,
    sort: category.sort || 1
  }
  categoryDialogTitle.value = '编辑分类'
  categoryDialogVisible.value = true
}

// 保存分类
const saveCategory = async () => {
  try {
    if (categoryForm.value.categoryId) {
      await categoryStore.updateCategory(categoryForm.value)
      ElMessage.success('分类更新成功')
    } else {
      await categoryStore.addCategory(categoryForm.value)
      ElMessage.success('分类添加成功')
    }
    categoryDialogVisible.value = false
    // 刷新分类数据
    await refreshCategories()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 删除分类
const deleteCategory = (categoryId) => {
  ElMessageBox.confirm('确定删除此分类吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await categoryStore.deleteCategory(categoryId)
      ElMessage.success('分类删除成功')
      // 刷新分类数据
      await refreshCategories()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 新增：刷新分类数据并向父组件发出事件
const refreshCategories = async () => {
  const newCategories = await categoryStore.fetchCategories()
  emit('refresh-categories', newCategories)
}

// 点击其他地方关闭父分类选择器
const handleClickOutside = (e) => {
  if (!e.target.closest('.custom-tree-select')) {
    showParentCategoryTree.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

// 清理事件监听器
// eslint-disable-next-line no-unused-vars
const beforeUnmount = () => {
  document.removeEventListener('click', handleClickOutside);
};
</script>

<style scoped>
.category-menu {
  position: relative;
  width: 250px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  flex-shrink: 0;
}

.category-list {
  border-right: none;
  flex: 1;
  overflow-y: auto;
}

.el-menu-item {
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

.actions {
  display: flex;
  gap: 5px;
}

/* 自定义树形选择器样式 */
.custom-tree-select {
  position: relative;
  width: 100%;
}

.tree-select-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  background: #fff;
}

.tree-select-trigger:hover {
  border-color: #409eff;
}

.arrow-icon {
  transition: transform 0.2s;
}

.tree-select-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 5px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.tree-select-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
}

.tree-select-option:hover {
  background-color: #f5f7fa;
}

.tree-select-option.has-children {
  font-weight: 500;
}
</style>