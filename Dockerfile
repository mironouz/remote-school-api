FROM openjdk:14
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]