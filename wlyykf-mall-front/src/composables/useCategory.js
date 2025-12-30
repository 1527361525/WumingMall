import { ref } from 'vue'
import { useCategoryStore } from '@/stores/category.store'

export const useCategory = () => {
  const categoryStore = useCategoryStore()
  const activeCategory = ref(null)
  
  // 加载分类
  const loadCategories = async (parentId = 0) => {
    await categoryStore.fetchCategories(parentId)
  }
  
  // 处理分类点击
  const handleCategoryClick = (category) => {
    activeCategory.value = category
    loadCategories(category.categoryId)
  }
  
  return {
    categories: categoryStore.categories,
    loading: categoryStore.loading,
    error: categoryStore.error,
    activeCategory,
    loadCategories,
    handleCategoryClick
  }
}