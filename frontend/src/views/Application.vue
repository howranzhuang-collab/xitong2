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
            action="/api/common/upload"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :file-list="fileList"
            list-type="text">
            <el-button size="small" type="primary">点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png/pdf文件，且不超过5MB</div>
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
  projectId: '',
  documents: []
})

const rules = {
  projectId: [
    { required: true, message: '请选择招生项目', trigger: 'change' }
  ],
  documents: [
    { required: true, message: '请至少上传一份申请材料', trigger: 'change', type: 'array' }
  ]
}

const getProjectList = async () => {
  try {
    const res = await axios.get('/api/project/list', {
      params: { pageIndex: 1, pageSize: 100 }
    })
    if (res.data.code === 200) {
      projectList.value = res.data.data.records
      
      // 如果 URL 中有 projectId 参数，自动选中
      if (route.query.projectId) {
        // 确保 projectId 存在于列表中
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

const handleUploadSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    form.documents.push(response.data.url)
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

const handleRemove = (file, fileList) => {
  // 简单处理：如果是刚刚上传的，从 form.documents 移除
  // 这里需要根据 response 里的 url 来匹配，实际 fileList 里的 file 对象包含 response
  if (file.response && file.response.code === 200) {
    const url = file.response.data.url
    const index = form.documents.indexOf(url)
    if (index > -1) {
      form.documents.splice(index, 1)
    }
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await axios.post('/api/application/submit', {
          projectId: form.projectId,
          documents: form.documents
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
  // 检查登录
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
