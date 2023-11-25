FROM maven:3.8.4-openjdk-17

COPY ./ /app
WORKDIR /app

#RUN mvn clean install

ENTRYPOINT ["java", "-jar", "./target/service-registry-1.0-SNAPSHOT.jar"]

EXPOSE 8761
