FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/publicaciones-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} publicaciones.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "publicaciones.jar"]
