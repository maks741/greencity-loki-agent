FROM maven:3.9.9-eclipse-temurin-21 AS build

COPY pom.xml pom.xml

RUN ["mvn", "dependency:go-offline"]

COPY src ./src

RUN ["mvn", "package", "-Dmaven.test.skip=true"]


FROM eclipse-temurin:21-jre

COPY --from=build ./target/greencity-loki-agent-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]