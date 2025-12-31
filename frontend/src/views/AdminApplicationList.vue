<template>
  <div class="admin-app-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>申请记录列表</span>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%" v-loading="loading">
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="projectName" label="申请项目" min-width="180" />
        <el-table-column prop="documents" label="申请材料" min-width="200">
           <template #default="scope">
             {{ scope.row.documents }}
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">通过</el-tag>
            <el-tag v-else-if="scope.row.status === 2" type="danger">拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
      </el-table>

      <div class="pagination-block">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/application/list', {
      params: {
        pageIndex: currentPage.value,
        pageSize: pageSize.value
      }
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    } else {
      ElMessage.error(res.data.msg || '获取数据失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.admin-app-container {
  padding: 20px;
}
.pagination-block {
  margin-top: 20px;
  text-align: right;
}
</style>
