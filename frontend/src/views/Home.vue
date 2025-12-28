<template>
  <div class="home-container">
    <div class="header">
      <h1>招生项目列表</h1>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>

    <el-card class="project-card">
      <el-table :data="projectList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="项目名称" width="250" />
        <el-table-column prop="year" label="招生年份" width="100" />
        <el-table-column prop="applyDeadline" label="申请截止日期" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.applyDeadline) }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="项目描述" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const projectList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getProjects = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/project/list', {
      params: {
        pageIndex: currentPage.value,
        pageSize: pageSize.value
      }
    })
    if (res.data.code === 200) {
      projectList.value = res.data.data.records
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.data.msg || '获取项目列表失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (val) => {
  currentPage.value = val
  getProjects()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
}

const viewDetail = (row) => {
  ElMessage.info(`查看项目：${row.name}`)
}

const logout = () => {
  localStorage.removeItem('user')
  ElMessage.success('已退出登录')
  router.push('/login')
}

onMounted(() => {
  getProjects()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.project-card {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
