<template>
  <div class="auth-container">
    <div class="auth-form">
      <h1 class="title">无名商城</h1>
      
      <el-form :model="form" :rules="rules" ref="loginForm">
        <el-form-item prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-button 
          type="primary" 
          @click="submitLogin"
          :loading="loading"
          class="submit-btn"
        >
          登录
        </el-button>
      </el-form>
      
      <div class="auth-footer">
        <span @click="$router.push('/register')">没有账号？立即注册</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const { handleLogin, loading, error } = useAuth()

const form = ref({
  email: '',
  password: ''
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const submitLogin = async () => {
  try {
    await handleLogin(form.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (err) {
    ElMessage.error(error.value)
  }
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/auth.scss';
</style>