# Testing with Containers

[Workshop exercises -> here](EXERCISES.md)

This is a sample project for the _Testing with Containers_ workshop. It contains two sample applications, one is a
reference implementation using Testcontainers' generic containers to orchestrate the _todo-list-gateway_ setup and the
docker compose containers to start a more complex environment for the tests.

Software requirements to run the samples are `maven`, `openjdk-1.8` (or any other 1.8 JDK) and `docker`.
When running the Maven lifecycleit will create the war package and use the `liberty-maven-plugin` to create a runnable 
jar (fatjar) which contains thetodo-service and the Open Liberty application server. The fatjar will be copied into a
Docker image using Spotify's `dockerfile-maven-plugin` during the package phase.

## How to run

Before running the application it needs to be compiled and packaged using Maven. It creates the required war,
jar and Docker image and can be run `todo-list-service` together with its required database via `docker-compose`:

```shell script
$ cd todo-list/todo-list-service
$ mvn clean package
$ docker-compose up
```

When changing code you must re-run the package process and start docker-compose with the additional build parameter to
ensure that both the application and the Docker image is up-to-date:
```shell script
$ mvn clean package
$ docker-compose up --build
```

Wait for a message log similar to this:

> service_1   | [3/5/20 16:36:02:145 UTC] 0000001b id=         com.ibm.ws.kernel.feature.internal.FeatureManager            A CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in 13.006 seconds.

If everything worked you can access the Swagger ui via http://localhost:9080/openapi/ui.

To run the `todo-list-gateway` you need to start it manually via Docker after building it:

```shell script
$ cd todo-list/todo-list-gateway
$ mvn clean package
$ docker run --rm -p 19080:19080 testing-with-containers/todo-list-gateway:0
```

*Both examples may require `sudo` on Linux machines.*

## Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep testing-with-containers
```

If there are containers remaining although the application has been stopped you can remove them:

````shell script
$ docker rm <ids of the containers>
````

## Remarks on the code

There are two samples, `hello-world` which is a minimal project intended for the exercises. The second project 
`todo-list` contains two services: `todo-list-gateway` and `todo-list-service`. The gateway is a service which relays
incoming requests to the `todo-list-service`. It represents a _backend for frontend_. Its target url for the service is
configured in the `src/main/resources/META-INF/microprofile-config.properties`.
The `todo-list-service` contains a MicroProfile-based project that provides a REST interface to interact with. It can 
list, create, update and delete to-dos which are saved in an external PostgreSQL database. 