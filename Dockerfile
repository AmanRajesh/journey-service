# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy the pom.xml and download dependencies first (this caches them to speed up future builds)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and compile the .jar, skipping tests for faster builds
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled .jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port the Spring Boot app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]