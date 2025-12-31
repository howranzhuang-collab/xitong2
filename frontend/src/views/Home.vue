<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <el-header class="nav-header">
      <div class="logo">来华留学生招生系统</div>
      <div class="nav-right">
        <template v-if="userInfo">
          <span class="welcome-text">欢迎, {{ userInfo.name || userInfo.username }}</span>
          <el-button type="danger" size="small" @click="logout">退出登录</el-button>
        </template>
        <template v-else>
          <el-button type="primary" size="small" @click="handleLoginRedirect">登录</el-button>
          <el-button size="small" @click="handleRegisterRedirect">注册</el-button>
        </template>
      </div>
    </el-header>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="banner">
        <h2>欢迎申请我们的项目</h2>
        <p>开启您的留学之旅</p>
      </div>

      <el-card class="project-card">
        <template #header>
          <div class="card-header">
            <span>招生项目列表</span>
          </div>
        </template>
        <el-table :data="projectList" style="width: 100%" v-loading="loading" stripe>
          <el-table-column prop="name" label="项目名称" min-width="200">
            <template #default="scope">
              <span class="project-name">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="year" label="招生年份" width="120" align="center">
            <template #default="scope">
              <el-tag>{{ scope.row.year }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyDeadline" label="申请截止日期" width="180" align="center">
            <template #default="scope">
              <i class="el-icon-time"></i>
              {{ formatDate(scope.row.applyDeadline) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="项目描述" min-width="300" show-overflow-tooltip />
          <el-table-column label="操作" width="150" align="center" fixed="right">
            <template #default="scope">
              <el-button size="small" type="primary" plain @click="viewDetail(scope.row)">查看详情</el-button>
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
            background
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const projectList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userInfo = ref(null)

const checkLoginStatus = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      userInfo.value = JSON.parse(userStr)
    } catch (e) {
      console.error('解析用户信息失败', e)
      localStorage.removeItem('user')
    }
  }
}

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
  return date.toLocaleDateString()
}

const viewDetail = (row) => {
  ElMessageBox.alert(
    `<div>
      <p><strong>项目名称:</strong> ${row.name}</p>
      <p><strong>招生年份:</strong> ${row.year}</p>
      <p><strong>截止日期:</strong> ${formatDate(row.applyDeadline)}</p>
      <p><strong>描述:</strong> ${row.description || '暂无描述'}</p>
    </div>`,
    '项目详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

const logout = () => {
  localStorage.removeItem('user')
  userInfo.value = null
  ElMessage.success('已退出登录')
  // 强制刷新并跳转到后端登录页
  window.location.href = '/login'
}

const handleLoginRedirect = () => {
  window.location.href = '/login'
}

const handleRegisterRedirect = () => {
  window.location.href = '/register'
}

onMounted(() => {
  checkLoginStatus()
  getProjects()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.nav-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 40px;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.welcome-text {
  color: #606266;
  font-size: 14px;
}

.main-content {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
}

.banner {
  background: linear-gradient(135deg, #409EFF 0%, #36d1dc 100%);
  color: white;
  padding: 40px;
  border-radius: 8px;
  margin-bottom: 30px;
  text-align: center;
}

.banner h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
}

.banner p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.project-card {
  border-radius: 8px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.project-name {
  font-weight: 500;
  color: #303133;
}

.pagination {
  margin-top: 25px;
  display: flex;
  justify-content: center;
}
</style>
