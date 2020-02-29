FROM openjdk:8-jre

ARG JAR_FILE
ADD target/${JAR_FILE} /opt/todo-list-service.jar

ENV JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"

EXPOSE 9080
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/todo-list-service.jar
