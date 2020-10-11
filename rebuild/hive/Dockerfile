# Maven build phase
FROM maven:3.6.3-jdk-13 as build
COPY pom.xml /tmp/
WORKDIR /tmp/
# Fetch dependencies listed in pom.xml
# Ran as separate step to optimise use of Docker cache
RUN mvn dependency:go-offline 
# Compile maven project
COPY /src/ /tmp/src/
RUN mvn clean install -DskipTests

# Runtime phase
FROM openjdk:13-alpine as runtime
COPY --from=build /tmp/target/hive.jar /app/hive.jar
EXPOSE 8080
WORKDIR /app/
ENTRYPOINT [ "java", "-jar", "chatterbox.jar" ]
