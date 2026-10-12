
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw && \
  ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S epharmgroup && \
    adduser -S -G epharmgroup epharmuser

COPY --from=build --chown=epharmuser:epharmgroup /app/target/*.jar app.jar

USER epharmuser:epharmgroup

EXPOSE 8082

ENTRYPOINT ["java","-jar","app.jar"]
