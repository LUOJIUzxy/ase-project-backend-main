FROM maven:3.8.4-openjdk-17

COPY ./ /app
WORKDIR /app

RUN mvn clean install

ENTRYPOINT ["java", "-jar", "./target/dispatcher-service-0.0.1-SNAPSHOT.jar"]