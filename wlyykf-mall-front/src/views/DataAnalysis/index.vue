<template>
  <div class="data-analysis-container">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <el-icon><DataAnalysis /></el-icon>
        <span>数据分析</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
        background-color="#ffffff"
        text-color="#606266"
        active-text-color="#409eff"
      >
        <el-menu-item index="/data-analysis/user-portrait">
          <el-icon><User /></el-icon>
          <span>用户画像</span>
        </el-menu-item>
        <el-menu-item index="/data-analysis/sales-trend">
          <el-icon><TrendCharts /></el-icon>
          <span>销售趋势分析</span>
        </el-menu-item>
        <el-menu-item index="/data-analysis/abnormal-monitor">
          <el-icon><Warning /></el-icon>
          <span>销售异常监控</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { DataAnalysis, User, TrendCharts, Warning } from '@element-plus/icons-vue'

const route = useRoute()

// 当前激活的菜单项
const activeMenu = computed(() => route.path)
</script>

<style lang="scss" scoped>
.data-analysis-container {
  display: flex;
  min-height: calc(100vh - 60px); // 减去顶部导航栏高度
  background-color: #f0f2f5;
}

.sidebar {
  width: 220px;
  background-color: #ffffff;
  flex-shrink: 0;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  
  .sidebar-header {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    color: #303133;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid #ebeef5;
    
    .el-icon {
      font-size: 24px;
    }
  }
  
  .sidebar-menu {
    border-right: none;
    
    .el-menu-item {
      height: 50px;
      line-height: 50px;
      
      &:hover {
        background-color: #f5f7fa !important;
      }
      
      &.is-active {
        background-color: #ecf5ff !important;
        border-right: 3px solid #409eff;
      }
      
      .el-icon {
        margin-right: 10px;
      }
    }
  }
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

// 页面切换动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
