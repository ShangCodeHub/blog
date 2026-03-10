# DockerServes 修复说明

## 修复内容

### 1. init-volumes.sh - 编码问题
**问题**: 文件使用 GBK 编码，中文显示为乱码 (`������Ҫ��Ŀ¼`)

**修复**:
- 重新编码为 UTF-8
- 删除了危险的 `skip-grant-tables` 配置
- 移除了不必要的 Java 容器初始化步骤

**使用**:
```bash
chmod +x init-volumes.sh
./init-volumes.sh
```

### 2. nginx/conf.d/default.conf - 乱码问题
**问题**: 中文注释显示为乱码 (`ȫ�ַſ��ϴ���С����`)

**修复**:
- 重新编码为 UTF-8
- 统一代理目标为 `myapp:8800`

### 3. docker-compose-fixed.yml - 配置修正
**修复内容**:
| 项目 | 原配置 | 修复后 |
|------|--------|--------|
| MySQL 数据目录 | `./mysql/data/mysql:/var/lib/mysql` | `./mysql/data:/var/lib/mysql` |
| 网络配置 | `external: true` | 自动创建网络 |
| 服务名 | `myapp` (docker-compose.yml) | `myapp` (保持一致) |
| 健康检查 | 无 | 添加 MySQL 和 Backend |
| Redis 配置 | 无配置文件 | 挂载配置文件 |

## 部署步骤

### 1. 初始化目录和数据
```bash
cd /home/DockerServes
chmod +x init-volumes.sh
./init-volumes.sh
```

### 2. 复制修复后的配置
```bash
# 备份原配置
cp docker-compose.yml docker-compose.yml.bak

# 使用修复后的配置
cp docker-compose-fixed.yml docker-compose.yml
```

### 3. 检查前端文件
确保前端文件已存在：
```bash
ls -la nginx/html/Blog-Web/dist/
ls -la nginx/html/Blog-Admin/dist/
```

如果不存在，上传构建好的前端文件：
```bash
# 在本地执行
scp -r blog-web/dist root@服务器IP:/home/DockerServes/nginx/html/Blog-Web/
scp -r blog-admin/dist root@服务器IP:/home/DockerServes/nginx/html/Blog-Admin/
```

### 4. 启动服务
```bash
docker-compose up -d

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 博客前台 | http://服务器IP | 端口 80 |
| 管理后台 | http://服务器IP:9001 | 端口 9001 |
| 后端API | http://服务器IP:8800 | 端口 8800 |
| RabbitMQ | http://服务器IP:15672 | 端口 15672 (admin/admin) |

## 常见问题

### 1. MySQL 启动失败
```bash
# 检查目录权限
ls -la mysql/data/

# 如果权限错误，修复权限
sudo chown -R 999:999 mysql/data/
```

### 2. 前端页面 404
```bash
# 检查前端文件是否存在
ls -la nginx/html/Blog-Web/dist/
ls -la nginx/html/Blog-Admin/dist/

# 如果不存在，创建测试页面
mkdir -p nginx/html/Blog-Web/dist
echo "<h1>Blog Web</h1>" > nginx/html/Blog-Web/dist/index.html

mkdir -p nginx/html/Blog-Admin/dist
echo "<h1>Blog Admin</h1>" > nginx/html/Blog-Admin/dist/index.html
```

### 3. 后端无法连接 MySQL
```bash
# 检查容器名是否一致
docker-compose logs myapp | grep -i "connection refused"

# 检查网络
docker network ls
docker network inspect iotdb
```

## 安全建议

1. **修改默认密码**
   - MySQL: `root123` → 强密码
   - RabbitMQ: `admin/admin` → 强密码

2. **删除 skip-grant-tables**
   - 已修复，不再使用免密登录

3. **配置防火墙**
```bash
# 仅开放必要端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --permanent --add-port=8800/tcp
firewall-cmd --permanent --add-port=9001/tcp
firewall-cmd --reload

# 关闭不必要的外部访问
# MySQL 3306 和 Redis 6379 建议仅限内部访问
```

## 文件变更记录

| 文件 | 变更 |
|------|------|
| init-volumes.sh | UTF-8 编码，删除 skip-grant-tables |
| nginx/conf.d/default.conf | UTF-8 编码，统一代理地址 |
| docker-compose-fixed.yml | 创建新文件，修复配置问题 |
