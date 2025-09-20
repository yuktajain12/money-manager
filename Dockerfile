# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy all project files
COPY . .

# Build the JAR using Maven wrapper and skip tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/*SNAPSHOT.jar moneymanager-v1.0.jar

# Expose the port your Spring Boot app runs on (match your server.port)
EXPOSE 8080

# Set prod profile explicitly via environment variable inside container
ENV SPRING_PROFILES_ACTIVE=prod

# Run the JAR
ENTRYPOINT ["java", "-jar", "moneymanager-v1.0.jar"]
