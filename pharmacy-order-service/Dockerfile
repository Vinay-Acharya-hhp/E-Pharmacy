
FROM eclipse-temurine:17-jdk-alpine AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

EXPOSE 8084

ENTRYPOINT ["java","-jar","app.jar"]
