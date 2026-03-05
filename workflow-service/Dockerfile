# ===============================
# STAGE 1: BUILD MAVEN PROJECT
# ===============================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Thư mục làm việc trong container
WORKDIR /app

# Copy pom trước để cache dependency
COPY pom.xml .

# Tải dependency
RUN mvn dependency:go-offline

# Copy toàn bộ source
COPY src ./src

# Build project
RUN mvn clean package -DskipTests


# ===============================
# STAGE 2: RUN SPRING BOOT APP
# ===============================
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Port mặc định Spring Boot
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java","-jar","app.jar"]