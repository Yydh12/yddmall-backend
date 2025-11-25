# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# 仅复制必要文件以利用 Docker 缓存
COPY pom.xml .
COPY src ./src

# 打包 JAR（跳过测试以加快构建）
RUN mvn -DskipTests clean package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Render 会注入 PORT；应用已在 application.yml 读取该变量
ENV PORT=8080
# 上传目录默认使用持久磁盘挂载路径
ENV UPLOAD_BASE_PATH=/data/uploads/images

# 更稳妥地复制打包后的 JAR
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
