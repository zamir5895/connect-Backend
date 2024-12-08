FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/places-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} places.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "places.jar"]
