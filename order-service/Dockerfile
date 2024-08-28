# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:22-jdk

# Set the working directory
WORKDIR /app

# Copy the project JAR file into the container at /app
COPY build/libs/order-service.jar /app/order-service-0.0.1-SNAPSHOT.jar

# Run the application
ENTRYPOINT ["java", "-jar", "order-service-0.0.1-SNAPSHOT.jar"]
