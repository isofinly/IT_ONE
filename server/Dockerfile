FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /build

COPY . /app
WORKDIR /app
RUN mvn clean package

FROM ghcr.io/graalvm/graalvm-community:17
COPY --from=build /app/target/restapi*.jar /app.jar
CMD ["java", "-jar", "/app.jar"]