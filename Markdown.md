来华留学生服务系统 - 实施计划（Trae AI 逐步指导）
此计划专为课程设计作业设计，采用极简技术栈：

后端：Spring Boot + MyBatis-Plus + MySQL
前端：Vue 3 + Vite + Element Plus
数据库：本地 MySQL

目标是先搭建一个最小可运行的基础系统（包含学生注册登录 + 招生项目浏览 + 简单个人信息查看），其他模块（如缴费、住宿、签证等）在后续迭代添加。
所有步骤都小而具体，每步包含明确的验证方式，不包含任何代码片段。
计划假设你在 Trae AI 界面中处理项目（例如，通过 Trae 生成提示、代码建议，或直接编辑文件），并使用本地 IDE（如 VS Code 或 IntelliJ）结合 Trae 的响应进行开发。
阶段 0：准备工作（Trae 环境检查）

打开项目并确认目录结构
在本地 IDE 中打开 jiaoshoujia 根目录
确认存在 app-springboot 子目录，且包含 pom.xml 和 src/main/java验证：在 IDE 文件浏览器中看到上述文件，或在终端运行 ls app-springboot 显示相关内容

启动 Spring Boot 项目（测试脚手架是否可用）
在 IDE 终端进入 app-springboot 目录
执行 Maven 命令启动应用
验证：日志显示 “Started Application in X seconds” 且无错误，浏览器访问 http://localhost:8080 返回 404 或默认错误页


阶段 1：后端基础环境搭建

添加核心依赖（MyBatis-Plus + MySQL 驱动）
在 IDE 中打开 app-springboot/pom.xml
添加必要的依赖项（MyBatis-Plus 启动器、MySQL Connector）
验证：保存后，运行 Maven 刷新命令，终端显示依赖树无报错

配置 application.yml（数据库连接）
在 src/main/resources 下创建或编辑 application.yml
设置 MySQL 连接（数据库名为 laihua_student，使用本地用户名root和密码root）
验证：启动应用，日志中无数据库连接错误

创建数据库和基础表
使用数据库工具（如 MySQL Workbench）创建数据库 laihua_student
创建 students 表（包含 id、student_no、passport_no、name、email、password、status 等字段）
验证：在工具中执行 SHOW TABLES; 查询，看到 students 表

生成 MyBatis-Plus 代码（Entity、Mapper、Service）
在 Trae 中输入提示生成配置类建议
根据建议生成 Student 实体、Mapper 接口、Service 接口及实现
验证：项目中出现相应文件，IDE 编译无错误


阶段 2：实现最基础的学生注册与登录

创建注册接口
创建 StudentController 文件
添加 POST /api/student/register 端点（处理姓名、邮箱、密码输入）
在 Service 层实现数据库插入
验证：用 Postman 发送 POST 请求，数据库中出现新记录

添加登录接口（基于 Session）
在 StudentController 添加 POST /api/student/login
实现邮箱密码校验，成功后存入 Session
验证：登录后请求需登录接口，返回当前用户信息

添加简单用户信息接口
添加 GET /api/student/info：从 Session 获取 ID 并查询数据库
验证：登录后用 Postman 请求，返回 JSON 包含姓名、邮箱

配置 Spring Security 简单拦截
创建 Security 配置类
放行注册和登录端点，其他需认证
验证：未登录访问 info 接口返回 401；登录后可访问


阶段 3：添加招生项目浏览功能

创建招生项目表和实体
在数据库添加 admission_projects 表（id、name、year、description、apply_deadline 等）
生成对应 Entity、Mapper、Service
验证：表结构正确，代码文件无编译错误

初始化几条测试数据
在数据库手动插入 3-5 条项目记录
验证：执行 SELECT 查询，看到数据

创建招生项目查询接口
在 Controller 添加 GET /api/project/list（支持简单分页）
返回项目列表（无需登录）
验证：Postman 请求返回 JSON 数组


阶段 4：搭建前端基础框架

初始化 Vue 3 项目
在根目录创建 frontend 文件夹
在终端进入 frontend，执行 Vue 创建命令
安装 Element Plus 和 Axios
验证：运行开发服务器，浏览器显示欢迎页

创建登录与注册页面
创建 src/views/Login.vue 和 Register.vue
实现表单调用后端注册和登录端点
登录成功跳转首页
验证：前端页面注册并登录，显示欢迎

创建首页展示招生项目
创建 src/views/Home.vue
用 Element Plus Table 显示项目列表
验证：登录后首页显示表格


