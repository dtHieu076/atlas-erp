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

# Tải Agent trực tiếp từ GitHub (Dùng bản OpenTelemetry chuẩn)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy jar từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Port mặc định Spring Boot
EXPOSE 8080

ENTRYPOINT ["java", \ 
    "-javaagent:/app/opentelemetry-javaagent.jar",  \
    "-Dotel.service.name=workflow-service",  \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp",\  
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09",  \
    "-Dotel.exporter.otlp.protocol=http/protobuf",  \
    "-Dotel.logs.exporter=otlp",  \
    "-Dotel.metrics.exporter=otlp",  \
    "-Dotel.instrumentation.logback-appender.enabled=true",  \
    "-Dotel.metric.export.interval=30000",  \
    "-Dotel.exporter.otlp.timeout=30000",  \
    "-Dspring.cloud.config.enabled=false",  \
    "-jar", "app.jar"] 