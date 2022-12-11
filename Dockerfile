FROM openjdk:11
# Add Maintainer Info
LABEL maintainer="tilsizoglou@protonmail.com"
# Add a volume pointing to /tmp
VOLUME /tmp
# Make port 8080 available to the world outside this container
EXPOSE 8080
# The application's jar file
ARG JAR_FILE=target/metricsCalculator-1.0.0.jar
# Add the application's jar to the container
ADD ${JAR_FILE} metricsCalculator-1.0.0.jar
# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/metricsCalculator-1.0.0.jar"]
