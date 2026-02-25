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

# Copy file jar (lúc này tên sẽ là organization-service-0.0.1-SNAPSHOT.jar) và đổi thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Mở cổng 8080 (hoặc nhận từ biến môi trường của Render)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]