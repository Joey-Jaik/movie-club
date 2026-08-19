# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven

COPY . .
RUN cd backend && mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/backend/target/backend-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]