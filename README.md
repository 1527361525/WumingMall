# 无名商城 (Wuming Mall)

一个基于 Spring Boot 的电商平台后端系统，提供完整的购物车、订单管理、商品管理、用户管理等功能。

**作者：** 陈诗懿   
**学号：** 202330450262

## 📋 项目简介

无名商城是一个功能完整的电商平台后端系统，采用现代化的技术栈构建，支持用户注册登录、商品浏览、购物车管理、订单处理、数据统计等核心电商功能。

注：发送验证码和发送发货信息这两个邮件发送服务需要用户使用真实的邮箱账号，如果要体验以上功能请使用有效邮箱账号自己注册一个账号体验。

## 在线体验

[无名商城](http://8.138.240.32:80)

测试账号附在实验报告里

## 🏗️ 技术栈

- **框架**: Spring Boot
- **ORM**: MyBatis-Plus
- **数据库**: MySQL
- **缓存**: Redis
- **安全**: JWT Token 认证
- **构建工具**: Maven
- **邮件服务**: Spring Mail

## 📁 项目结构

```
mall/
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
│   ├── CartItemController.java    # 购物车管理
│   ├── CategoryController.java    # 分类管理
│   ├── FileController.java        # 文件上传下载
│   ├── OrderController.java      # 订单管理
│   ├── ProductController.java     # 商品管理
│   ├── StatisticController.java   # 数据统计
│   └── UserController.java        # 用户管理
├── dto/                # 数据传输对象
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
│   └── User.java
├── enums/              # 枚举类
│   ├── OrderStatusEnum.java
│   ├── OrderTypeEnum.java
│   └── SortDirectionEnum.java
├── exception/          # 异常处理
│   └── BusinessException.java
├── interceptor/        # 拦截器
│   ├── AuthInterceptor.java
│   └── WebConfig.java
├── mappers/            # 数据访问层
│   ├── CartItemMapper.java
│   ├── CategoryMapper.java
│   ├── OrderItemMapper.java
│   ├── OrderMapper.java
│   ├── ProductMapper.java
│   └── UserMapper.java
├── service/            # 服务层
│   ├── impl/           # 服务实现
│   └── *.java          # 服务接口
├── utils/              # 工具类
│   ├── CurrentUserUtil.java
│   ├── PasswordUtil.java
│   ├── RedisUtil.java
│   └── StringUtil.java
└── vo/                 # 视图对象
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

## 🚀 核心功能

### 用户管理

- 用户注册、登录、登出
- 自动登录和 Token 管理
- 用户信息修改、密码修改
- 余额充值功能
- 管理员权限验证

### 商品管理

- 商品增删改查
- 商品分类管理
- 商品图片上传
- 商品库存管理

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

### 数据统计

- 销售额统计（日、周、月、年）
- 商品销量排行榜
- 订单状态分布统计

### 文件管理

- 图片上传和存储
- 静态资源访问

## 🔧 安装和运行

### 环境要求

- JDK 8+
- MySQL 5.7+
- Redis
- Maven

## 📡 API 接口

### 认证相关

- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `POST /user/logout` - 用户登出
- `POST /user/sendCode` - 发送验证码

### 商品相关

- `GET /product/getProductList` - 获取商品列表
- `GET /product/{productId}` - 获取商品详情
- `POST /product` - 新增商品（管理员）
- `PUT /product` - 修改商品（管理员）

### 购物车相关

- `POST /cartItem` - 添加商品到购物车
- `PUT /cartItem` - 修改购物车商品
- `GET /cartItem/getAll` - 获取购物车列表
- `GET /cartItem/totalPrice` - 计算总价

### 订单相关

- `POST /order` - 创建订单
- `POST /order/submitOrder` - 提交订单
- `POST /order/pay` - 支付订单
- `GET /order/{orderId}` - 获取订单详情

### 数据统计

- `GET /statistic/getTotalAmount` - 获取销售额统计
- `GET /statistic/getProductTopN` - 获取商品销量排行
- `GET /statistic/getAllTypeOrderCount` - 获取订单状态统计

## 📊 数据库设计

系统包含以下核心表：

- `user` - 用户表
- `product` - 商品表
- `category` - 分类表
- `cart_item` - 购物车表
- `order` - 订单表
- `order_item` - 订单商品表

## 🛠️ 开发说明

### 自定义异常处理

系统使用统一的异常处理机制，通过 `GlobalExceptionHandler` 处理业务异常和系统异常。

### 分页查询

所有列表查询接口都支持分页，使用 MyBatis-Plus 的分页插件。

### 数据验证

使用 Spring Validation 进行参数校验，确保数据的完整性。
