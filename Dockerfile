FROM openjdk:8-jdk-alpine
COPY ./target/custom-alfresco-1.0-SNAPSHOT.jar /usr/src/alf/
WORKDIR /usr/src/alf
EXPOSE 8888
CMD ["java", "-jar", "custom-alfresco-1.0-SNAPSHOT.jar"]