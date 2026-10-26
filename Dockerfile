# ==========================================
# Frontend Stage (compile TypeScript)
# ==========================================
FROM node:22-alpine AS frontend
WORKDIR /web
COPY package.json package-lock.json ./
RUN npm ci
COPY tsconfig.json ./
COPY src/main/ts ./src/main/ts
RUN npm run build

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

# Pre-download dependencies
RUN ./gradlew dependencies --no-daemon

# Copy source code, then overlay the compiled JS from the frontend stage
COPY src src
COPY --from=frontend /web/src/main/resources/static/js/map.js src/main/resources/static/js/map.js

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

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=40", "-jar", "app.jar"]