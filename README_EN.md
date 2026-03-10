# Shang Blog

<p align="center">
  <b>A Modern Full-Stack Blog Platform</b>
</p>

<p align="center">
  <a href="README.md">中文</a> |
  <a href="README_EN.md">English</a> |
  <a href="README_JP.md">日本語</a>
</p>

---

## 📖 Introduction

**Shang Blog** is a feature-rich full-stack blog platform with a decoupled architecture, supporting multi-terminal access. The project includes a user portal, admin dashboard, and mobile mini-programs/APP to meet various scenario needs.

## 🚀 Tech Stack

### Backend (blog/)
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

### Frontend
| Project | Tech Stack | Description |
|---------|------------|-------------|
| blog-web | Vue 2.7 + Vite | User Portal |
| blog-admin | Vue 3.2 + Element Plus | Admin Dashboard |
| uniapp-blog | UniApp | Mobile Mini-programs/APP |

## 📁 Project Structure

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

## 🔧 Quick Start

### Local Development

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

### Docker Deployment

```bash
cd docker
./deploy.sh  # Linux/Mac
deploy.bat     # Windows
```

Access URLs:
- Blog Portal: http://localhost:9001
- Admin Dashboard: http://localhost:9003
- API Docs: http://localhost:8800/shiyi/doc.html

## ✨ Key Features

- 📝 Markdown Editor Support
- 🎨 Custom Themes
- 🔍 Full-Text Search
- 💬 Comment System
- 📊 Statistics Dashboard
- 🤖 AI Smart Assistant
- 🔐 Multi-Platform OAuth (QQ/Weibo/Gitee/GitHub)
- 📱 Responsive Design
- 🌙 Dark Mode

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
