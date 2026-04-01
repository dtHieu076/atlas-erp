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

# Đổi tên group và user thành 'mailapp'
RUN addgroup -g 1000 -S mailapp && \
    adduser -S mailapp -u 1000 -G mailapp

WORKDIR /app

# Copy JAR from builder
COPY --from=builder /app/target/*.jar app.jar

# Copy certs (Kafka SSL)
COPY certs ./certs

# 🟢 THÊM MỚI: Tải Agent trực tiếp từ GitHub 
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Cấp quyền sở hữu toàn bộ thư mục /app (bao gồm cả file app.jar và opentelemetry-javaagent.jar) cho user 'mailapp'
RUN chown -R mailapp:mailapp /app

# Chuyển sang chạy ứng dụng bằng user 'mailapp' để bảo mật
USER mailapp

# Expose port
EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 🟢 THÊM MỚI: Cập nhật ENTRYPOINT để nhúng OpenTelemetry Agent trỏ về Grafana
ENTRYPOINT ["java", \
    "-javaagent:/app/opentelemetry-javaagent.jar", \
    "-Dotel.service.name=mail-service", \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp", \
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09", \
    "-Dotel.exporter.otlp.protocol=http/protobuf", \
    "-Dotel.logs.exporter=otlp", \
    "-Dotel.metrics.exporter=otlp", \
    "-Dotel.instrumentation.logback-appender.enabled=true", \
    "-Dotel.metric.export.interval=30000", \
    "-Dotel.exporter.otlp.timeout=30000", \
    "-jar", "app.jar"]