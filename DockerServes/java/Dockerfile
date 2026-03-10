# 使用官方Java 8镜像
FROM  openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 创建文件上传目录并设置为卷（持久化存储）
VOLUME /app/upload

# 复制构建好的JAR包到镜像中
COPY SHANG-BLOG.jar /app/SHANG-BLOG.jar
# 暴露应用端口（根据实际端口修改）
EXPOSE 8800
# 定义启动命令
CMD ["java", "-jar", "/app/SHANG-BLOG.jar"]