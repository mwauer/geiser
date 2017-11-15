FROM openjdk:8-jdk-alpine
VOLUME /tmp

# copy spring boot fat jar into container
ADD target/geiser-sparql-feed-service.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]