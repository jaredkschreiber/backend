FROM openjdk:11
ADD apis/build/libs/apis-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]