# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is **Shang博客** (Shang Blog), a full-stack blog platform with:
- **Backend**: Multi-module Spring Boot 2.7.0 application (Java 8)
- **Frontend Web**: Vue 2.7 user portal (`blog-web/`)
- **Frontend Admin**: Vue 3.2 admin dashboard (`blog-admin/`)
- **Mobile**: UniApp project (`uniapp-blog/`)
- **Deployment**: Docker Compose with MySQL 8.0, Redis 7, Nginx

## Build Commands

### Backend (blog/)

```bash
cd blog
# Build all modules and package
mvn clean package -DskipTests

# Copy JAR for Docker deployment
cp shang-server/target/*.jar SHANG-BLOG.jar
```

### Frontend Web (blog-web/)

```bash
cd blog-web
npm install
npm run dev      # Development server
npm run build    # Production build
```

### Frontend Admin (blog-admin/)

```bash
cd blog-admin
npm install
npm run dev      # Development server
npm run build    # Production build
```

### Docker Deployment (docker/)

```bash
cd docker

# One-click deployment
./deploy.sh      # Linux/Mac
deploy.bat       # Windows

# Manual commands
docker-compose up -d --build     # Build and start all services
docker-compose down              # Stop all services
docker-compose logs -f [service] # View logs (backend|mysql|redis|frontend-web|frontend-admin)
```

## Architecture

### Backend Module Structure (blog/)

All modules are under `com.shang` package:

| Module | Purpose | Key Dependencies |
|--------|---------|------------------|
| `shang-server` | Entry point, config | Aggregates all modules; `NeatAdminApplication` is main class |
| `shang-api` | Portal REST APIs | For blog visitors; depends on shang-common |
| `shang-admin` | Admin REST APIs | For management; depends on Shang-AiMode, shang-common |
| `shang-auth` | Authentication | Sa-Token based; QQ/Weibo/Gitee/GitHub OAuth |
| `shang-common` | Shared utilities | Entity, mapper, utils, constants |
| `shang-file` | File storage | X-File-Storage; local + cloud storage |
| `shang-quartz` | Scheduled jobs | Quartz scheduler |
| `Shang-AiMode` | AI integration | Dify API, DashScope (Tongyi), OkHttp |

**Important**: The `Shang-AiMode` module excludes springdoc dependencies to avoid Java version conflicts and uses OkHttp for streaming requests compatible with Java 8.

### Application Configuration

Configuration files are in `blog/shang-server/src/main/resources/`:

- `application.yml` - Base config (Sa-Token, MyBatis-Plus, third-party OAuth)
- `application-dev.yml` - Local development (MySQL localhost:3306, Redis localhost:6379)
- `application-pro.yml` - Production for Docker (MySQL mysql:3306, Redis redis:6379)

Key configurations:
- **Server port**: 8800
- **Sa-Token**: Token name `Authorization`, timeout 7 days
- **MyBatis-Plus**: Mapper location `classpath:/mapper/*Mapper.xml`
- **Knife4j**: API docs at `/doc.html` (enabled in dev with auth test/123456)

### Frontend Web (blog-web/)

Vue 2.7 + Vite stack:
- **State**: Vuex 3.6
- **UI**: Element UI 2.15
- **Editor**: mavon-editor (Markdown)
- **Features**: Lazy loading, animations (GSAP, animate.css), audio player

### Frontend Admin (blog-admin/)

Vue 3.2 + Vite stack:
- **State**: Pinia 2.0 with persisted state
- **UI**: Element Plus 2.3
- **Editor**: WangEditor
- **Charts**: ECharts 5.5
- **Icons**: Element Plus Icons

### Docker Services

| Service | Container Port | Host Port | Purpose |
|---------|---------------|-----------|---------|
| MySQL | 3306 | 3307 | Database (root/root123) |
| Redis | 6379 | 6380 | Cache (password: redis123) |
| Backend | 8800 | 8800 | Spring Boot API |
| Frontend Web | 80 | 9001 | Nginx - User portal |
| Frontend Admin | 80 | 9003 | Nginx - Admin dashboard |

Network: `shang-blog-network` (172.21.0.0/24)

## Development Workflow

### Local Development

1. Start MySQL and Redis locally (or use Docker for deps only)
2. Run backend: Import `blog/pom.xml` in IDE, run `NeatAdminApplication` with `dev` profile
3. Run frontend web: `cd blog-web && npm run dev`
4. Run frontend admin: `cd blog-admin && npm run dev`

API docs available at: `http://127.0.0.1:8800/shiyi/doc.html`

### Production Deployment

1. Build backend JAR: `cd blog && mvn clean package -DskipTests && cp shang-server/target/*.jar SHANG-BLOG.jar`
2. Deploy: `cd docker && ./deploy.sh`
3. Access:
   - Blog: http://localhost:9001
   - Admin: http://localhost:9003
   - API: http://localhost:8800

## Key Files

- **Backend main**: `blog/shang-server/src/main/java/com/shang/NeatAdminApplication.java`
- **Backend config**: `blog/shang-server/src/main/resources/application-dev.yml`
- **Web entry**: `blog-web/src/main.js`
- **Admin entry**: `blog-admin/src/main.ts`
- **Docker compose**: `docker/docker-compose.yml`
- **DB SQL**: Root `shang-blog.sql`

## Module Dependencies

```
shang-server (aggregator)
  ├── shang-admin → Shang-AiMode, shang-common
  ├── shang-api → shang-common
  ├── shang-auth → shang-common
  ├── shang-file → shang-common
  ├── shang-quartz → shang-common
  └── Shang-AiMode → shang-common
```

All modules ultimately depend on `shang-common` for entities, mappers, and utilities.
