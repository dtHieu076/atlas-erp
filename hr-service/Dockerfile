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

# 1. Cài đặt postgresql-client để chạy pg_dump
# 2. Cài tzdata để đồng bộ múi giờ
RUN apk update && apk add --no-cache postgresql-client tzdata
ENV TZ=Asia/Ho_Chi_Minh

COPY certs/ ./certs/

# Tải Agent trực tiếp từ GitHub (Dùng bản OpenTelemetry chuẩn)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy file .jar từ Stage 1 sang Stage 2 và đổi tên thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Expose port mặc định của Spring Boot
EXPOSE 8080

ENTRYPOINT ["java", \ 
    "-javaagent:/app/opentelemetry-javaagent.jar",  \
    "-Dotel.service.name=hr-service",  \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp",\  
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09",  \
    "-Dotel.exporter.otlp.protocol=http/protobuf",  \
    "-Dotel.logs.exporter=otlp",  \
    "-Dotel.metrics.exporter=otlp",  \
    "-Dotel.instrumentation.logback-appender.enabled=true",  \
    "-Dotel.metric.export.interval=30000",  \
    "-Dotel.exporter.otlp.timeout=30000",  \
    "-jar", "app.jar"]  