<template>
  <div>
    <el-card>
      <div slot="header" style="display:flex; justify-content:space-between; align-items:center;">
        <span>应用管理</span>
        <el-button type="primary" size="small" @click="showCreate">新建应用</el-button>
      </div>
      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="appId" label="应用ID" width="260" />
        <el-table-column prop="appName" label="应用名称" />
        <el-table-column prop="appKey" label="AppKey" width="280" show-overflow-tooltip />
        <el-table-column prop="price" label="单价(元/条)" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="mini" type="warning" @click="handleResetKey(scope.row)">重置Key</el-button>
            <el-button size="mini" :type="scope.row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(scope.row)">
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="text-align:right; margin-top:10px;">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page="pageNum"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="400px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="应用名称">
          <el-input v-model="form.appName" />
        </el-form-item>
        <el-form-item label="单价(元/条)">
          <el-input v-model="form.price" type="number" step="0.0001" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAppList, createApp, updateApp, toggleAppStatus, resetAppKey, deleteApp } from '@/api/app'
export default {
  name: 'AppList',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogTitle: '新建应用',
      form: { id: null, appName: '', price: '0.0500' },
      isEdit: false
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await getAppList({ pageNum: this.pageNum, pageSize: this.pageSize })
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    showCreate() {
      this.isEdit = false
      this.dialogTitle = '新建应用'
      this.form = { id: null, appName: '', price: '0.0500' }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.dialogTitle = '编辑应用'
      this.form = { id: row.id, appName: row.appName, price: row.price }
      this.dialogVisible = true
    },
    async handleSubmit() {
      if (this.isEdit) {
        await updateApp(this.form.id, { appName: this.form.appName, price: this.form.price })
      } else {
        await createApp({ appName: this.form.appName, price: this.form.price })
      }
      this.$message.success('操作成功')
      this.dialogVisible = false
      this.loadData()
    },
    async handleResetKey(row) {
      await this.$confirm('确认重置AppKey？', '提示', { type: 'warning' })
      const res = await resetAppKey(row.id)
      if (res.code === 200) {
        this.$message.success('AppKey已重置: ' + res.data)
        this.loadData()
      }
    },
    async handleToggleStatus(row) {
      const newStatus = row.status === 1 ? 0 : 1
      await toggleAppStatus(row.id, { status: newStatus })
      this.$message.success('操作成功')
      this.loadData()
    },
    async handleDelete(row) {
      await this.$confirm('确认删除该应用？', '提示', { type: 'warning' })
      const res = await deleteApp(row.id)
      if (res.code === 200) {
        this.$message.success('删除成功')
        this.loadData()
      } else {
        this.$message.error(res.msg)
      }
    },
    handlePageChange(page) {
      this.pageNum = page
      this.loadData()
    }
  }
}
</script>
