# 来华留学生招生系统 (Admission System for International Students)

基于 Spring Boot + Vue 3 的前后端分离招生管理系统。

## 技术栈

### 后端
- **框架**: Spring Boot 3.5.0
- **ORM**: MyBatis-Plus 3.5.7
- **数据库**: MySQL 8.0
- **安全**: Spring Security
- **构建工具**: Maven

### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios

## 功能特性

1.  **用户注册与登录**
    - 学生账号注册
    - 邮箱/密码登录
    - 登录状态保持与路由守卫
2.  **招生项目浏览**
    - 分页展示招生项目列表
    - 项目详情查看（基础）
    - 无需登录即可浏览（后端接口支持，前端做了路由拦截演示）

## 快速开始

### 1. 环境准备
- JDK 17+ (推荐 JDK 21)
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
1.  创建数据库 `laihua_student`（如果未自动创建）。
2.  修改 `app-springboot/src/main/resources/application.yml` 中的数据库连接信息：
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/laihua_student?...
        username: your_username
        password: your_password
    ```
3.  系统启动时会自动执行 `schema.sql` 和 `data.sql` 初始化表结构和数据。

### 3. 启动后端
```bash
cd app-springboot
mvn spring-boot:run
```
后端服务将启动在 `http://localhost:8081`。

### 4. 访问系统
由于前端已打包集成到后端，直接访问后端地址即可：
- 打开浏览器访问：`http://localhost:8081`

### 5. 前端开发（可选）
如果需要修改前端代码：
```bash
cd frontend
npm install
npm run dev
```
前端开发服务器将启动在 `http://localhost:5173`。

## 项目结构
```
app-springboot2/
├── app-springboot/       # 后端项目源码
│   ├── src/main/java     # Java 源代码
│   ├── src/main/resources
│   │   ├── static/       # 前端打包产物 (dist)
│   │   ├── schema.sql    # 数据库初始化脚本
│   │   └── data.sql      # 测试数据脚本
│   └── pom.xml           # Maven 依赖配置
├── frontend/             # 前端项目源码
│   ├── src/              # Vue 组件与逻辑
│   ├── dist/             # 构建产物
│   └── vite.config.js    # Vite 配置
└── README.md             # 项目说明文档
```
