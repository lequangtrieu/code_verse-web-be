# -------------------------
# Stage 1: Build .jar file
# -------------------------
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# ----------------------------
# Stage 2: Run Spring Boot App
# ----------------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
