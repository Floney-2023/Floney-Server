# Stage 1: Build
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# Copy Gradle files for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build application
RUN gradle clean build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create log directories
RUN mkdir -p /app/log/info /app/log/warn /app/log/error /app/backup/info /app/backup/warn /app/backup/error

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# JVM memory optimization for 1GB instance (blue-green 동시 실행 고려)
# Heap: 320MB, Metaspace: 128MB, SerialGC (저메모리 환경에 적합)
ENV JAVA_OPTS="-Xms128m -Xmx320m -XX:MaxMetaspaceSize=128m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=10s --timeout=3s --start-period=120s --retries=5 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health-check || exit 1

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
