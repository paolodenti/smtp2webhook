FROM amazoncorretto:17.0.8 AS build

WORKDIR /app

COPY .. .

RUN ./mvnw --no-transfer-progress clean package -DskipTests=true -Dcheckstyle.skip=true -P-testtools

FROM amazoncorretto:17.0.8

COPY --from=build /app/target/*.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
