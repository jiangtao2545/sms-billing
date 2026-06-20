<template>
  <div>
    <el-card>
      <div slot="header">发送记录</div>
      <el-form inline>
        <el-form-item label="应用ID">
          <el-input v-model="query.appId" placeholder="应用ID" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="query.phone" placeholder="手机号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="提交失败" :value="0" />
            <el-option label="已提交" :value="1" />
            <el-option label="发送成功" :value="2" />
            <el-option label="发送失败" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="query.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="开始时间" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="query.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="结束时间" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleExport">导出Excel</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="appId" label="应用ID" width="240" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="charCount" label="字符数" width="80" />
        <el-table-column prop="billingCount" label="计费条" width="80" />
        <el-table-column prop="price" label="单价" width="80" />
        <el-table-column prop="totalFee" label="费用" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="statusTagType(scope.row.status)" size="small">
              {{ statusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发送时间" width="160" />
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
  </div>
</template>

<script>
import { getRecordList, exportRecord } from '@/api/record'
export default {
  name: 'RecordList',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      query: { appId: '', phone: '', status: null, startTime: '', endTime: '' }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize, ...this.query }
        Object.keys(params).forEach(k => { if (!params[k] && params[k] !== 0) delete params[k] })
        const res = await getRecordList(params)
        if (res.code === 200) {
          this.tableData = res.data.records
          this.total = res.data.total
        }
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageNum = 1
      this.loadData()
    },
    statusText(status) {
      const map = { 0: '提交失败', 1: '已提交', 2: '发送成功', 3: '发送失败' }
      return map[status] || '未知'
    },
    statusTagType(status) {
      const map = { 0: 'danger', 1: 'warning', 2: 'success', 3: 'danger' }
      return map[status] || 'info'
    },
    handlePageChange(page) {
      this.pageNum = page
      this.loadData()
    },
    async handleExport() {
      const params = { ...this.query }
      Object.keys(params).forEach(k => { if (!params[k] && params[k] !== 0) delete params[k] })
      const res = await exportRecord(params)
      const url = URL.createObjectURL(new Blob([res]))
      const a = document.createElement('a')
      a.href = url
      a.download = 'sms_records.xlsx'
      a.click()
      URL.revokeObjectURL(url)
    }
  }
}
</script>
