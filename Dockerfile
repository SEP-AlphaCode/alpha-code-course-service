# Stage 1: Build với Maven + generate gRPC code
FROM maven:3.9-eclipse-temurin-24 AS build

WORKDIR /app

# Copy pom và tải dependency offline
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src
COPY src/main/proto ./src/main/proto

# Build + generate gRPC classes
RUN mvn clean test package

# Stage 2: Run ứng dụng
FROM eclipse-temurin:24-jdk AS runtime

WORKDIR /app

COPY --from=build /app/target/alpha-code-course-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
