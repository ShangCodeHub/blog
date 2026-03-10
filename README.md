# Shang Blog - 上博客

<p align="center">
  <b>一个现代化的全栈博客平台</b><br>
  <b>A Modern Full-Stack Blog Platform</b><br>
  <b>モダンなフルスタックブログプラットフォーム</b>
</p>

<p align="center">
  <a href="README.md">中文</a> |
  <a href="README_EN.md">English</a> |
  <a href="README_JP.md">日本語</a>
</p>

---

## 📖 项目介绍

**上博客 (Shang Blog)** 是一个功能丰富的全栈博客平台，采用前后端分离架构，支持多端访问。项目包含用户端、管理后台和小程序/APP，满足不同场景的需求。

## 🚀 技术栈

### 后端 (blog/)
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.0 | 核心框架 |
| Java | 8 | 编程语言 |
| MyBatis-Plus | - | ORM 框架 |
| Sa-Token | - | 权限认证 |
| MySQL | 8.0 | 数据库 |
| Redis | 7 | 缓存 |
| RabbitMQ | - | 消息队列 |
| Quartz | - | 定时任务 |
| Dify API | - | AI 集成 |

### 前端
| 项目 | 技术栈 | 说明 |
|------|--------|------|
| blog-web | Vue 2.7 + Vite | 用户门户网站 |
| blog-admin | Vue 3.2 + Element Plus | 管理后台 |
| uniapp-blog | UniApp | 移动端小程序/APP |

## 📁 项目结构

```
shang/
├── blog/                   # 后端项目
│   ├── shang-server/      # 入口模块
│   ├── shang-api/         # 门户 API
│   ├── shang-admin/       # 管理 API
│   ├── shang-auth/        # 认证模块
│   ├── shang-common/      # 公共模块
│   ├── shang-file/        # 文件存储
│   ├── shang-quartz/      # 定时任务
│   └── Shang-AiMode/      # AI 模块
├── blog-web/              # 前端用户端
├── blog-admin/            # 前端管理后台
├── uniapp-blog/           # 移动端
├── docker/                # Docker 部署配置
└── shang-blog.sql         # 数据库初始化脚本
```

## 🔧 快速开始

### 本地开发

1. **启动数据库**
```bash
docker-compose up -d mysql redis
```

2. **运行后端**
```bash
cd blog
mvn clean install -DskipTests
cd shang-server
mvn spring-boot:run
```

3. **运行前端**
```bash
# 用户端
cd blog-web
npm install
npm run dev

# 管理后台
cd blog-admin
npm install
npm run dev
```

### Docker 一键部署

```bash
cd docker
./deploy.sh  # Linux/Mac
deploy.bat     # Windows
```

访问地址：
- 博客前台：http://localhost:9001
- 管理后台：http://localhost:9003
- API 文档：http://localhost:8800/shiyi/doc.html

## ✨ 核心功能

- 📝 Markdown 编辑器支持
- 🎨 自定义主题
- 🔍 全文搜索
- 💬 评论系统
- 📊 数据统计面板
- 🤖 AI 智能助手
- 🔐 多平台 OAuth 登录 (QQ/微博/Gitee/GitHub)
- 📱 响应式设计
- 🌙 暗黑模式

## 🐳 Docker 服务

| 服务 | 容器端口 | 主机端口 | 说明 |
|------|---------|---------|------|
| MySQL | 3306 | 3307 | 数据库 |
| Redis | 6379 | 6380 | 缓存 |
| 后端 | 8800 | 8800 | Spring Boot API |
| 前端门户 | 80 | 9001 | 用户门户网站 |
| 前端管理 | 80 | 9003 | 管理后台 |

## 📄 许可证

[MIT](LICENSE)

## 👤 作者

Shang

---

<p align="center">
  <b>Made with ❤️ by Shang</b>
</p>
