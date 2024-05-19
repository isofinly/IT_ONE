# Use Maven and OpenJDK 17 for the build stage
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory for the build stage
WORKDIR /build

# Copy the project files into the build stage
COPY . /app

# Change to the app directory and build the project using Maven
WORKDIR /app
RUN mvn clean package

# Use GraalVM for the final stage
FROM ghcr.io/graalvm/graalvm-community:17

# Copy the built JAR file from the build stage to the final stage
COPY --from=build /app/target/restapi*.jar /app/app.jar
# COPY /certs /app/certs


# Set the default command to run the application
CMD ["java", "-jar", "/app/app.jar"]
