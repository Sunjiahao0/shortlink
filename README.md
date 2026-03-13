# 短链接生成服务 ShortLink

基于 Spring Boot + Redis + 布隆过滤器实现的高性能短链接服务，支持自动/自定义短码生成、302 重定向、点击统计和缓存优化，有效防止缓存穿透。

## 技术栈
- 后端框架：Spring Boot 2.7 / 3.x
- ORM：MyBatis
- 数据库：MySQL 8.0
- 缓存：Redis (QPS提升约5倍)
- 防穿透：布隆过滤器 (Guava)
- 分布式ID：雪花算法 (Hutool)

## 功能特性
- ✅ 短链接生成（支持自定义短码）
- ✅ 302重定向
- ✅ Redis缓存热点数据
- ✅ 布隆过滤器防止缓存穿透
- ✅ 点击统计（异步更新）
- ✅ 分布式ID保证集群唯一


### 环境要求
- JDK 11+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 运行步骤
1. 克隆项目：`git clone https://github.com/Sunjiahao0/shortlink.git`
2. 创建数据库 `shortlink`，执行 `src/main/resources/sql/shortlink.sql`
3. 修改 `src/main/resources/application.yml` 中的数据库和Redis密码
4. 运行主类 `ShortLinkApplication`
5. 测试接口：
   - 创建短链接：
     ```bash
     POST http://localhost:8080/shorten
     Content-Type: application/json
     ```
     ```json
     {
       "url": "https://www.baidu.com",
       "customCode": "mybaidu"
     }
     ```
   - 重定向：访问 `http://localhost:8080/{shortCode}`
  
### 项目结构
src/main/java/com/example/shortlink
├── config      # 配置类（Redis、雪花算法）
├── controller  # 接口层
├── service     # 业务逻辑层
├── mapper      # 数据访问层
├── entity      # 实体类
└── util        # 工具类（短码生成、布隆过滤器）

### 联系方式
邮箱：sunjiahao407@outlook.com
