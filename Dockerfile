FROM maven:3.9.9-eclipse-temurin-23 AS build

COPY pom.xml pom.xml

RUN ["mvn", "dependency:go-offline"]

COPY src ./src

RUN ["mvn", "package", "-Dmaven.test.skip=true"]


FROM eclipse-temurin:23-jre

COPY --from=build ./target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]