# app build
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# runnable image
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
WORKDIR /app

# Copy of generated jar
COPY --from=build /app/target/*.jar app.jar

# Copy of keystore SSL
COPY src/main/resources/keystore.jks keystore.jks

EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]
