<!-- src/views/Profile.vue -->
<template>
  <div class="profile-container">
    <el-row :gutter="20" justify="center">
      <el-col :span="16">
        <el-card class="profile-card">
          <template #header>
            <div class="card-header">
              <span>个人资料</span>
            </div>
          </template>
          
          <el-form 
            :model="userForm" 
            :rules="rules" 
            ref="formRef" 
            label-width="100px"
            v-loading="loading"
          >
            <el-form-item label="头像">
              <div class="avatar-container">
                <el-avatar :size="80" :src="avatarUrl" />
                <el-upload
                  class="avatar-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                >
                  <el-button size="small" type="primary">上传头像</el-button>
                </el-upload>
              </div>
            </el-form-item>
            
            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="userForm.nickName" />
            </el-form-item>
            
            <el-form-item label="邮箱">
              <el-input v-model="userForm.email" disabled />
            </el-form-item>
            
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="userForm.phone" />
            </el-form-item>
            
            <el-form-item label="余额">
              <div class="money-container">
                <span class="money-text">¥{{ userForm.money }}</span>
                <el-button size="small" type="primary" @click="showRechargeDialog">充值</el-button>
              </div>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="submitForm" :loading="saving">保存</el-button>
              <el-button type="warning" @click="showChangePasswordDialog" style="margin-left: 10px;">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 充值对话框 -->
    <el-dialog 
      v-model="rechargeDialogVisible" 
      title="账户充值" 
      width="400px"
    >
      <el-form 
        :model="rechargeForm" 
        :rules="rechargeRules" 
        ref="rechargeFormRef"
      >
        <el-form-item label="充值金额" prop="amount">
          <el-input-number 
            v-model="rechargeForm.amount" 
            :min="0.01" 
            :precision="2" 
            :step="100"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmRecharge" 
          :loading="recharging"
        >
          确认充值
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog 
      v-model="changePasswordDialogVisible" 
      title="修改密码" 
      width="400px"
    >
      <el-form 
        :model="passwordForm" 
        :rules="passwordRules" 
        ref="passwordFormRef" 
        label-width="80px"
      >
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password 
          />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password 
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password 
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="changePasswordDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="changePassword" 
          :loading="changingPassword"
        >
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useUserStore } from '@/stores/user.store'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const formRef = ref(null)
const passwordFormRef = ref(null)
const rechargeFormRef = ref(null)
const saving = ref(false)
const changingPassword = ref(false)
const recharging = ref(false)
const rechargeDialogVisible = ref(false)
const changePasswordDialogVisible = ref(false)

// 表单数据
const userForm = reactive({
  userId: '',
  nickName: '',
  email: '',
  phone: '',
  avatar: '',
  money: 0
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rechargeForm = reactive({
  amount: 100
})

// 验证规则
const rules = {
  nickName: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '充值金额必须大于0', trigger: 'blur' }
  ]
}

// 加载状态
const loading = computed(() => userStore.loading)

// 头像URL
const avatarUrl = computed(() => {
  if (!userForm.avatar) {
    // 返回默认头像路径
    return new URL('@/assets/images/default-avatar.png', import.meta.url).href
  }
  if (userForm.avatar.startsWith('http')) return userForm.avatar
  return `${import.meta.env.VITE_API_BASE_URL}/file/getResource?sourceName=${encodeURIComponent(userForm.avatar)}`
})

// 上传相关配置
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/file/uploadImage`
const uploadHeaders = {
  'Authorization': `Bearer ${localStorage.getItem('token')}`
}

// 监听用户信息变化，更新表单
watch(() => userStore.user, (newUser) => {
  if (newUser) {
    userForm.userId = newUser.userId
    userForm.nickName = newUser.nickName
    userForm.email = newUser.email
    userForm.phone = newUser.phone
    userForm.avatar = newUser.avatar
    userForm.money = newUser.money
  }
}, { immediate: true })

// 处理头像上传成功
const handleAvatarSuccess = (response, uploadFile) => {
  if (response.code === 200) {
    userForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.info || '头像上传失败')
  }
}

// 上传前检查
const beforeAvatarUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像图片必须是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        saving.value = true
        await userStore.updateUser({
          userId: userForm.userId,
          nickName: userForm.nickName,
          phone: userForm.phone,
          avatar: userForm.avatar
        })
        ElMessage.success('保存成功')
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      } finally {
        saving.value = false
      }
    }
  })
}

// 显示修改密码对话框
const showChangePasswordDialog = () => {
  // 清空密码表单
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  changePasswordDialogVisible.value = true
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        changingPassword.value = true
        await userStore.updatePassword(
          passwordForm.oldPassword,
          passwordForm.newPassword
        )
        ElMessage.success('密码修改成功')
        changePasswordDialogVisible.value = false // 关闭对话框
        // 清空密码表单
        passwordForm.oldPassword = ''
        passwordForm.newPassword = ''
        passwordForm.confirmPassword = ''
      } catch (error) {
        ElMessage.error(error.message || '密码修改失败')
      } finally {
        changingPassword.value = false
      }
    }
  })
}

// 显示充值对话框
const showRechargeDialog = () => {
  rechargeForm.amount = 100
  rechargeDialogVisible.value = true
}

// 确认充值
const confirmRecharge = async () => {
  if (!rechargeFormRef.value) return
  
  await rechargeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        recharging.value = true
        const result = await userStore.recharge(rechargeForm.amount)
        userForm.money = result.data.money
        ElMessage.success('充值成功')
        rechargeDialogVisible.value = false
      } catch (error) {
        ElMessage.error(error.message || '充值失败')
      } finally {
        recharging.value = false
      }
    }
  })
}

// 获取用户信息
const loadUserInfo = async () => {
  try {
    await userStore.fetchCurrentUser()
  } catch (error) {
    ElMessage.error(error.message || '获取用户信息失败')
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.avatar-container {
  display: flex;
  align-items: center;
  gap: 20px;
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

.money-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.money-text {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.password-card {
  height: 100%;
}
</style>