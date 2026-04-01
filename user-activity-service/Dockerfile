# Multi-stage Dockerfile for Spring Boot Maven app (user-activity-service)
# Java 17, optimized layers, includes Kafka SSL certs

# Stage 1: Build fat JAR with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build (skip tests for faster build)
COPY src ./src
RUN mvn clean package -DskipTests -Dspring-boot.repackage.skip=false

# Stage 2: Runtime image (JRE Alpine for smaller size)
FROM eclipse-temurin:17-jre-alpine

# Install tini for proper signal handling (PID 1)
RUN apk add --no-cache tini

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup

WORKDIR /app

# 🟢 THÊM MỚI: Tải OpenTelemetry Agent trực tiếp từ GitHub
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy JAR and certs from builder
COPY --from=builder /app/target/*.jar app.jar
COPY certs/ ./certs/

# 🟢 THÊM MỚI: Đổi quyền sở hữu toàn bộ thư mục /app (bao gồm agent, jar, certs) cho appuser
RUN chown -R appuser:appgroup /app

# Chuyển sang user non-root sau khi đã setup xong file
USER appuser

# Expose Spring Boot port
EXPOSE 8080

# Healthcheck (assumes Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Use tini, run JAR with JVM opts and OpenTelemetry Agent
ENTRYPOINT ["/sbin/tini", "--"]
CMD ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-javaagent:/app/opentelemetry-javaagent.jar", \
    "-Dotel.service.name=user-activity-service", \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp", \
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09", \
    "-Dotel.exporter.otlp.protocol=http/protobuf", \
    "-Dotel.logs.exporter=otlp", \
    "-Dotel.metrics.exporter=otlp", \
    "-Dotel.instrumentation.logback-appender.enabled=true", \
    "-Dotel.metric.export.interval=30000", \
    "-Dotel.exporter.otlp.timeout=30000", \
    "-jar", "app.jar"]