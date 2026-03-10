# 使用官方Java 8镜像
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 安装必要的工具（用于健康检查）
RUN apk add --no-cache curl

# 创建必要的目录
RUN mkdir -p /app/upload /app/logs

# 复制构建好的JAR包到镜像中
# 注意：构建时需要先打包JAR文件，命名为 SHANG-BLOG.jar
COPY SHANG-BLOG.jar /app/SHANG-BLOG.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 暴露应用端口
EXPOSE 8800

# 定义启动命令
CMD ["java", "-jar", "-Dspring.profiles.active=pro", "/app/SHANG-BLOG.jar"]