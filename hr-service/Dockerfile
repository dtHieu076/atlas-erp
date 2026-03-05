# Stage 1: Build ứng dụng
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml và tải dependencies trước (Tận dụng cache của Docker giúp build lần sau nhanh hơn)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy toàn bộ source code và build ra file .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng (Dùng JRE Alpine cho siêu nhẹ)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Tải Agent trực tiếp từ GitHub vào Stage 2 (Dùng bản OpenTelemetry chuẩn)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy file .jar từ Stage 1 sang Stage 2 và đổi tên thành app.jar
COPY --from=build /app/target/*.jar app.jar

# --- Giữ nguyên các biến môi trường cấu hình cũ ---
ENV OTEL_SERVICE_NAME="hr-service"  
ENV OTEL_EXPORTER_OTLP_ENDPOINT="https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp"  
ENV OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"  
ENV OTEL_EXPORTER_OTLP_HEADERS="Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09"  
ENV SPRING_CLOUD_CONFIG_ENABLED=false  

# Expose port mặc định của Spring Boot
EXPOSE 8080

# Sử dụng tham số -D để ép cấu hình ngay khi chạy, tránh Agent nhận nhầm localhost
ENTRYPOINT ["java", \
    "-javaagent:/app/opentelemetry-javaagent.jar", \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp", \
    "-Dotel.exporter.otlp.protocol=http/protobuf", \
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09", \
    "-Dotel.service.name=hr-service", \
    "-Dotel.logs.exporter=none", \
    "-Dotel.metrics.exporter=none", \
    "-jar", "app.jar"]