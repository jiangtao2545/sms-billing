# SMS计费系统

## 项目介绍

SMS计费系统是一个完整的短信发送计费管理平台，提供短信应用管理、发送记录查询、统计报表等功能。

## 技术栈

### 后端
- Java 1.8
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3
- MySQL 8.0
- JWT认证
- Apache POI (Excel导出)

### 前端
- Vue 2.6
- Element UI 2.15
- Vue Router 3
- Vuex 3
- ECharts 5
- Axios

## 功能模块

- **登录认证**: JWT Token认证，BCrypt密码加密
- **应用管理**: 创建/编辑/删除应用，重置AppKey，启用/禁用
- **短信发送**: 单条发送、批量发送，实时费用预估
- **发送记录**: 分页查询，多条件筛选，Excel导出
- **统计报表**: 按应用/按时间维度统计，ECharts图表
- **个人中心**: 修改密码，登录日志

## 快速开始

### 数据库初始化
```sql
-- 执行 doc/sms_billing.sql
```

### 后端启动
```bash
cd backend
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run serve
```

### 默认账号
- 用户名: admin
- 密码: 123456
