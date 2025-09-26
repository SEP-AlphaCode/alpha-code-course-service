# Stage 1: Build với Maven
FROM maven:3.9-eclipse-temurin-24-alpine AS build

WORKDIR /app

# Copy pom.xml trước để cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source
COPY src ./src

# Build jar, skip tests
RUN mvn clean package -DskipTests

# Stage 2: Run ứng dụng với JDK
FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/alpha-code-course-service-0.0.1-SNAPSHOT.jar app.jar

# Port service (thay theo app config của bạn)
EXPOSE 8084

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]
