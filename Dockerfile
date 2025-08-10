# Dockerfile for SpringBoot AuthBackend with Ultra-Secure JWT
# Using pre-built JAR for production deployment
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Set working directory
WORKDIR /app

# Copy pre-built JAR from target directory
COPY target/authbackend-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port 8080
EXPOSE 8080

# Set Spring profile for Docker
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]