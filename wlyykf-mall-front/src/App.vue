<!-- src/App.vue -->
<template>
  <div class="app-container">
    <!-- 顶部导航栏 - 只在非认证页面隐藏 -->
    <header class="app-header" v-if="!isAuthPage">
      <div class="container">
        <div class="header-content">
          <router-link to="/" class="logo">
            <h1>无名商城</h1>
          </router-link>
          <div class="nav-wrapper">
            <nav class="main-nav">
              <router-link to="/" class="nav-item">首页</router-link>
              <router-link to="/cart" class="nav-item">购物车</router-link>
              <router-link to="/order" class="nav-item">我的订单</router-link>
              <!-- 只有管理员才显示订单管理和统计菜单 -->
              <template v-if="isAdmin">
                <router-link to="/order-admin" class="nav-item">订单管理</router-link>
                <router-link to="/statistic" class="nav-item">统计</router-link>
                <router-link to="/user" class="nav-item">用户管理</router-link>
              </template>
            </nav>
          </div>
          <nav class="user-nav">
            <template v-if="!authStore.token">
            </template>
            <template v-else>
              <el-dropdown>
                <span class="user-info">
                  <el-avatar :size="30" :src="userAvatar" />
                  <span>{{ userNickName }}</span>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="goToProfile">个人中心</el-dropdown-item>
                    <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </nav>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="app-main">
      <div class="container">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>

    <!-- 全局加载状态 -->
    <el-loading-fullscreen :loading="isLoading" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useUserStore } from '@/stores/user.store'
import { ElMessage, ElLoading } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const userStore = useUserStore()

// 判断当前是否是认证页面（登录/注册）
const isAuthPage = computed(() => {
  return ['Login', 'Register'].includes(route.name)
})

// 全局加载状态
const isLoading = ref(false)

// 计算属性：用户头像
const userAvatar = computed(() => {
  if (!userStore.user || !userStore.user.avatar) {
    // 返回默认头像路径
    return new URL('@/assets/images/default-avatar.png', import.meta.url).href
  }
  if (userStore.user.avatar.startsWith('http')) return userStore.user.avatar
  return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(userStore.user.avatar)}`
})

// 计算属性：用户昵称
const userNickName = computed(() => {
  return userStore.user?.nickName || '用户'
})

// 计算属性：是否为管理员（修改为使用userStore中的isAdmin）
const isAdmin = computed(() => {
  return userStore.isAdmin // 使用userStore中定义的isAdmin计算属性
})

// 监听路由变化显示加载状态
router.beforeEach(() => {
  isLoading.value = true
})

router.afterEach(() => {
  isLoading.value = false
})

// 退出登录
const handleLogout = async () => {
  try {
    await authStore.logout();
    userStore.resetUser(); // 使用自定义的重置方法
    ElMessage.success('已退出登录');
    router.push('/login');
  } catch (error) {
    ElMessage.error('退出登录失败: ' + error.message);
  }
}

// 前往个人中心
const goToProfile = () => {
  router.push('/profile')
}

// 获取用户信息的函数
const loadUserInfo = async () => {
  if (authStore.token) {
    try {
      await userStore.fetchCurrentUser()
      await userStore.checkIsAdmin() // 添加管理员权限检查
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
}

// 监听token变化，当token存在时获取用户信息
watch(() => authStore.token, (newToken) => {
  if (newToken) {
    loadUserInfo()
  }
}, { immediate: true })

// 在组件挂载时获取用户信息
onMounted(async () => {
  await loadUserInfo()
})
</script>

<style lang="scss">
@use '@/assets/styles/variables' as *;

.app-header {
  .header-content {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    height: 60px;
    gap: 20px;
  }
  
  .logo h1 {
    margin: 0;
    font-size: 24px;
    color: #333;
    flex-shrink: 0;
  }
  
  .nav-wrapper {
    flex: 1;
    display: flex;
    justify-content: flex-end;
  }
  
  .main-nav {
    display: flex;
    gap: 30px;
    
    .nav-item {
      text-decoration: none;
      color: #333;
      font-size: 16px;
      padding: 5px 10px;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:hover {
        background-color: #e9ecef;
      }
      
      &.router-link-exact-active {
        color: #409eff;
        font-weight: bold;
      }
    }
  }
  
  .user-nav {
    display: flex;
    align-items: center;
    flex-shrink: 0;
  }
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
</style>