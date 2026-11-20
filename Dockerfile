# ==============================================================================
# Dockerfile for Spring Boot 3 & Java 21 on Render
# ==============================================================================

# Stage 1: Build the application using Maven and JDK 21
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production JAR (unit tests run during development/CI)
RUN mvn clean package -DskipTests

# Stage 2: Lightweight runtime environment with JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy compiled JAR from build stage
COPY --from=build /app/target/employee-management-system-1.0.0.jar app.jar

# Expose standard port (Render will dynamically assign PORT via environment variable)
EXPOSE 8080

# Launch application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
