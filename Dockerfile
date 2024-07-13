FROM openjdk:22-jdk-slim
ARG JAR_FILE=target/Connect-0.0.1.jar
COPY ${JAR_FILE} app_connect.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app_connect.jar"]