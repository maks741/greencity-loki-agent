FROM maven:3.9.9-eclipse-temurin-21 AS build

RUN ["git", "clone", "https://github.com/maks741/greencity-loki-agent.git", "./app"]

WORKDIR ./app

RUN ["mvn", "clean", "install"]


FROM eclipse-temurin:21-jre

COPY --from=build ./app/target/greencity-loki-agent-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]