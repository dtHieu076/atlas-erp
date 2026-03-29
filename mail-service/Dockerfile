FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for layer caching
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Set executable permissions
RUN chmod +x mvnw

# Download dependencies first (cache layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR (skip tests for faster build)
RUN ./mvnw clean package -DskipTests -Dmaven.javadoc.skip=true \
    --no-transfer-progress

# Production stage
FROM eclipse-temurin:17-jdk-alpine

# SỬA LỖI Ở ĐÂY: Đổi tên group và user từ 'mail' thành 'mailapp'
RUN addgroup -g 1000 -S mailapp && \
    adduser -S mailapp -u 1000 -G mailapp

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Copy certs (Kafka SSL) - mount volume in production for security
COPY certs ./certs

# SỬA LỖI Ở ĐÂY: Cấp quyền sở hữu thư mục cho user 'mailapp'
RUN chown -R mailapp:mailapp /app

# SỬA LỖI Ở ĐÂY: Chuyển sang chạy ứng dụng bằng user 'mailapp'
USER mailapp

# Expose port
EXPOSE 8080

# Healthcheck (optional, assumes actuator or simple TCP)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]