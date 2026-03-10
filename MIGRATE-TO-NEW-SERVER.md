# 博客项目迁移到新服务器指南

将你的博客从旧服务器迁移到 `/home/DockerServes/blog/` 目录。

## 迁移前检查清单

- [ ] 新服务器已安装 Docker 和 Docker Compose
- [ ] 新服务器防火墙已开放端口：8800, 9001, 9003
- [ ] 确认新服务器端口未被占用：`netstat -tlnp | grep -E "8800|9001|9003"`

---

## 第一步：旧服务器 - 打包数据

SSH 登录旧服务器，执行：

```bash
cd /root/shang/docker  # 或你的项目路径
chmod +x migrate-to-new-server.sh
./migrate-to-new-server.sh
```

这会生成 `/tmp/shang-blog-migration-YYYYMMDD-HHMMSS.tar.gz`

---

## 第二步：传输到新服务器

在旧服务器执行：

```bash
scp /tmp/shang-blog-migration-*.tar.gz root@新服务器IP:/home/DockerServes/
```

或者使用 any 其他传输方式（FTP、宝塔面板等）

---

## 第三步：新服务器 - 部署

SSH 登录新服务器，依次执行：

```bash
cd /home/DockerServes

# 1. 解压迁移包
mkdir -p blog
tar -xzf shang-blog-migration-*.tar.gz -C blog/
cd blog

# 2. 解压代码
ls -la  # 确认有这些压缩包
tar -xzf blog-source.tar.gz
tar -xzf blog-web.tar.gz
tar -xzf blog-admin.tar.gz
tar -xzf docker-config.tar.gz

# 3. 安装 Maven
yum install -y maven  # CentOS
# 或
apt-get install -y maven  # Ubuntu

# 4. 构建后端 JAR
cd blog
mvn clean package -DskipTests -q
cp shang-server/target/*.jar SHANG-BLOG.jar
cd ..

# 5. 准备 Docker 数据目录
cd docker
mkdir -p data/mysql/{data,conf,logs,backup}
mkdir -p data/redis/conf
mkdir -p data/upload
mkdir -p data/logs

# 6. 恢复上传的文件
if [ -f "../upload-files.tar.gz" ]; then
    tar -xzf ../upload-files.tar.gz -C data/
fi

# 7. 启动服务
docker-compose up -d

# 8. 等待 MySQL 启动并导入数据
echo "等待 MySQL 启动..."
sleep 30

# 导入数据库
docker exec -i shang-blog-mysql mysql -uroot -proot123 -e "CREATE DATABASE IF NOT EXISTS \`Shang-Blog\`;"
docker exec -i shang-blog-mysql mysql -uroot -proot123 Shang-Blog < ../database.sql

# 9. 验证
docker-compose ps
echo ""
echo "测试访问:"
curl -s http://localhost:8800/actuator/health && echo " ✓ 后端正常"
```

---

## 第四步：防火墙配置

```bash
# CentOS/RHEL
firewall-cmd --permanent --add-port=8800/tcp
firewall-cmd --permanent --add-port=9001/tcp
firewall-cmd --permanent --add-port=9003/tcp
firewall-cmd --reload

# Ubuntu/Debian
ufw allow 8800/tcp
ufw allow 9001/tcp
ufw allow 9003/tcp
ufw reload
```

---

## 第五步：验证

浏览器访问：
- 博客前台：`http://新服务器IP:9001`
- 管理后台：`http://新服务器IP:9003`
- 账号：test，密码：123456

---

## 端口冲突检查

如果启动失败，检查端口是否被占用：

```bash
# 查看端口占用
netstat -tlnp | grep -E "8800|9001|9003"

# 如果被占用，修改 docker-compose.yml 中的端口映射
# 例如将 8800:8800 改为 8801:8800
```

---

## 网络共存说明

与现有 Dify 服务共存：
- Blog MySQL 使用端口 3307（Dify 用 3306）
- Blog Redis 使用端口 6380（Dify 用 6379）
- Blog 使用独立 Docker 网络 `shang-blog-network`（子网 172.21.0.0/24）

不会与现有服务冲突。

---

## 快速命令速查

```bash
# 查看日志
cd /home/DockerServes/blog/docker
docker-compose logs -f backend
docker-compose logs -f mysql

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 完全删除（包括数据）
docker-compose down -v
rm -rf data/mysql/data

# 进入 MySQL
docker exec -it shang-blog-mysql mysql -uroot -proot123

# 备份数据库
docker exec shang-blog-mysql mysqldump -uroot -proot123 Shang-Blog > backup.sql
```

---

## 迁移后配置更新（如需要）

如果新服务器 IP/域名变更，更新以下配置：

1. **第三方登录回调 URL**（QQ/微博/Gitee/GitHub）
   - 编辑：`blog/shang-server/src/main/resources/application.yml`

2. **七牛云存储域名**
   - 编辑：`blog/shang-server/src/main/resources/application-dev.yml`

3. **重新构建部署**
   ```bash
   cd /home/DockerServes/blog/blog
   mvn clean package -DskipTests
   cp shang-server/target/*.jar SHANG-BLOG.jar
   cd ../docker
   docker-compose up -d --build backend
   ```
