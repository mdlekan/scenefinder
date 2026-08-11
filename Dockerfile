# ==========================================
# Build Stage
# ==========================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copy Gradle wrapper and configuration files for caching dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Grant execution permissions to Gradle wrapper
RUN chmod +x gradlew

# Pre-download dependencies (improves build caching)
RUN ./gradlew dependencies --no-daemon

# Copy source code and build application
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# ==========================================
# Runtime Stage
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]