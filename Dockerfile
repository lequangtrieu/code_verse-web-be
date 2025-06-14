# -------------------------
# Stage 1: Build .jar file
# -------------------------
FROM gradle:8.4-jdk17 AS builder
WORKDIR /app

# Copy everything (excluding files in .dockerignore)
COPY . .

# Build JAR without running tests
RUN gradle build -x test

# ----------------------------
# Stage 2: Run Spring Boot App
# ----------------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copy .jar from builder
COPY --from=builder /app/build/libs/code-verse_web_be-0.0.1-SNAPSHOT.jar app.jar

# Expose port (optional, for local dev)
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]