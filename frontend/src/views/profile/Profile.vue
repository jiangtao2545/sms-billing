<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div slot="header">个人信息</div>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ userInfo && userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ userInfo && userInfo.nickname }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header">修改密码</div>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdForm" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top:20px;">
      <div slot="header">登录日志</div>
      <el-table :data="loginLogs" border>
        <el-table-column prop="ip" label="IP" />
        <el-table-column prop="loginTime" label="登录时间" />
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userAgent" label="UA" show-overflow-tooltip />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { changePassword, getLoginLogs } from '@/api/auth'
export default {
  name: 'Profile',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.pwdForm.newPassword) {
        callback(new Error('两次密码不一致'))
      } else {
        callback()
      }
    }
    return {
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ required: true, min: 6, message: '新密码至少6位', trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validateConfirm, trigger: 'blur' }]
      },
      loginLogs: []
    }
  },
  computed: {
    userInfo() {
      return this.$store.state.userInfo
    }
  },
  mounted() {
    this.loadLogs()
  },
  methods: {
    async loadLogs() {
      const res = await getLoginLogs({ pageNum: 1, pageSize: 20 })
      if (res.code === 200) {
        this.loginLogs = res.data.records
      }
    },
    handleChangePwd() {
      this.$refs.pwdForm.validate(async valid => {
        if (!valid) return
        const res = await changePassword({
          oldPassword: this.pwdForm.oldPassword,
          newPassword: this.pwdForm.newPassword
        })
        if (res.code === 200) {
          this.$message.success('密码修改成功，请重新登录')
          this.$store.dispatch('logout')
          this.$router.push('/login')
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>
