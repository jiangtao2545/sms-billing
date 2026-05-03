<template>
  <div>
    <el-row :gutter="20" style="margin-bottom:20px;">
      <el-col :span="12">
        <el-card>
          <div slot="header">按应用统计</div>
          <div ref="appChart" style="height:300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">
            按时间统计
            <el-radio-group v-model="timeType" size="mini" style="margin-left:10px;" @change="loadTimeChart">
              <el-radio-button label="day">日</el-radio-button>
              <el-radio-button label="month">月</el-radio-button>
              <el-radio-button label="year">年</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="timeChart" style="height:300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <div slot="header">应用统计详情</div>
      <el-table :data="appStatData" border>
        <el-table-column prop="app_id" label="应用ID" />
        <el-table-column prop="total_count" label="总发送数" />
        <el-table-column prop="success_count" label="成功数" />
        <el-table-column prop="total_fee" label="总费用" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { statByApp, statByTime } from '@/api/stat'
export default {
  name: 'StatReport',
  data() {
    return {
      timeType: 'day',
      appStatData: [],
      appChart: null,
      timeChart: null
    }
  },
  mounted() {
    this.loadAppChart()
    this.loadTimeChart()
  },
  methods: {
    async loadAppChart() {
      const res = await statByApp()
      if (res.code === 200) {
        this.appStatData = res.data
        if (!this.appChart) {
          this.appChart = echarts.init(this.$refs.appChart)
        }
        this.appChart.setOption({
          tooltip: {},
          xAxis: { type: 'category', data: res.data.map(d => d.app_id ? d.app_id.substring(0, 8) + '...' : '') },
          yAxis: { type: 'value' },
          series: [{ name: '发送量', type: 'bar', data: res.data.map(d => d.total_count) }]
        })
      }
    },
    async loadTimeChart() {
      const res = await statByTime({ type: this.timeType })
      if (res.code === 200) {
        if (!this.timeChart) {
          this.timeChart = echarts.init(this.$refs.timeChart)
        }
        this.timeChart.setOption({
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: res.data.map(d => d.time_key) },
          yAxis: { type: 'value' },
          series: [{ name: '发送量', type: 'line', data: res.data.map(d => d.total_count) }]
        })
      }
    }
  }
}
</script>
