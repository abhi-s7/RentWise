# Dockerfile for RentWise Property Service
# This builds and runs the property management service

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set working directory inside container
WORKDIR /build

# Copy Maven wrapper files first (for better caching)
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Make mvnw executable (needed for Linux containers)
RUN chmod +x mvnw || true

# Copy pom.xml file
# This is done separately so Docker can cache dependencies
COPY pom.xml .

# Download dependencies (this layer is cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
# Use smaller JRE image (not full JDK) to reduce image size
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Create directory for logs
RUN mkdir -p /app/logs

# Copy the JAR file from build stage
COPY --from=builder /build/target/rentwise-property-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8082 (Property Service port)
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

