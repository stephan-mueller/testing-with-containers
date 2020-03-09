# Exercises for the _Testing with Containers_ workshop

This is an overview of the exercises for the workshop. There are several exercises spread across the source files.
You can find all exercises when you do a fulltext search for `EXERCISE`. There is also an overview below.

## Overview

1. [EXERCISE 1: HelloWorld integration test with testcontainer on-the-fly (JUnit 5)](hello-world/src/test/java/de/openknowledge/projects/helloworld/HelloWorldResourceIT.java)
2. [EXERCISE 2: HelloWorld cucumber test with manual container management (JUnit 4)](hello-world/src/test/java/de/openknowledge/projects/helloworld/HelloWorldResourceCucumberIT.java)
3. [EXERCISE 3: DbUnit persistence test with Postgres DB (JUnit 4)](todo-list/todo-list-service/src/test/java/de/openknowledge/projects/todolist/service/domain/TodoRepositoryIT.java)
4. [EXERCISE 4: Todo-List integration test with two testcontainers (JUnit 4)](todo-list/todo-list-service/src/test/java/de/openknowledge/projects/todolist/service/application/TodoResourceIT.java)
5. [EXERCISE 5: Todo-List integration test with DockerCompose (JUnit 4)](todo-list/todo-list-service/src/test/java/de/openknowledge/projects/todolist/service/application/TodoResourceIT.java)
6. [EXERCISE 6: TodoGatewayResource integration test with Mockserver (JUnit 5)](todo-list/todo-list-gateway/src/test/java/de/openknowledge/projects/todolist/gateway/application/TodoGatewayResourceIT.java)

### Exercise 1

The exercise can be found in [HelloWorldResourceIT.java](hello-world/src/test/java/de/openknowledge/projects/helloworld/HelloWorldResourceIT.java).

Todos:
1. add `@Testcontainers` annotation to test class
2. add `GenericContainer` with `ImageFromDockerfile` & use `DockerfileBuilder`
3. add log consumer to receive container logs
4. get host and port from container

### Exercise 2

The exercise can be found in [HelloWorldResourceCucumberIT](hello-world/src/test/java/de/openknowledge/projects/helloworld/HelloWorldResourceCucumberIT.java)
while the implementation has to be done in [hello-world/Dockerfile](hello-world/Dockerfile) and in
[HelloWorldResourceCucumberTestContainerBaseClass.java](hello-world/src/test/java/de/openknowledge/projects/helloworld/HelloWorldResourceCucumberTestContainerBaseClass.java)

Todos:
1. prepare `Dockerfile`
2. add `GenericContainer` with `ImageFromDockerfile`
3. call start/stop
4. get host and port from container

### Exercise 3

The exercise can be found in [TodoRepositoryIT.java](todo-list/todo-list-service/src/test/java/de/openknowledge/projects/todolist/service/domain/TodoRepositoryIT.java).
The [schema for the tables](todo-list/todo-list-service/src/main/resources/docker/1-schema.sql) can be found in in the 
[resources/docker](todo-list/todo-list-service/src/main/resources/docker/) folder of the _todo-list-service_. 

Todos:
1. add `FixedHostPortGenericContainer` with postgres image
2. set environment variables for database configuration (database, user, password)
3. add DDL script
4. replace `FixedHostPortGenericContainer` by `GenericContainer`
5. override JDBC Url

### Exercise 4

The exercise can be found in [TodoResourceIT.java](todo-list/todo-list-service/src/test/java/de/openknowledge/projects/todolist/service/application/TodoResourceIT.java).

**Note**: Exercise 5 is also in the same file. 

Todos:
1. add `Network` to link the two testcontainers
2. add `FixedHostGenericContainer` with postgres image (name = database)
3. add `GenericContainer` with _todo-list-service_ image (name = service)
4. get host and port from container

### Exercise 5

The exercise can be found in [TodoResourceIT.java](todo-list/todo-list-service/src/test/java/de/openknowledge/projects/todolist/service/application/TodoResourceIT.java).

Todos:
5. set up `DockerCompose` Container
6. replace `GenericContainer` with `DockerComposeContainer`
7. override JDBC Url
8. get host and port from container

### Exercise 6

The exercise can be found in [TodoGatewayResourceIT.java](todo-list/todo-list-gateway/src/test/java/de/openknowledge/projects/todolist/gateway/application/TodoGatewayResourceIT.java).

Todos:
1. add `@Testcontainers` annotation to test class
2. add `Network` to link the two testcontainers
3. add `MockServerContainer`
4. add `GenericContainer` with _todo-list-gateway_ image
5. get host and port from gateway container