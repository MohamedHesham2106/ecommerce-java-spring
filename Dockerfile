FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app

# Copy the POM file to download dependencies
COPY pom.xml .
# Download all dependencies. This will be cached when no changes to pom.xml
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Run stage
FROM amazoncorretto:17-alpine
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
