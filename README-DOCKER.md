# 🐳 Docker Compose 部署快速参考

## 一键部署

```bash
# 进入 docker 目录
cd docker

# Windows
deploy.bat

# Linux/Mac
chmod +x deploy.sh && ./deploy.sh
```

## 服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端Web | http://localhost:9001 | 博客网站（用户端） |
| 前端Admin | http://localhost:9003 | 管理后台（管理端） |
| 后端API | http://localhost:8800 | Spring Boot API |
| MySQL | localhost:3307 | 数据库（root/root123） |
| Redis | localhost:6380 | 缓存（密码：redis123） |

## 数据目录映射（方便迁移）

| 数据类型 | 本地目录 | 说明 |
|---------|---------|------|
| MySQL数据 | `./docker/data/mysql/data` | 数据库文件，迁移时复制此目录 |
| MySQL备份 | `./docker/data/mysql/backup` | 数据库备份文件 |
| Redis配置 | `./docker/data/redis/conf` | Redis配置文件（数据不持久化） |
| 上传文件 | `./docker/data/upload` | 用户上传的文件 |
| 应用日志 | `./docker/data/logs` | 后端应用日志 |
| Nginx日志 | `./docker/data/nginx/web/logs`<br>`./docker/data/nginx/admin/logs` | Web和Admin的Nginx日志 |

## 常用命令

```bash
# 进入 docker 目录
cd docker

# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [服务名]

# 重启服务
docker-compose restart [服务名]

# 重新构建并启动
docker-compose up -d --build
```

## 数据库操作

```bash
# 进入 MySQL
docker exec -it shang-blog-mysql mysql -uroot -proot123

# 导入数据库
docker exec -i shang-blog-mysql mysql -uroot -proot123 Shang-Blog < shang-blog.sql

# 备份数据库
docker exec shang-blog-mysql mysqldump -uroot -proot123 Shang-Blog > backup.sql
```

## 网络隔离

- **网络名称**: `shang-blog-network`
- **子网**: `172.21.0.0/24`
- **与 Dify 完全隔离**，使用不同端口避免冲突

## 更新部署

### 更新后端
```bash
cd blog
mvn clean package -DskipTests
cp shang-server/target/*.jar SHANG-BLOG.jar
cd ../docker
docker-compose up -d --build backend
```

### 更新前端
```bash
cd docker

# 更新Web前端
docker-compose up -d --build frontend-web

# 更新Admin前端
docker-compose up -d --build frontend-admin
```

## 故障排查

```bash
cd docker

# 查看所有日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend-web
docker-compose logs frontend-admin
docker-compose logs mysql

# 检查服务健康状态
docker-compose ps
```

详细文档请查看：[部署说明.md](./部署说明.md)
