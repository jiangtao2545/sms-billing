<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card title="单条发送">
          <div slot="header">单条发送</div>
          <el-form :model="singleForm" label-width="100px">
            <el-form-item label="选择应用">
              <el-select v-model="singleForm.appId" placeholder="请选择应用" @change="onAppChange">
                <el-option v-for="app in appList" :key="app.appId" :label="app.appName" :value="app.appId" />
              </el-select>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="singleForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="短信内容">
              <div v-if="currentSignature" style="margin-bottom:4px; color:#409EFF; font-size:12px;">
                签名: {{ currentSignature }}
                <span v-if="currentSmsType === 2" style="color:#E6A23C;"> | 营销短信，将自动追加退订信息</span>
              </div>
              <el-input v-model="singleForm.content" type="textarea" :rows="4" placeholder="请输入短信内容（不含签名）" @input="calcSingle" />
              <div style="color:#999; font-size:12px;">字符数: {{ singleCharCount }}, 计费条数: {{ singleBillingCount }}, 预计费用: {{ singleFee }}元</div>
              <div v-if="singleFullMsg" style="color:#909399; font-size:11px; margin-top:2px;">完整短信预览: {{ singleFullMsg }}</div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSingleSend" :loading="singleLoading">发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">批量发送</div>
          <el-form :model="batchForm" label-width="100px">
            <el-form-item label="选择应用">
              <el-select v-model="batchForm.appId" placeholder="请选择应用">
                <el-option v-for="app in appList" :key="app.appId" :label="app.appName" :value="app.appId" />
              </el-select>
            </el-form-item>
            <el-form-item label="手机号列表">
              <div style="margin-bottom:6px;">
                <el-upload
                  :show-file-list="false"
                  accept=".xlsx,.xls"
                  :http-request="handleExcelUpload"
                  :disabled="batchUploadLoading">
                  <el-button size="small" :loading="batchUploadLoading" icon="el-icon-upload2">
                    {{ batchUploadLoading ? '解析中...' : '上传Excel导入' }}
                  </el-button>
                </el-upload>
                <el-button size="small" type="text" icon="el-icon-download" @click="handleDownloadTemplate" style="margin-left:4px;">
                  下载模板
                </el-button>
                <span v-if="batchPhonesCount > 0" style="color:#67C23A; font-size:12px; margin-left:8px;">
                  已导入 {{ batchPhonesCount }} 个号码
                </span>
              </div>
              <el-input v-model="batchForm.phonesText" type="textarea" :rows="4" placeholder="每行一个手机号，或上传Excel导入" />
            </el-form-item>
            <el-form-item label="短信内容">
              <div v-if="currentSignature" style="margin-bottom:4px; color:#409EFF; font-size:12px;">
                签名: {{ currentSignature }}
                <span v-if="currentSmsType === 2" style="color:#E6A23C;"> | 营销短信，将自动追加退订信息</span>
              </div>
              <el-input v-model="batchForm.content" type="textarea" :rows="4" placeholder="请输入短信内容（不含签名）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleBatchSend" :loading="batchLoading">批量发送</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getAppList } from '@/api/app'
import request from '@/utils/request'
import { sendSms, batchSendSms, parseExcel } from '@/api/sms'
import { countChars, countBillingSegments, calcFee, buildFullMessage } from '@/utils/smsCount'
export default {
  name: 'SmsSend',
  data() {
    return {
      appList: [],
      singleForm: { appId: '', phone: '', content: '' },
      batchForm: { appId: '', phonesText: '', content: '' },
      singleCharCount: 0,
      singleBillingCount: 0,
      singleFee: '0.0000',
      singleFullMsg: '',
      currentPrice: 0.05,
      currentSignature: '',
      currentSmsType: 1,
      singleLoading: false,
      batchLoading: false,
      batchUploadLoading: false,
      batchPhonesCount: 0
    }
  },
  mounted() {
    this.loadApps()
  },
  methods: {
    async loadApps() {
      const res = await getAppList({ pageNum: 1, pageSize: 100 })
      if (res.code === 200) {
        this.appList = res.data.records.filter(a => a.status === 1)
      }
    },
    onAppChange(appId) {
      const app = this.appList.find(a => a.appId === appId)
      if (app) {
        this.currentPrice = parseFloat(app.price)
        this.currentSignature = app.signature || ''
        this.currentSmsType = app.smsType || 1
      } else {
        this.currentSignature = ''
        this.currentSmsType = 1
      }
      this.calcSingle()
    },
    calcSingle() {
      const fullMsg = buildFullMessage(this.singleForm.content, this.currentSignature, this.currentSmsType)
      this.singleFullMsg = fullMsg
      const chars = countChars(fullMsg)
      const segments = countBillingSegments(chars)
      this.singleCharCount = chars
      this.singleBillingCount = segments
      this.singleFee = calcFee(segments, this.currentPrice)
    },
    async handleSingleSend() {
      if (!this.singleForm.appId || !this.singleForm.phone || !this.singleForm.content) {
        return this.$message.warning('请填写完整信息')
      }
      this.singleLoading = true
      try {
        const res = await sendSms(this.singleForm)
        if (res.code === 200) {
          this.$message.success('发送成功')
        } else {
          this.$message.error(res.msg)
        }
      } finally {
        this.singleLoading = false
      }
    },
    async handleExcelUpload({ file }) {
      this.batchUploadLoading = true
      try {
        const res = await parseExcel(file)
        if (res.code === 200 && res.data && res.data.length > 0) {
          this.batchForm.phonesText = res.data.join('\n')
          this.batchPhonesCount = res.data.length
          this.$message.success(`成功导入 ${res.data.length} 个手机号`)
        } else {
          this.$message.error(res.msg || '未识别到有效手机号')
        }
      } catch {
        this.$message.error('文件上传失败')
      } finally {
        this.batchUploadLoading = false
      }
    },
    async handleDownloadTemplate() {
      try {
        const res = await request({ method: 'get', url: '/sms/downloadTemplate', responseType: 'blob' })
        const url = URL.createObjectURL(new Blob([res]))
        const a = document.createElement('a')
        a.href = url
        a.download = '手机号导入模板.xlsx'
        a.click()
        URL.revokeObjectURL(url)
      } catch {
        this.$message.error('下载失败')
      }
    },
    async handleBatchSend() {
      if (!this.batchForm.appId || !this.batchForm.phonesText || !this.batchForm.content) {
        return this.$message.warning('请填写完整信息')
      }
      const phones = this.batchForm.phonesText.split('\n').map(p => p.trim()).filter(p => p)
      this.batchLoading = true
      try {
        const res = await batchSendSms({ appId: this.batchForm.appId, phones, content: this.batchForm.content })
        if (res.code === 200) {
          this.$message.success(`批量发送成功，共${res.data.length}条`)
        } else {
          this.$message.error(res.msg)
        }
      } finally {
        this.batchLoading = false
      }
    }
  }
}
</script>
