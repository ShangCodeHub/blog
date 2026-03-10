#!/bin/bash
# 设置变量
IMAGE_NAME="myapp"
CONTAINER_NAME="myapp"
NETWORK_NAME="iotdb"
IP_ADDRESS="172.20.0.11"
UPLOAD_DIR="/home/DockerServes/java/upload/"
APP_UPLOAD_DIR="/app/upload"
PORT="8800"
# 1. 停止容器（如果存在）
if [ "$(docker ps -f "name=${CONTAINER_NAME}" --format "{{.Status}}")" ]; then
    echo "Stopping container ${CONTAINER_NAME}..."
    docker stop ${CONTAINER_NAME}
fi
# 2. 删除容器（如果存在）
if [ "$(docker ps -a -f "name=${CONTAINER_NAME}" --format "{{.Status}}")" ]; then
    echo "Removing container ${CONTAINER_NAME}..."
    docker rm ${CONTAINER_NAME}
fi
# 3. 删除镜像（如果存在）
if [ "$(docker images -f "name=${IMAGE_NAME}" --format "{{.Repository}}")" ]; then
    echo "Removing image ${IMAGE_NAME}..."
    docker rmi ${IMAGE_NAME}
fi
# 4. 构建新的镜像
echo "Building new image ${IMAGE_NAME}..."
docker build -t ${IMAGE_NAME} .
# 5. 检查网络是否存在，若不存在则创建带 IP 的自定义网络
if [ "$(docker network ls -f "name=${NETWORK_NAME}" --format "{{.Name}}")" != "${NETWORK_NAME}" ]; then
    echo "Creating network ${NETWORK_NAME} with custom subnet and IP..."
    docker network create \
      --driver bridge \
      --subnet=172.20.0.0/24 \
      --gateway=172.20.0.1 \
      ${NETWORK_NAME}
fi
# 6. 运行新容器，挂载目录、分配网络和IP、开放端口
echo "Starting new container ${CONTAINER_NAME}..."
docker run -d \
  --name ${CONTAINER_NAME} \
  --network ${NETWORK_NAME} \
  --ip ${IP_ADDRESS} \
  -v ${UPLOAD_DIR}:${APP_UPLOAD_DIR} \
  -p ${PORT}:8800 \
  ${IMAGE_NAME}
echo "Deployment completed successfully!"