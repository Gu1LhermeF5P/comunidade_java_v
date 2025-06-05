
FROM maven:3.9-eclipse-temurin-17 AS builder


WORKDIR /app


COPY pom.xml .


RUN mvn dependency:go-offline -B


COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim

WORKDIR /app

ARG JAR_NAME=comunidade-java-0.0.1-SNAPSHOT.jar

COPY --from=builder /app/target/${JAR_NAME} app.jar

EXPOSE 8080 

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

