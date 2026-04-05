# Stage 1: Build Native Image bằng GraalVM JDK 25
FROM ghcr.io/graalvm/native-image-community:25-ol9 AS build
WORKDIR /app

# Cài đặt maven (vì base image này chỉ có JDK)
RUN microdnf install -y maven

# Copy pom.xml và tải dependencies trước
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code và build ra file nhị phân (AOT)
COPY src ./src
# Lệnh này sẽ kích hoạt GraalVM compiler thay vì chỉ đóng gói .jar
RUN mvn -Pnative native:compile -DskipTests

# Stage 2: Chạy ứng dụng (Dùng Oracle Linux Micro cho cực nhẹ và tương thích thư viện C)
# Lưu ý: Không dùng Alpine vì GraalVM mặc định build với glibc (Alpine dùng musl)
FROM oraclelinux:9-micro
WORKDIR /app

# Copy file thực thi nhị phân từ Stage 1 sang Stage 2
# LƯU Ý QUAN TRỌNG: Sửa 'ai-service' thành <artifactId> của bạn trong pom.xml
COPY --from=build /app/target/ai-service ./app

# Expose port mặc định của Spring Boot
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

# Chạy file thực thi trực tiếp (Không có lệnh java)
ENTRYPOINT ["/app/app"]