<template>
  <div class="register-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>学生注册</span>
        </div>
      </template>
      <el-form :model="registerForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="registerForm.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <el-button @click="$router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerForm = ref({
  name: '',
  email: '',
  password: ''
})

const handleRegister = async () => {
  if (!registerForm.value.name || !registerForm.value.email || !registerForm.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    const formData = new FormData()
    formData.append('name', registerForm.value.name)
    formData.append('email', registerForm.value.email)
    formData.append('password', registerForm.value.password)

    const res = await axios.post('/api/student/register', formData)
    if (res.data.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.msg || '注册失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误')
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.box-card {
  width: 400px;
}
.card-header {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}
</style>
