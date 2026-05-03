<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: #304156;">
      <div class="logo">SMS计费系统</div>
      <el-menu
        :default-active="$route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/stat">
          <i class="el-icon-data-line"></i>
          <span>统计报表</span>
        </el-menu-item>
        <el-menu-item index="/app">
          <i class="el-icon-setting"></i>
          <span>应用管理</span>
        </el-menu-item>
        <el-menu-item index="/sms">
          <i class="el-icon-message"></i>
          <span>发送短信</span>
        </el-menu-item>
        <el-menu-item index="/record">
          <i class="el-icon-document"></i>
          <span>发送记录</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <i class="el-icon-user"></i>
          <span>个人中心</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff; border-bottom:1px solid #e6e6e6; display:flex; align-items:center; justify-content:flex-end;">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link" style="cursor:pointer;">
            {{ nickname }}<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </el-header>
      <el-main style="background:#f0f2f5; padding:20px;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { logout } from '@/api/auth'
export default {
  name: 'Layout',
  computed: {
    nickname() {
      const info = this.$store.state.userInfo
      return info ? (info.nickname || info.username) : '用户'
    }
  },
  methods: {
    async handleCommand(cmd) {
      if (cmd === 'logout') {
        await logout().catch(() => {})
        this.$store.dispatch('logout')
        this.$router.push('/login')
      } else if (cmd === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style scoped>
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #3a4a5c;
}
</style>
