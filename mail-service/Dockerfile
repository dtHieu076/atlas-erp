# ==========================================
# STAGE 1: Builder (Biên dịch ra file JAR)
# ==========================================
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy Maven wrapper và pom.xml (Tận dụng cache)
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Cấp quyền và tải dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy toàn bộ source code và build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ==========================================
# STAGE 2: Production (Chạy trên JVM)
# ==========================================
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Cài đặt wget để dùng cho healthcheck, sau đó dọn dẹp cache
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Tạo user và group mailapp
RUN groupadd -g 1000 mailapp && \
    useradd -u 1000 -g mailapp -s /bin/bash -m mailapp

# Copy certs (Kafka SSL)
COPY certs ./certs

# Tải Agent trực tiếp từ GitHub (Dùng bản OpenTelemetry chuẩn)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy file JAR từ builder sang
COPY --from=builder /app/target/*.jar ./mail-service.jar

# Cấp quyền đọc file agent và sở hữu thư mục cho mailapp
RUN chmod 644 /app/opentelemetry-javaagent.jar && \
    chown -R mailapp:mailapp /app

# Chuyển sang user bảo mật
USER mailapp

EXPOSE 8080

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# ENTRYPOINT truyền tham số Java Agent y hệt như hr-service
ENTRYPOINT ["java", \ 
    "-javaagent:/app/opentelemetry-javaagent.jar",  \
    "-Dotel.service.name=mail-service",  \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp",\  
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09",  \
    "-Dotel.exporter.otlp.protocol=http/protobuf",  \
    "-Dotel.logs.exporter=otlp",  \
    "-Dotel.metrics.exporter=otlp",  \
    "-Dotel.instrumentation.logback-appender.enabled=true",  \
    "-Dotel.metric.export.interval=30000",  \
    "-Dotel.exporter.otlp.timeout=30000",  \
    "-jar", "mail-service.jar"]