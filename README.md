# 无名商城 (Wuming Mall)

一个基于 Spring Boot 的电商平台系统，提供完整的购物车、订单管理、商品管理、用户管理、数据分析及推荐系统等功能。

**作者：** 陈诗懿   
**学号：** 202330450262

## 📋 项目简介

无名商城是一个功能完整的电商平台系统，采用现代化的技术栈构建，支持用户注册登录、商品浏览、购物车管理、订单处理、数据统计等核心电商功能。

**v2.0 新增功能**：系统新增了大数据分析、智能推荐、数据可视化等模块，支持三类用户角色（普通用户/销售人员/管理者），提供用户行为分析、销售趋势预测、个性化推荐等功能。

注：发送验证码和发送发货信息这两个邮件发送服务需要用户使用真实的邮箱账号，如果要体验以上功能请使用有效邮箱账号自己注册一个账号体验。

## 在线体验

[无名商城](http://8.138.240.32:80)

测试账号附在实验报告里

## 🏗️ 技术栈

### 后端技术栈
- **框架**: Spring Boot
- **ORM**: MyBatis-Plus
- **数据库**: MySQL
- **缓存**: Redis
- **安全**: JWT Token 认证
- **构建工具**: Maven
- **邮件服务**: Spring Mail
- **数据分析**: 基于 SQL 聚合查询的统计分析
- **推荐算法**: 协同过滤推荐算法

### 前端技术栈
- **框架**: Vue 3
- **图表库**: ECharts
- **UI组件**: 自定义组件
- **状态管理**: Vue Reactive

## 📁 项目结构

### 后端项目结构 (wlyykf-mall-backend)

```
src/main/java/com/wlyykf/mall/
├── component/           # 组件类
│   └── RedisComponent.java
├── config/              # 配置类
│   ├── AppConfig.java
│   ├── GlobalExceptionHandler.java
│   ├── MybatisPlusConfig.java
│   ├── MyMetaObjectHandler.java
│   └── RedisConfig.java
├── constants/           # 常量定义
│   └── Constants.java
├── controller/          # 控制器层
│   ├── analysis/        # 数据分析控制器
│   │   └── AnalysisController.java
│   ├── recommend/       # 推荐系统控制器
│   │   └── RecommendController.java
│   ├── log/             # 日志管理控制器
│   │   └── LogController.java
│   ├── salesperson/     # 销售人员控制器
│   │   └── SalespersonController.java
│   ├── CartItemController.java    # 购物车管理
│   ├── CategoryController.java    # 分类管理
│   ├── FileController.java        # 文件上传下载
│   ├── OrderController.java      # 订单管理
│   ├── ProductController.java     # 商品管理
│   ├── StatisticController.java   # 数据统计
│   └── UserController.java        # 用户管理
├── dto/                # 数据传输对象
│   ├── analysis/        # 分析相关DTO
│   ├── recommend/       # 推荐相关DTO
│   ├── CartItemDTO.java
│   ├── CategoryDTO.java
│   ├── LoginDTO.java
│   ├── OrderAddDTO.java
│   ├── OrderBuyDTO.java
│   ├── OrderListDTO.java
│   ├── OrderUpdateDTO.java
│   ├── ProductListDTO.java
│   ├── ProductUpdateDTO.java
│   ├── RegisterDTO.java
│   ├── TokenUserInfoDTO.java
│   └── UserUpdateDTO.java
├── entity/             # 实体类
│   ├── BaseEntity.java
│   ├── CartItem.java
│   ├── Category.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Product.java
│   ├── User.java
│   ├── UserBrowseLog.java      # 用户浏览日志
│   └── OperationLog.java      # 操作日志
├── enums/              # 枚举类
│   ├── OrderStatusEnum.java
│   ├── OrderTypeEnum.java
│   ├── SortDirectionEnum.java
│   └── UserRoleEnum.java      # 用户角色枚举
├── exception/          # 异常处理
│   └── BusinessException.java
├── interceptor/        # 拦截器
│   ├── AuthInterceptor.java
│   ├── RoleInterceptor.java    # 角色拦截器
│   └── WebConfig.java
├── mappers/            # 数据访问层
│   ├── CartItemMapper.java
│   ├── CategoryMapper.java
│   ├── OrderItemMapper.java
│   ├── OrderMapper.java
│   ├── ProductMapper.java
│   ├── UserMapper.java
│   ├── UserBrowseLogMapper.java
│   └── OperationLogMapper.java
├── service/            # 服务层
│   ├── analysis/        # 数据分析服务
│   ├── recommend/       # 推荐系统服务
│   ├── log/             # 日志服务
│   ├── impl/           # 服务实现
│   └── *.java          # 服务接口
├── utils/              # 工具类
│   ├── CurrentUserUtil.java
│   ├── PasswordUtil.java
│   ├── RedisUtil.java
│   ├── StringUtil.java
│   └── IpUtil.java     # IP地址工具类
└── vo/                 # 视图对象
    ├── analysis/        # 分析相关VO
    ├── recommend/       # 推荐相关VO
    ├── CartItemVO.java
    ├── CategoryVO.java
    ├── OrderDetailVO.java
    ├── OrderItemVO.java
    ├── OrderVO.java
    ├── PageResultVO.java
    ├── ProductUpdateVO.java
    ├── ProductVO.java
    ├── ResponseVO.java
    └── UserVO.java
```

### 前端项目结构 (wlyykf-mall-front)

```
src/
├── views/              # 页面组件
│   ├── Home.vue        # 首页
│   ├── DashboardView.vue # 数据大屏
│   ├── ProductView.vue  # 商品详情
│   └── ...
├── components/         # 组件
│   ├── BaseChart.vue   # 基础图表组件
│   ├── LineChart.vue   # 折线图组件
│   ├── BarChart.vue    # 柱状图组件
│   └── PieChart.vue    # 饼图组件
├── router/             # 路由配置
├── store/              # 状态管理
└── utils/              # 工具函数
```

## 🚀 核心功能

### 用户管理与角色权限

- **三类用户角色**：普通用户、销售人员、管理者
- 用户注册、登录、登出（支持日志记录）
- 自动登录和 Token 管理
- 用户信息修改、密码修改
- 余额充值功能
- 角色权限验证（销售人员可管理商品，管理者可管理所有）

### 商品管理

- 商品增删改查
- 商品分类管理
- 商品图片上传
- 商品库存管理
- 商品销售数据统计

### 购物车管理

- 添加商品到购物车
- 修改购物车商品数量
- 删除购物车商品
- 清空购物车
- 计算选中商品总价

### 订单管理

- 创建订单（购物车下单和立即购买）
- 订单支付流程
- 订单状态管理（待支付、已支付、已发货等）
- 订单发货和邮件通知
- 订单分页查询
- 订单操作日志记录

### 数据分析与可视化

- **用户画像分析**
  - 用户地域分布统计
  - 用户购买力分层（低/中/高消费）
  - 用户商品偏好分析
  
- **销售趋势分析**
  - 销售趋势统计（日/周/月维度）
  - 商品销售趋势分析
  - 类别销售统计
  
- **销售异常监控**
  - 订单量/销售额波动检测
  - 异常阈值告警（默认30%波动）
  
- **数据可视化大屏**
  - 销售趋势图（折线图）
  - 商品销量排行榜（柱状图）
  - 订单状态分布图（饼图）
  - 用户地域分布图（饼图/柱状图）
  - 用户购买力分布图

### 智能推荐系统

- **简单推荐**："浏览过此商品的人也买了"
- **协同过滤推荐**：基于用户行为相似度的个性化推荐
- **冷启动处理**：新用户推荐热门商品

### 日志采集系统

- **登录/登出日志**：记录用户登录时间、IP地址
- **浏览行为日志**：记录用户浏览商品行为、停留时长
- **操作日志**：记录销售人员和管理者的操作行为（AOP实现）

### 销售人员管理

- 管理者添加/删除销售人员账号
- 销售人员密码重置
- 销售人员销售数据面板
- 销售人员权限控制（可管理商品，不能管理人员）

### 文件管理

- 图片上传和存储
- 静态资源访问

## 📡 API 接口

### 认证相关

- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `POST /user/logout` - 用户登出
- `POST /user/sendCode` - 发送验证码

### 用户管理

- `GET /user/info` - 获取用户信息
- `PUT /user/update` - 修改用户信息
- `POST /user/changePassword` - 修改密码
- `POST /user/recharge` - 余额充值
- `POST /user/addSalesPerson` - 添加销售人员（管理者）
- `DELETE /user/salesPerson/{userId}` - 删除销售人员（管理者）
- `POST /user/resetPassword/{userId}` - 重置密码（管理者）
- `GET /user/getSalesPersonList` - 获取销售人员列表（管理者）

### 商品相关

- `GET /product/getProductList` - 获取商品列表
- `GET /product/{productId}` - 获取商品详情
- `POST /product` - 新增商品（管理员/销售人员）
- `PUT /product` - 修改商品（管理员/销售人员）
- `DELETE /product/{productId}` - 删除商品（管理员/销售人员）

### 购物车相关

- `POST /cartItem` - 添加商品到购物车
- `PUT /cartItem` - 修改购物车商品
- `GET /cartItem/getAll` - 获取购物车列表
- `GET /cartItem/totalPrice` - 计算总价
- `DELETE /cartItem/{cartItemId}` - 删除购物车商品
- `DELETE /cartItem/clear` - 清空购物车

### 订单相关

- `POST /order` - 创建订单
- `POST /order/submitOrder` - 提交订单
- `POST /order/pay` - 支付订单
- `GET /order/{orderId}` - 获取订单详情
- `GET /order/list` - 获取订单列表
- `POST /order/cancel` - 取消订单
- `POST /order/ship` - 订单发货

### 数据分析

- `GET /analysis/user/regionDistribution` - 用户地域分布统计
- `GET /analysis/user/purchasingPower` - 用户购买力分层
- `GET /analysis/user/preference/{userId}` - 用户商品偏好
- `GET /analysis/sales/trend` - 销售趋势统计（日/周/月）
- `GET /analysis/product/trend/{productId}` - 商品销售趋势
- `GET /analysis/category/sales` - 类别销售统计
- `GET /analysis/abnormal/sales` - 销售异常检测
- `GET /analysis/abnormal/realtime` - 实时监控数据

### 推荐系统

- `GET /recommend/simple/{productId}` - 简单推荐（看过此商品的人还买了）
- `GET /recommend/collaborative/{userId}` - 协同过滤推荐

### 日志管理

- `POST /log/browse` - 记录用户浏览行为

### 销售人员面板

- `GET /salesperson/dashboard` - 销售人员数据面板

### 数据统计（原有）

- `GET /statistic/getTotalAmount` - 获取销售额统计
- `GET /statistic/getProductTopN` - 获取商品销量排行
- `GET /statistic/getAllTypeOrderCount` - 获取订单状态统计

## 📊 数据库设计

### 核心业务表

- `tb_user` - 用户表（新增 role 字段：0-普通用户，1-销售人员，2-管理者）
- `tb_product` - 商品表
- `tb_category` - 分类表
- `tb_cart_item` - 购物车表
- `tb_order` - 订单表
- `tb_order_item` - 订单商品表

### 日志数据表

- `tb_user_browse_log` - 用户浏览日志表
  - 记录用户浏览商品的行为
  - 字段：log_id, user_id, product_id, category_id, browse_time, stay_duration, ip_address
  
- `tb_operation_log` - 操作日志表
  - 记录用户登录/登出和管理操作
  - 字段：log_id, user_id, user_type, operation_type, operation_content, operation_time, ip_address

### 数据库表结构示例

```sql
-- 用户表（新增 role 字段）
CREATE TABLE tb_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-销售人员，2-管理者',
    balance DECIMAL(10,2) DEFAULT 0,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    del_flag TINYINT DEFAULT 0
);

-- 用户浏览日志表
CREATE TABLE tb_user_browse_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '用户ID（未登录为null）',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    category_id BIGINT COMMENT '商品分类ID',
    browse_time DATETIME NOT NULL COMMENT '浏览时间',
    stay_duration INT DEFAULT 0 COMMENT '停留时长（秒）',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记 0-未删除 1-已删除'
);

-- 操作日志表
CREATE TABLE tb_operation_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_type TINYINT DEFAULT 0 COMMENT '用户类型 0-用户 1-销售人员 2-管理者',
    operation_type VARCHAR(50) COMMENT '操作类型：LOGIN-登录, LOGOUT-登出, PRODUCT_ADD-添加商品, etc.',
    operation_content VARCHAR(500) COMMENT '操作内容',
    operation_time DATETIME NOT NULL COMMENT '操作时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME NOT NULL COMMENT '更新时间',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标记 0-未删除 1-已删除'
);
```

## 🛠️ 开发说明

### 自定义异常处理

系统使用统一的异常处理机制，通过 `GlobalExceptionHandler` 处理业务异常和系统异常。

### 分页查询

所有列表查询接口都支持分页，使用 MyBatis-Plus 的分页插件。

### 数据验证

使用 Spring Validation 进行参数校验，确保数据的完整性。

### 多角色权限控制

系统支持三类用户角色（普通用户/销售人员/管理者），通过 JWT Token 中的 role 字段进行权限验证。使用拦截器 `RoleInterceptor` 对特定接口进行角色权限校验。

### AOP 操作日志

使用 Spring AOP 切面编程实现操作日志记录，通过自定义注解 `@Log` 标记需要记录的方法，自动拦截并记录操作信息（操作类型、操作人、操作时间、IP地址、入参数据）。

### 异步日志记录

使用 Spring `@Async` 异步线程池记录日志，避免影响主业务接口响应性能。登录日志、浏览日志等操作采用异步方式写入数据库。

### 数据分析算法

- **用户地域分析**：通过 IP 地址解析（纯真IP库或在线解析服务）获取用户省份/城市信息
- **购买力分层**：基于用户历史订单总金额，采用固定阈值分档（低消费<100元、中消费100-500元、高消费>500元）
- **销售异常检测**：对比今日与昨日数据，检测波动是否超过阈值（默认30%）

### 推荐算法

- **简单推荐**：基于商品关联规则，查询"购买过此商品的用户还购买的其他商品"
- **协同过滤推荐**：使用 Jaccard 相似度计算用户间相似性（共同购买的商品数 / 两个用户购买商品的并集数）
- **冷启动处理**：新用户采用热门商品推荐

### 前端数据可视化

使用 ECharts 实现数据可视化大屏，包括：
- 折线图：销售趋势
- 柱状图：商品销量排行、类别销售统计
- 饼图：订单状态分布、用户地域分布、用户购买力分布

## 🔧 安装和运行

### 环境要求

- JDK 8+
- MySQL 5.7+
- Redis
- Maven
- Node.js 16+

### 后端运行

1. 安装 JDK 8+、MySQL 5.7+、Redis
2. 导入数据库脚本（位于 `docs/sql/` 目录）
3. 修改 `application.yml` 中的数据库和 Redis 配置
4. 运行 `MallApplication.java` 启动后端服务

### 前端运行

1. 进入 `wlyykf-mall-front` 目录
2. 执行 `npm install` 安装依赖
3. 执行 `npm run dev` 启动前端开发服务器

### 默认账号

- 管理者账号：admin / 123456
- 销售人员账号：sales1 / 123456
- 普通用户账号：user1 / 123456（或自行注册）

---

## 📝 版本历史

### v2.0 (2026-05-30)
- 新增大数据分析模块（用户画像、销售趋势、异常监控）
- 新增智能推荐系统（简单推荐、协同过滤推荐）
- 新增数据可视化大屏（ECharts 图表展示）
- 新增日志采集系统（登录日志、浏览日志、操作日志）
- 新增销售人员管理功能
- 支持三类用户角色（普通用户/销售人员/管理者）
- 优化权限控制系统

### v1.0 (2025-01-01)
- 初始版本发布
- 基础电商功能（用户、商品、购物车、订单、支付）
- 数据统计功能
