FROM openjdk:8-jdk-alpine
ADD target/*.jar app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=docker","/app.jar"]
