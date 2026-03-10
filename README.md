# Shang Blog - 上博客

<p align="center">
  <b>一个现代化的全栈博客平台</b><br>
  <b>A Modern Full-Stack Blog Platform</b><br>
  <b>モダンなフルスタックブログプラットフォーム</b>
</p>

<p align="center">
  <a href="#-中文">中文</a> |
  <a href="#-english">English</a> |
  <a href="#-日本語">日本語</a>
</p>

---

## 🇨🇳 中文

### 📖 项目介绍

**上博客 (Shang Blog)** 是一个功能丰富的全栈博客平台，采用前后端分离架构，支持多端访问。项目包含用户端、管理后台和小程序/APP，满足不同场景的需求。

### 🚀 技术栈

#### 后端 (blog/)
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

#### 前端
| 项目 | 技术栈 | 说明 |
|------|--------|------|
| blog-web | Vue 2.7 + Vite | 用户门户网站 |
| blog-admin | Vue 3.2 + Element Plus | 管理后台 |
| uniapp-blog | UniApp | 移动端小程序/APP |

### 📁 项目结构

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

### 🔧 快速开始

#### 本地开发

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

#### Docker 一键部署

```bash
cd docker
./deploy.sh  # Linux/Mac
deploy.bat     # Windows
```

访问地址：
- 博客前台：http://localhost:9001
- 管理后台：http://localhost:9003
- API 文档：http://localhost:8800/shiyi/doc.html

### ✨ 核心功能

- 📝 Markdown 编辑器支持
- 🎨 自定义主题
- 🔍 全文搜索
- 💬 评论系统
- 📊 数据统计面板
- 🤖 AI 智能助手
- 🔐 多平台 OAuth 登录 (QQ/微博/Gitee/GitHub)
- 📱 响应式设计
- 🌙 暗黑模式

---

## 🇺🇸 English

### 📖 Introduction

**Shang Blog** is a feature-rich full-stack blog platform with a decoupled architecture, supporting multi-terminal access. The project includes a user portal, admin dashboard, and mobile mini-programs/APP to meet various scenario needs.

### 🚀 Tech Stack

#### Backend (blog/)
| Technology | Version | Description |
|------------|---------|-------------|
| Spring Boot | 2.7.0 | Core Framework |
| Java | 8 | Programming Language |
| MyBatis-Plus | - | ORM Framework |
| Sa-Token | - | Authentication |
| MySQL | 8.0 | Database |
| Redis | 7 | Cache |
| RabbitMQ | - | Message Queue |
| Quartz | - | Job Scheduler |
| Dify API | - | AI Integration |

#### Frontend
| Project | Tech Stack | Description |
|---------|------------|-------------|
| blog-web | Vue 2.7 + Vite | User Portal |
| blog-admin | Vue 3.2 + Element Plus | Admin Dashboard |
| uniapp-blog | UniApp | Mobile Mini-programs/APP |

### 📁 Project Structure

```
shang/
├── blog/                   # Backend Project
│   ├── shang-server/      # Entry Module
│   ├── shang-api/         # Portal API
│   ├── shang-admin/       # Admin API
│   ├── shang-auth/        # Auth Module
│   ├── shang-common/      # Common Module
│   ├── shang-file/        # File Storage
│   ├── shang-quartz/      # Scheduled Jobs
│   └── Shang-AiMode/      # AI Module
├── blog-web/              # Web Frontend
├── blog-admin/            # Admin Frontend
├── uniapp-blog/           # Mobile App
├── docker/                # Docker Config
└── shang-blog.sql         # Database Schema
```

### 🔧 Quick Start

#### Local Development

1. **Start Database**
```bash
docker-compose up -d mysql redis
```

2. **Run Backend**
```bash
cd blog
mvn clean install -DskipTests
cd shang-server
mvn spring-boot:run
```

3. **Run Frontend**
```bash
# User Portal
cd blog-web
npm install
npm run dev

# Admin Dashboard
cd blog-admin
npm install
npm run dev
```

#### Docker Deployment

```bash
cd docker
./deploy.sh  # Linux/Mac
deploy.bat     # Windows
```

Access URLs:
- Blog Portal: http://localhost:9001
- Admin Dashboard: http://localhost:9003
- API Docs: http://localhost:8800/shiyi/doc.html

