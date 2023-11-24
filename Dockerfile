FROM openjdk:19-jdk-alpine
WORKDIR /dermahelp
VOLUME /main-app
ADD target/dermahelp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/dermahelp/app.jar" ]