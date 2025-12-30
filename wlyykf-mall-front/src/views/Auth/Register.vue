<template>
  <div class="auth-container">
    <div class="auth-form">
      <h1 class="title">无名商城</h1>
      
      <el-form :model="form" :rules="rules" ref="registerForm">
        <el-form-item prop="email">
          <el-input 
            v-model="form.email" 
            placeholder="请输入邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item prop="nickName">
          <el-input
            v-model="form.nickName"
            placeholder="请输入昵称"
            prefix-icon="User"
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
        
        <el-form-item prop="code">
          <div class="code-input">
            <el-input
              v-model="form.code"
              placeholder="请输入验证码"
              prefix-icon="Key"
            />
            <el-button 
              type="primary" 
              @click="sendVerificationCode"
              :disabled="!form.email || isSending"
              class="send-btn"
            >
              {{ codeBtnText }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-button 
          type="primary" 
          @click="submitRegister"
          :loading="loading"
          class="submit-btn"
        >
          注册
        </el-button>
      </el-form>
      
      <div class="auth-footer">
        <span @click="$router.push('/login')">已有账号？立即登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '@/composables/useAuth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const { handleRegister, handleSendCode, loading } = useAuth()

const form = ref({
  email: '',
  nickName: '',
  password: '',
  code: ''
})

const isSending = ref(false)
const countdown = ref(0)

const codeBtnText = computed(() => {
  return countdown.value > 0 
    ? `${countdown.value}s后重新获取` 
    : '获取验证码'
})

const rules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 12, message: '昵称长度在2到12个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为4位', trigger: 'blur' }
  ]
}

const sendVerificationCode = async () => {
  if (countdown.value > 0) return
  
  try {
    isSending.value = true
    await handleSendCode(form.value.email)
    ElMessage.success('验证码发送成功')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (err) {
    ElMessage.error('验证码发送失败')
  } finally {
    isSending.value = false
  }
}

const submitRegister = async () => {
  try {
    await handleRegister(form.value)
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (err) {
    ElMessage.error(err.message)
  }
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/auth.scss';
</style>