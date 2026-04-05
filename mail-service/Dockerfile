# ==========================================
# STAGE 1: Builder (Biên dịch AOT)
# ==========================================
# FROM ghcr.io/graalvm/native-image-community:17-ol9 AS builder
FROM ghcr.io/graalvm/native-image-community:25-ol9 AS builder

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
# Lưu ý: Yêu cầu Spring Boot 3.x và phải có profile native
RUN ./mvnw clean native:compile -Pnative -DskipTests

# ==========================================
# STAGE 2: Production (Image siêu nhẹ)
# ==========================================
FROM ubuntu:22.04

# Cài đặt wget để dùng cho healthcheck, sau đó dọn dẹp cache
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Tạo user và group mailapp
RUN groupadd -g 1000 mailapp && \
    useradd -u 1000 -g mailapp -s /bin/bash -m mailapp

WORKDIR /app

# Copy file thực thi (binary) từ builder. 
# CHÚ Ý: Tên file thường trùng với tên artifactId trong pom.xml. 
# Giả sử artifactId của bạn là 'mail-service'.
COPY --from=builder /app/target/mail-service ./mail-service

# Copy certs (Kafka SSL)
COPY certs ./certs

# Cấp quyền sở hữu
RUN chown -R mailapp:mailapp /app

# Chuyển sang user bảo mật
USER mailapp

EXPOSE 8080

# Chuyển các tham số của Agent cũ thành Biến Môi Trường (Environment Variables)
ENV OTEL_SERVICE_NAME="mail-service"
ENV OTEL_EXPORTER_OTLP_ENDPOINT="https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp"
ENV OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09"
ENV OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"
ENV OTEL_METRICS_EXPORTER="otlp"
ENV OTEL_LOGS_EXPORTER="otlp"

# Healthcheck (Thời gian start-period có thể giảm xuống rất thấp vì AOT khởi động cực nhanh)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# ENTRYPOINT bây giờ gọi thẳng file hệ thống, KHÔNG dùng lệnh "java -jar"
ENTRYPOINT ["/app/mail-service"]