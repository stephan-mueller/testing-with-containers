FROM openjdk:8-jre-alpine

ARG JAR_FILE
COPY target/${JAR_FILE} /opt/todo-list-gateway.jar

ENV JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"

ENV SERVICE_HOST="localhost"
ENV SERVICE_PORT="9080"

EXPOSE 9080
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/todo-list-gateway.jar -Dtodo-list-service.host=$SERVICE_HOST -Dtodo-list-service.port=$SERVICE_PORT
