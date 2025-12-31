<template>
  <div class="application-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>提交申请</span>
        </div>
      </template>
      <el-form :model="form" label-width="120px" ref="formRef" :rules="rules">
        <el-form-item label="选择项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择招生项目" style="width: 100%">
            <el-option
              v-for="item in projectList"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="申请材料" prop="documents">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleChange"
            :on-remove="handleRemove"
            :file-list="fileList"
            multiple
            list-type="text">
            <el-button size="small" type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">支持jpg/png/pdf文件，提交申请时自动上传</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm">提交申请</el-button>
          <el-button @click="$router.push('/')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const projectList = ref([])
const fileList = ref([])

const form = reactive({
  projectId: ''
})

const rules = {
  projectId: [
    { required: true, message: '请选择招生项目', trigger: 'change' }
  ]
}

const getProjectList = async () => {
  // ... (Keep existing logic)
  try {
    const res = await axios.get('/api/project/list', {
      params: { pageIndex: 1, pageSize: 100 }
    })
    if (res.data.code === 200) {
      projectList.value = res.data.data.records
      if (route.query.projectId) {
        const exists = projectList.value.some(p => p.id === route.query.projectId)
        if (exists) {
          form.projectId = route.query.projectId
        }
      }
    }
  } catch (error) {
    console.error('获取项目列表失败', error)
  }
}

const handleChange = (file, fileListRef) => {
  fileList.value = fileListRef
}

const handleRemove = (file, fileListRef) => {
  fileList.value = fileListRef
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const formData = new FormData()
        formData.append('projectId', form.projectId)
        
        // Append files
        fileList.value.forEach(item => {
           // el-upload raw file is in item.raw
           formData.append('files', item.raw)
        })

        const res = await axios.post('/api/application/submit', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
        
        if (res.data.code === 200) {
          ElMessage.success('申请已提交')
          setTimeout(() => {
            router.push('/')
          }, 1500)
        } else {
          ElMessage.error(res.data.msg || '提交失败')
        }
      } catch (error) {
        console.error(error)
        ElMessage.error('网络错误')
      }
    }
  })
}

onMounted(() => {
  const user = localStorage.getItem('user')
  if (!user) {
    window.location.href = '/login'
    return
  }
  getProjectList()
})
</script>

<style scoped>
.application-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 0 20px;
}
.card-header {
  font-weight: bold;
}
</style>
