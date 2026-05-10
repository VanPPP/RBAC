FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /w
COPY pom.xml /w/
COPY common /w/common
COPY user-service /w/user-service
COPY trip-service /w/trip-service
COPY notification-service /w/notification-service
RUN mvn -f /w/pom.xml -pl trip-service -am package -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /w/trip-service/target/trip-service-*.jar /app/app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
