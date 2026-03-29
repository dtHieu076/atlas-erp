# Dockerfile Implementation Plan for user-activity-service

## Steps to Complete:
- [x] 1. Create Dockerfile with multi-stage Maven build (Java 17, Spring Boot fat JAR, include certs/)
- [ ] 2. Build the Docker image: `docker build -t user-activity-service .`
- [ ] 3. Run the container with external MongoDB/Kafka env vars: `docker run -p 8080:8080 ...`
- [ ] 4. Test health endpoint: `curl http://localhost:8080/actuator/health`
- [ ] 5. Optional: Create .dockerignore and docker-compose.yml

**Status:** Dockerfile created. Proceed to build and test.
