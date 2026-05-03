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
              <el-input v-model="singleForm.content" type="textarea" :rows="4" placeholder="请输入短信内容" @input="calcSingle" />
              <div style="color:#999; font-size:12px;">字符数: {{ singleCharCount }}, 计费条数: {{ singleBillingCount }}, 预计费用: {{ singleFee }}元</div>
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
              <el-input v-model="batchForm.phonesText" type="textarea" :rows="4" placeholder="每行一个手机号" />
            </el-form-item>
            <el-form-item label="短信内容">
              <el-input v-model="batchForm.content" type="textarea" :rows="4" placeholder="请输入短信内容" />
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
import { sendSms, batchSendSms } from '@/api/sms'
import { countChars, countBillingSegments, calcFee } from '@/utils/smsCount'
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
      currentPrice: 0.05,
      singleLoading: false,
      batchLoading: false
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
      if (app) this.currentPrice = parseFloat(app.price)
      this.calcSingle()
    },
    calcSingle() {
      const chars = countChars(this.singleForm.content)
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