阶段 5：前后端联调与简单优化

解决跨域问题
在后端添加跨域配置
验证：前端调用后端无跨域错误

添加简单路由守卫
在前端路由文件中设置守卫
验证：未登录访问首页跳转登录

打包前端并集成到后端
执行前端构建命令生成 dist
复制到后端 static 目录
启动后端访问根路径
验证：单机运行显示前端页面


阶段 6：最终检查与文档

完整流程测试
测试注册 → 登录 → 查看项目
验证：流程无报错，数据保存正确

编写 README.md
包含简介、技术栈、启动步骤
验证：内容清晰，按步骤可运行



模块1：招生申请功能（推荐优先扩展）
目标：学生登录后可提交申请（选择项目 + 上传材料）。

添加申请表
数据库创建 applications 表（id, student_id, project_id, apply_time, status, files_json）。
生成 Entity、Mapper、Service。
验证：表创建成功，代码编译通过。

创建申请接口（后端）
POST /api/application/submit（接收 projectId + 文件列表）。
Service 层：校验登录、插入申请记录。
验证：Postman 发送请求，数据库新增记录。

前端创建申请页面
新建 src/views/Application.vue（项目选择下拉 + 文件上传）。
使用 Element Plus el-upload 组件。
提交时调用后端接口。
验证：登录后访问 /application，提交成功提示“申请已提交”。

管理员查看申请列表
GET /api/application/list（admin 角色）。
前端新建 admin 视图显示表格。
验证：admin 登录后看到申请记录。

模块2：个人信息完善

扩展 students 表
添加字段：photo_path, phone, nationality, address 等。
验证：数据库字段更新，Entity 同步。

后端完善接口
PUT /api/student/profile（更新个人信息 + 上传头像）。
验证：Postman 更新成功，数据库变化。

前端个人信息页面
新建 src/views/Profile.vue（表单 + 头像上传）。
加载当前用户信息，提交更新。
验证：登录后访问 /profile，修改信息保存成功。

模块3：模拟缴费

添加费用表fees 表（id, student_id, fee_type, amount, due_date, status）。
验证：表创建，插入测试数据。

后端模拟支付接口
POST /api/fee/pay/{feeId}（更新 status 为 paid）。
返回“支付成功”模拟响应。
验证：Postman 调用，数据库 status 更新。

前端缴费页面
新建 src/views/Pay.vue（显示欠费列表 + 模拟支付按钮）。
点击支付调用后端接口，刷新列表。
验证：页面显示欠费，点击支付后状态变为“已缴”。

模块4：宿舍分配

添加宿舍表dormitories（id, building, room_no, capacity, available）。
dorm_assignments（id, student_id, dormitory_id, check_in_date）。
验证：表创建，插入测试宿舍。
后端分配接口
POST /api/dorm/assign（接收 student_id + dorm_id）。
更新 available 计数。
验证：Postman 调用，床位减少。
前端宿舍查询页面src/views/Dorm.vue（列表显示可用宿舍 + 申请按钮）。
验证：登录后显示宿舍，申请成功更新状态。

模块5：签证材料上传

添加签证记录表visa_records（id, student_id, status, files_json）。
验证：表创建。
后端上传接口
POST /api/visa/upload（多文件上传，存路径）。
验证：Postman 上传文件，数据库记录路径。
前端签证页面src/views/Visa.vue（上传 JW201/JW202 等材料）。
验证：上传成功，页面显示“待审核”。

模块6：学籍记录

添加学籍表student_status（id, student_id, status, major, entry_date, graduate_date）。
验证：表创建。
后端学籍查询接口
GET /api/student/status。
验证：返回当前学籍信息。
前端学籍页面src/views/Status.vue（显示入学日期、专业、状态）。
验证：页面显示个人信息。

模块7：校友登记

添加 alumni 表alumni（id, student_id, graduate_year, company, email）。
验证：表创建。
后端登记接口
POST /api/alumni/register（毕业生填写信息）。
验证：Postman 插入成功。
前端校友页面src/views/Alumni.vue（表单登记 + 查看校友列表）。
验证：登记成功，列表显示。

总体建议

顺序：按上面 1→7 顺序扩展，每完成一个模块后测试完整流程。
公共部分：每个模块完成后，更新路由（添加对应页面）、菜单导航。
角色区分：简单用 Session 判断角色（student/admin），后期可扩展。