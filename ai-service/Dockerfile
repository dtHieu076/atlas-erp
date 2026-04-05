# ==========================================
# STAGE 1: Builder (Biên dịch AOT)
# ==========================================
FROM ghcr.io/graalvm/native-image-community:21-ol9 AS builder

WORKDIR /app

# Copy Maven wrapper và pom.xml (Tận dụng cache)
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Cấp quyền và tải dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

# Copy toàn bộ source code
COPY src ./src

# Biên dịch ra Native Image (AOT)
# Lưu ý: Yêu cầu Spring Boot 3.x/4.x và phải có profile native
RUN ./mvnw clean native:compile -Pnative -DskipTests

# ==========================================
# STAGE 2: Production (Image siêu nhẹ)
# ==========================================
FROM ubuntu:22.04

# Cài đặt wget (cho healthcheck) và ca-certificates (bắt buộc để gọi API HTTPS ra LLM)
RUN apt-get update && apt-get install -y wget ca-certificates && rm -rf /var/lib/apt/lists/*

# Tạo user và group bảo mật
RUN groupadd -g 1000 aiapp && \
    useradd -u 1000 -g aiapp -s /bin/bash -m aiapp

WORKDIR /app

# Copy file thực thi (binary) từ builder. 
# CHÚ Ý: Đảm bảo tên file trùng với artifactId của bạn.
COPY --from=builder /app/target/ai-service ./ai-service

# Cấp quyền sở hữu
RUN chown -R aiapp:aiapp /app

# Chuyển sang user bảo mật
USER aiapp

EXPOSE 8080

# Chuyển cấu hình Java Agent thành Biến môi trường tiêu chuẩn của OpenTelemetry
ENV OTEL_SERVICE_NAME="ai-service" \
    OTEL_EXPORTER_OTLP_ENDPOINT="https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp" \
    OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09" \
    OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf" \
    OTEL_LOGS_EXPORTER="otlp" \
    OTEL_METRICS_EXPORTER="otlp" \
    OTEL_INSTRUMENTATION_LOGBACK_APPENDER_ENABLED="true" \
    OTEL_METRIC_EXPORT_INTERVAL="30000" \
    OTEL_EXPORTER_OTLP_TIMEOUT="30000"

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Chạy file thực thi trực tiếp
ENTRYPOINT ["/app/ai-service"]