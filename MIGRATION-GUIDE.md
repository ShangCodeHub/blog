# 博客项目迁移指南

将 Shang 博客从一个服务器迁移到另一个服务器的完整步骤。

## 前提条件

- 新服务器已安装 Docker 和 Docker Compose
- 新服务器开放端口：3307, 6380, 8800, 9001, 9003

## 迁移步骤

### 第一步：在旧服务器备份数据

SSH 登录旧服务器，执行：

```bash
cd /path/to/shang/docker
chmod +x backup-for-migration.sh
./backup-for-migration.sh
```

这会生成一个 `backup-YYYYMMDD-HHMMSS` 目录，包含：
- `database.sql` - 数据库备份
- `upload-files.tar.gz` - 上传的文件
- `source-code.tar.gz` - 项目代码
- `mysql-config.tar.gz` - MySQL 配置（可选）
- `redis-config.tar.gz` - Redis 配置（可选）

### 第二步：打包并传输到新服务器

在旧服务器：
```bash
cd docker
tar -czf blog-migration-$(date +%Y%m%d).tar.gz backup-*/

# 传输到新服务器（选择一种方式）
scp blog-migration-*.tar.gz root@新服务器IP:/opt/
# 或使用 rsync
rsync -avz blog-migration-*.tar.gz root@新服务器IP:/opt/
```

### 第三步：在新服务器恢复

SSH 登录新服务器：

```bash
cd /opt
tar -xzf blog-migration-*.tar.gz
cd backup-*

# 1. 解压项目代码
tar -xzf source-code.tar.gz -C /opt/
cd /opt

# 目录结构应该是：
# /opt/
# ├── blog/           # 后端代码
# ├── blog-admin/     # 管理端前端
# ├── blog-web/       # 用户端前端
# └── docker/         # Docker 配置和数据

# 2. 安装 Docker（如未安装）
# Ubuntu/Debian:
curl -fsSL https://get.docker.com | sh
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# CentOS/RHEL:
yum install -y docker docker-compose
systemctl start docker
systemctl enable docker
```

### 第四步：构建后端 JAR

```bash
cd /opt/blog

# 安装 Maven（如未安装）
# Ubuntu:
apt-get install -y maven
# CentOS:
yum install -y maven

# 构建 JAR
mvn clean package -DskipTests

# 复制到 Docker 构建目录
cp shang-server/target/*.jar SHANG-BLOG.jar
```

### 第五步：启动服务

```bash
cd /opt/docker

# 创建必要目录
mkdir -p data/mysql/{data,conf,logs,backup}
mkdir -p data/redis/conf
mkdir -p data/upload
mkdir -p data/logs
mkdir -p data/nginx/{web,admin}/{logs,conf}

# 恢复上传文件（如果有）
if [ -f "/opt/backup-*/upload-files.tar.gz" ]; then
    tar -xzf /opt/backup-*/upload-files.tar.gz -C data/
fi

# 启动服务
chmod +x deploy.sh
./deploy.sh
```

### 第六步：恢复数据库

```bash
cd /opt/docker

# 等待 MySQL 启动完成（约 30 秒）
sleep 30

# 导入数据库
docker exec -i shang-blog-mysql mysql -uroot -proot123 -e "CREATE DATABASE IF NOT EXISTS \`Shang-Blog\`;"
docker exec -i shang-blog-mysql mysql -uroot -proot123 Shang-Blog < /opt/backup-*/database.sql

# 验证导入
docker exec shang-blog-mysql mysql -uroot -proot123 -e "SHOW TABLES;" Shang-Blog
```

### 第七步：验证迁移

```bash
# 查看所有服务状态
cd /opt/docker
docker-compose ps

# 查看日志
docker-compose logs -f

# 测试访问
curl http://localhost:8800/actuator/health
curl http://localhost:9001
curl http://localhost:9003
```

## 常见问题

### 1. 端口被占用

修改 `docker/docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "新端口:8800"  # 后端
  - "新端口:80"    # Web 前端
```

### 2. 内存不足

编辑 `docker/docker-compose.yml`，为服务添加资源限制：
```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          memory: 1G
```

### 3. 数据库导入失败

如果数据库太大，分步导入：
```bash
# 只导入表结构（前 100 行）
head -100 database.sql | docker exec -i shang-blog-mysql mysql -uroot -proot123 Shang-Blog

# 然后导入数据
tail -n +101 database.sql | docker exec -i shang-blog-mysql mysql -uroot -proot123 Shang-Blog
```

### 4. 上传文件权限问题

```bash
chmod -R 755 data/upload
chown -R 1000:1000 data/upload  # 匹配容器内用户
```

## 配置更新

迁移后可能需要更新的配置：

### 1. 后端配置
编辑 `blog/shang-server/src/main/resources/application-pro.yml`：
- 第三方登录回调 URL（QQ/微博/Gitee/GitHub）
- 七牛云 OSS 配置
- 邮件配置

### 2. Nginx 配置
编辑 `blog-web/nginx.conf` 和 `blog-admin/nginx.conf`：
- 更新 server_name
- 配置 HTTPS 证书

### 3. 重新构建并部署
```bash
cd /opt/blog
mvn clean package -DskipTests
cp shang-server/target/*.jar SHANG-BLOG.jar

cd /opt/docker
docker-compose up -d --build backend
```

## 快速检查清单

- [ ] Docker 和 Docker Compose 已安装
- [ ] 端口 3307, 6380, 8800, 9001, 9003 已开放
- [ ] 数据库已导入
- [ ] 上传文件已恢复
- [ ] 后端 JAR 已构建
- [ ] 所有容器运行正常
- [ ] 网站可访问
- [ ] 后台可登录

## 回滚方案

如果迁移失败，可以在旧服务器继续运行：
```bash
# 旧服务器上
cd /opt/shang/docker
docker-compose up -d
```

确保 DNS 或反向代理指向旧服务器直到新服务器验证通过。