### ✨ Key Features

- 📝 Markdown Editor Support
- 🎨 Custom Themes
- 🔍 Full-Text Search
- 💬 Comment System
- 📊 Statistics Dashboard
- 🤖 AI Smart Assistant
- 🔐 Multi-Platform OAuth (QQ/Weibo/Gitee/GitHub)
- 📱 Responsive Design
- 🌙 Dark Mode

---

## 🇯🇵 日本語

### 📖 プロジェクト紹介

**Shang Blog** は、豊富な機能を持つフルスタックブログプラットフォームです。フロントエンドとバックエンドを分離したアーキテクチャを採用し、複数の端末からアクセスできます。プロジェクトにはユーザーポータル、管理画面、モバイルアプリ/ミニプログラムが含まれています。

### 🚀 技術スタック

#### バックエンド (blog/)
| 技術 | バージョン | 説明 |
|------|------------|------|
| Spring Boot | 2.7.0 | コアフレームワーク |
| Java | 8 | プログラミング言語 |
| MyBatis-Plus | - | ORM フレームワーク |
| Sa-Token | - | 認証認可 |
| MySQL | 8.0 | データベース |
| Redis | 7 | キャッシュ |
| RabbitMQ | - | メッセージキュー |
| Quartz | - | ジョブスケジューラー |
| Dify API | - | AI 統合 |

#### フロントエンド
| プロジェクト | 技術スタック | 説明 |
|--------------|--------------|------|
| blog-web | Vue 2.7 + Vite | ユーザーポータル |
| blog-admin | Vue 3.2 + Element Plus | 管理画面 |
| uniapp-blog | UniApp | モバイルアプリ |

### 📁 プロジェクト構成

```
shang/
├── blog/                   # バックエンドプロジェクト
│   ├── shang-server/      # エントリーモジュール
│   ├── shang-api/         # ポータル API
│   ├── shang-admin/       # 管理 API
│   ├── shang-auth/        # 認証モジュール
│   ├── shang-common/      # 共通モジュール
│   ├── shang-file/        # ファイルストレージ
│   ├── shang-quartz/      # スケジュールジョブ
│   └── Shang-AiMode/      # AI モジュール
├── blog-web/              # Web フロントエンド
├── blog-admin/            # 管理画面
├── uniapp-blog/           # モバイルアプリ
├── docker/                # Docker 設定
└── shang-blog.sql         # データベーススキーマ
```

### 🔧 クイックスタート

#### ローカル開発

1. **データベースを起動**
```bash
docker-compose up -d mysql redis
```

2. **バックエンドを実行**
```bash
cd blog
mvn clean install -DskipTests
cd shang-server
mvn spring-boot:run
```

3. **フロントエンドを実行**
```bash
# ユーザーポータル
cd blog-web
npm install
npm run dev

# 管理画面
cd blog-admin
npm install
npm run dev
```

#### Docker デプロイ

```bash
cd docker
./deploy.sh  # Linux/Mac
deploy.bat     # Windows
```

アクセス URL：
- ブログポータル：http://localhost:9001
- 管理画面：http://localhost:9003
- API ドキュメント：http://localhost:8800/shiyi/doc.html

### ✨ 主要機能

- 📝 Markdown エディタ対応
- 🎨 カスタムテーマ
- 🔍 全文検索
- 💬 コメントシステム
- 📊 統計ダッシュボード
- 🤖 AI スマートアシスタント
- 🔐 多プラットフォーム OAuth (QQ/微博/Gitee/GitHub)
- 📱 レスポンシブデザイン
- 🌙 ダークモード

---

## 🐳 Docker Services

| Service | Container Port | Host Port | Description |
|---------|---------------|-----------|-------------|
| MySQL | 3306 | 3307 | Database |
| Redis | 6379 | 6380 | Cache |
| Backend | 8800 | 8800 | Spring Boot API |
| Frontend Web | 80 | 9001 | User Portal |
| Frontend Admin | 80 | 9003 | Admin Dashboard |

## 📄 License

[MIT](LICENSE)

## 👤 Author

Shang

---

<p align="center">
  <b>Made with ❤️ by Shang</b>
</p>
