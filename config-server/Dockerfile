# Stage 1: Build ứng dụng bằng Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy file pom.xml và download dependencies (tận dụng docker cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy mã nguồn và đóng gói file jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Chạy ứng dụng bằng JRE nhẹ
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Docker sẽ lấy file .jar bất kỳ trong thư mục target và đổi tên thành app.jar
COPY --from=build /app/target/config-server-*.jar app.jar

# Mở cổng 8888 (cổng mặc định của Config Server)
EXPOSE 8888

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]