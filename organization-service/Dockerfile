# Stage 1: Build file .jar
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Tận dụng cache của Docker cho các dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng với JRE siêu nhẹ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Tải Agent trực tiếp từ GitHub (Dùng bản OpenTelemetry chuẩn)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy file jar (lúc này tên sẽ là organization-service-0.0.1-SNAPSHOT.jar) và đổi thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 (hoặc nhận từ biến môi trường của Render)
EXPOSE 8080

ENTRYPOINT ["java", \ 
    "-javaagent:/app/opentelemetry-javaagent.jar",  \
    "-Dotel.service.name=organization-service",  \
    "-Dotel.exporter.otlp.endpoint=https://otlp-gateway-prod-ap-southeast-1.grafana.net/otlp",\  
    "-Dotel.exporter.otlp.headers=Authorization=Basic MTU0NDc5MTpnbGNfZXlKdklqb2lNVFk0TmpJMU5TSXNJbTRpT2lKemRHRmpheTB4TlRRME56a3hMVzkwWld3dFlYQndMV3BoZG1FdGFXNTBaV2R5WVhScGIyNGlMQ0pySWpvaWNEbDROamRNVWtGWmJEa3hPSGM0UnpkUVZqZHpiVEZGSWl3aWJTSTZleUp5SWpvaWNISnZaQzFoY0MxemIzVjBhR1ZoYzNRdE1TSjlmUT09",  \
    "-Dotel.exporter.otlp.protocol=http/protobuf",  \
    "-Dotel.logs.exporter=otlp",  \
    "-Dotel.metrics.exporter=otlp",  \
    "-Dotel.instrumentation.logback-appender.enabled=true",  \
    "-Dotel.metric.export.interval=30000",  \
    "-Dotel.exporter.otlp.timeout=30000",  \
    "-jar", "app.jar"] 