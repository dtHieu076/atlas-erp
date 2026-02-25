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

# Copy file .jar từ Stage 1 sang Stage 2 và đổi tên thành app.jar cho dễ gọi
COPY --from=build /app/target/*.jar app.jar

# Expose port mặc định của Spring Boot
EXPOSE 8080

# Lệnh khởi chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]