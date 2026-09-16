# 豪车租赁管理系统 · 后台服务

豪华汽车租赁后台管理系统的 Java 后端服务，为 React 管理端提供 REST API，覆盖认证鉴权、RBAC、车辆与订单、财务统计、营销优惠券等完整业务。

- 服务端口：`8088`
- 接口文档：[http://localhost:8088/doc.html](http://localhost:8088/doc.html)（Knife4j，免登录）
- 配套前端：同级目录 `my-first-react-app`（React 18 管理后台）

---

## 技术栈

| 类别 | 选型 |
|------|------|
| 框架 | Spring Boot 3.2.6 |
| 语言 | Java 17 |
| ORM | MyBatis-Plus 3.5.8 |
| 数据库 | MySQL 8.0（库名 `car_rental`） |
| 缓存 | Redis |
| 认证 | JWT（access + refresh）+ Spring Security |
| 接口文档 | Knife4j 4.5.0（OpenAPI 3） |
| 工具 | Hutool、Lombok、Jakarta Validation |
| 导出 | Apache POI（Excel）、PDFBox（PDF） |
| 构建 | Maven |

---

## 功能模块

| 模块 | 路径前缀 | 说明 |
|------|----------|------|
| 认证登录 | `/api/auth` | 登录、注册、退出、刷新令牌、找回密码 |
| 个人中心 | `/api/profile` | 资料、头像、修改密码 |
| 用户 / 角色 / 菜单 | `/api/system/user`、`/role`、`/menu` | RBAC：用户绑定多角色，角色分配菜单与按钮权限 |
| 主题配置 | `/api/system/theme` | 当前用户布局、主题色等个性化配置 |
| 系统配置 / 字典 | `/api/system/config`、`/api/dict` | 全局参数、数据字典 |
| 公告 / 轮播 | `/api/system/announcement`、`/api/carousel` | 后台维护；C 端公开公告走 `/api/public/**` |
| 操作日志 | `/api/operation-log` | 写操作审计，支持导出 |
| 文件管理 | `/api/file`、`/api/upload` | 上传、回收站、物理删除 |
| 车辆 | `/api/car` | 车辆 CRUD、上下架、图片、证件、维保、违章、GPS |
| 门店配置 | `/api/store-config` | 城市 / 门店；公开树接口 `/api/public/city-store-tree` |
| 租客 | `/api/customer` | 客户资料、启停、实名状态、租赁历史 |
| 会员认证 | `/api/customer/verify` | C 端实名认证审核 |
| 订单 | `/api/order` | 订单 CRUD、状态流转、财务流水维护 |
| 仪表盘 | `/api/statistics` | 总览、订单趋势、营收、车型占比、热门车辆、复购、高峰时段 |
| 财务 | `/api/finance` | 流水、成本、发票、对账、利润分析 |
| 优惠券 | `/api/coupon`、`/api/customer/coupon` | 投放 / 下线、领券、锁定、核销 |
| 售后投诉 | `/api/after-sales` | 工单处理、优先级、关联车辆 |
| 意见反馈 | `/api/feedback` | C 端反馈处理 |

---

## 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0，库名 `car_rental`
- Redis（默认 `localhost:6379`）

部分业务会跨库读取 C 端库 `car_rental_customer`（会员优惠券、实名认证、意见反馈）。请保证同一 MySQL 实例上两个库都存在，且当前账号有访问权限。

本仓库**不包含**建表 SQL 与初始化数据。启动前请自行准备表结构，并至少有一条可登录的 `sys_user` 记录。

---

## 快速启动

1. 确认 MySQL、Redis 已启动，并创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS car_rental DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.yml` 中的数据源、Redis 与 JWT 配置（见下文）。

3. 启动：

```bash
# 开发
mvn spring-boot:run

# 或打包后运行
mvn -DskipTests package
java -jar target/rental-1.0.0.jar
```

4. 打开接口文档：http://localhost:8088/doc.html

5. 调用 `POST /api/auth/login` 获取 `accessToken`，在文档右上角 **Authorize** 填入：

```text
Bearer {accessToken}
```

（`Bearer` 后有空格。）之后即可调试需登录接口。

---

## 配置说明

主配置文件：`src/main/resources/application.yml`

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | `8088` | 服务端口 |
| `spring.datasource.*` | `localhost:3306/car_rental` | MySQL 连接，请改成自己的账号密码 |
| `spring.data.redis.*` | `localhost:6379` | Redis，无密码时 `password` 留空 |
| `jwt.secret` | 见配置文件 | JWT 签名密钥，生产环境务必更换 |
| `jwt.expiration` | `7200000`（2 小时） | access token 有效期（毫秒） |
| `jwt.refresh-expiration` | `604800000`（7 天） | refresh token 有效期（毫秒） |
| `upload.path` | `${user.home}/car_rental_uploads` | 本地上传目录 |
| `mybatis-plus.global-config.db-config.logic-delete-field` | `isDelete` | 逻辑删除字段，删除值 `1` |

上传文件通过 `/uploads/**` 静态映射对外访问。接口返回相对路径（如 `/uploads/xxx.jpg`），由前端拼接服务地址，避免库里存死 IP。

---

## 接口约定

### 统一响应

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 401 | 未登录 / Token 失效 |
| 403 | 已登录但无权限 |
| 500 | 业务或系统异常 |

### 分页

- 请求参数：`page`（从 1 开始）、`pageSize`
- 响应 `data`：

```json
{
  "list": [],
  "total": 0,
  "page": 1,
  "pageSize": 10
}
```

### 鉴权

除下列公开接口外，均需在请求头携带 JWT：

```http
Authorization: Bearer {accessToken}
```

**公开接口（无需登录）：**

- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh`
- `POST /api/auth/forgot-password/**`
- `/api/upload/**`
- `/uploads/**`
- `/api/public/**`
- Knife4j / Swagger 文档相关路径

登录成功返回 `accessToken`（短期，用于接口鉴权）和 `refreshToken`（长期，仅用于刷新）。access token 过期后，前端用 refresh token 调用 `/api/auth/refresh` 换取新的 access token。退出登录时 token 会写入 Redis 黑名单，立即失效。refresh token 不能用于业务接口鉴权。

按钮级权限由 `@RequirePermission("模块:资源:动作")` 校验，例如 `settings:user:list`。超级管理员拥有通配权限；权限不足返回 403，提示信息不暴露具体权限标识。

### 跨域

已开启全局 CORS（`allowedOriginPattern=*`），可直接对接前端本地开发。

---

## 项目结构

```
src/main/java/com/car/rental
├── CarRentalApplication.java     # 启动类（@EnableScheduling）
├── config/                       # Security / Redis / MP / CORS / Knife4j / 静态资源
├── common/
│   ├── annotation/               # @RequirePermission、@LogChanges
│   ├── dto/                      # PageDTO
│   ├── exception/                # 全局异常
│   ├── result/                   # Result、PageResult
│   ├── security/                 # JWT 过滤器、权限切面、操作日志切面
│   └── util/                     # JwtUtil、SecurityUtil
├── entity/                       # 实体
├── mapper/                       # 部分公共 Mapper
└── module/                       # 按业务分包（controller / service / mapper）
    ├── auth/
    ├── system/
    ├── car/
    ├── customer/
    ├── order/
    ├── statistics/
    ├── marketing/
    ├── store/
    ├── after_sales/
    ├── feedback/
    └── upload/
```

分层：Controller（入参校验、权限注解）→ Service（业务）→ Mapper（数据访问）。

---

## 定时任务

| 任务 | 频率 | 作用 |
|------|------|------|
| `OrderFinanceScheduler` | 每 30 分钟（启动 1 分钟后首次） | 到期租赁中订单自动完成；补生成缺失的财务流水 / 发票 |
| `CouponExpireScheduler` | 每 30 分钟（启动 30 秒后首次） | 已投放且过有效期的优惠券自动下线 |

两条任务均幂等，可重复执行。

---

## 主要数据表

管理库 `car_rental`：

| 表 | 用途 |
|----|------|
| `sys_user` / `sys_role` / `sys_user_role` / `sys_menu` | RBAC |
| `sys_theme_config` / `sys_config` / `sys_file` | 主题、系统参数、文件元数据 |
| `sys_dict_type` / `sys_dict_data` | 数据字典 |
| `operation_log` | 操作日志 |
| `announcement` / `carousel` | 公告、轮播 |
| `car_info` / `car_image` / `car_config` | 车辆及图片、配置 |
| `customer_info` / `customer_order` / `customer_order_item` | 租客、订单 |
| `customer_city` / `customer_store` | 城市、门店 |
| `finance_record` / `cost_record` / `invoice` / `reconciliation` | 财务 |
| `coupon` / `coupon_car` | 优惠券及适用车辆 |
| `after_sales_complaint` | 售后工单 |

C 端库 `car_rental_customer`（跨库）：

| 表 | 用途 |
|----|------|
| `member_verify_record` | 会员实名认证 |
| `member_coupon` | 会员已领优惠券 |
| `feedback` | 意见反馈 |

业务表普遍使用逻辑删除字段 `is_delete`（1 删除 / 0 未删除）。

---

## 文件上传

- `POST /api/upload/image` 单文件
- `POST /api/upload/images` 多文件

限制：单文件 ≤ 50MB。允许扩展名包括图片（jpg/png/webp 等）、文档（pdf/docx/xlsx 等）、视频（mp4/webm 等）。成功后写入 `sys_file`，返回 `url`、`fileId` 等元信息。

---

## 与前端联调

1. 后端默认 `http://localhost:8088`。
2. 登录后把 `accessToken` 放到 `Authorization: Bearer ...`。
3. 401 时用 `refreshToken` 调 `/api/auth/refresh`；刷新失败再跳登录页。
4. 上传接口返回相对路径，前端需拼接后端 origin 后再展示。
5. 完整接口清单、入参与示例以 Knife4j 文档为准：http://localhost:8088/doc.html

---

## 许可证

仅供学习与项目交付使用。
