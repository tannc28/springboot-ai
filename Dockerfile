# Build stage
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /app

# Update certificates and repositories
RUN apk update && \
    apk upgrade && \
    apk add --no-cache ca-certificates

COPY pom.xml .
COPY src src
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Make mvnw executable and build
RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"] 