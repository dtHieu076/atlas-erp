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
USER appuser

WORKDIR /app

# Copy JAR and certs from builder
COPY --from=builder /app/target/*.jar app.jar
COPY --chown=appuser:appgroup certs/ ./certs/

# Expose Spring Boot port
EXPOSE 8080

# Healthcheck (assumes Spring Boot Actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Use tini, run JAR with JVM opts (adjust heap as needed)
ENTRYPOINT ["/sbin/tini", "--"]
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]

