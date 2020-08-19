FROM openjdk:13
COPY /target/courier-docker.jar /usr/build/courier-docker.jar
EXPOSE 8080
WORKDIR /usr/build
CMD ["java", "-jar", "courier-docker.jar"]
