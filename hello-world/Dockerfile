# HOWTO:
#
# 1. define Docker base image
# 2. copy runnable jar to /opt
# 3. define expose port
# 4. define entry point that starts the runnable jar
#
# HINT 1: use docker image "openjdk:8-jre"
# HINT 2: add JAVA_OPTS "-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"
#
FROM openjdk:8-jre

COPY target/hello-world.jar /opt/hello-world.jar

EXPOSE 9080
ENTRYPOINT exec java -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -jar /opt/hello-world.jar
