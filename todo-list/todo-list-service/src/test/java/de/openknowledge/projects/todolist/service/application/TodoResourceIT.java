/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.projects.todolist.service.application;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;

import de.openknowledge.projects.todolist.service.AbstractIntegrationTest;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.specification.RequestSpecification;

/**
 * Integration test class for the resource {@link TodoResource}.
 *
 * EXERCISE 4: Todo-List integration test with two testcontainers (JUnit 4)
 *
 * HOWTO: 1. add Network to link the two testcontainers 2. add FixedHostGenericContainer with postgres image (name = database) 3. add
 * GenericContainer with todo-list-service image (name = service) 4. get host and port from container
 */

/**
 * EXERCISE 4: Todo-List integration test with two testcontainers (JUnit 4)
 *
 * HOWTO: 1. add Network to link the two testcontainers 2. add FixedHostGenericContainer with postgres image (name = database) 3. add
 * GenericContainer with todo-list-service image (name = service) 4. get host and port from container
 */
@RunWith(JUnit4.class)
@DBUnit(caseSensitiveTableNames = true, escapePattern = "\"?\"")
public class TodoResourceIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(TodoResourceIT.class);

  private static final long TODO_ID = 1L;
  private static final long DELETED_TODO_ID = 4L;
  private static final long UPDATED_TODO_ID = 2L;
  private static final long UNKNOWN_TODO_ID = 999L;

  /**
   * HOWTO:
   * 1. add Network to link the two testcontainers
   */
//  public static Network NETWORK = Network.newNetwork();

  /**
   * 2. add FixedHostGenericContainer with postgres image (name = database)
   * - add @ClassRule annotation
   * - instantiate GenericContainer with postgres image
   * - set expose port (5432)
   * - set NETWORK
   * - add NETWORK alias "database"
   * - add log consumer to receive container logs
   * - add WaitStrategy -> Wait.forLogMessage(".*server started.*", 1)
   */
//  @ClassRule
//  public static FixedHostPortGenericContainer<?> database = new FixedHostPortGenericContainer<>("postgres:12-alpine")
//      .withExposedPorts(5432)
//      .withFixedExposedPort(5432, 5432)
//      .withNetwork(NETWORK)
//      .withNetworkAliases("database")
//      .withEnv("POSTGRES_DB", "postgres")
//      .withEnv("POSTGRES_USER", "postgres")
//      .withEnv("POSTGRES_PASSWORD", "postgres")
//      .withCopyFileToContainer(MountableFile.forClasspathResource("docker/1-schema.sql"), "/docker-entrypoint-initdb.d/1-schema.sql")
//      .withLogConsumer(new Slf4jLogConsumer(LOG))
//      .waitingFor(
//          Wait.forLogMessage(".*server started.*", 1)
//      );

  /**
   * 3. add GenericContainer with todo-list-service image (name = service) - add @ClassRule annotation - instantiate GenericContainer with
   * service image - set expose port (9080) - set NETWORK - set depends on database container - add log consumer to receive container logs -
   * add WaitStrategy -> Wait.forLogMessage(".*server started.*", 1)
   *
   * HINT: use service image "testing-with-containers/todo-list-service:0" (requires to run "mvn clean package" before)
   */
//  @ClassRule
//  public static GenericContainer service = new GenericContainer("testing-with-containers/todo-list-service:0")
//      .withExposedPorts(9080)
//      .withNetwork(NETWORK)
//      .dependsOn(database)
//      .withLogConsumer(new Slf4jLogConsumer(LOG))
//      .waitingFor(
//          Wait.forLogMessage(".*server started.*", 1)
//      );

  private static Map<String, String> entityManagerProviderProperties = new HashMap<>();

  private static RequestSpecification requestSpecification;

  /**
   * HOWTO: 6. override JDBC Url - add database container port to JDBC url
   *
   * HINT: use jpa property "javax.persistence.jdbc.url"
   */
  @BeforeClass
  public static void setUpDatabase() {
    String databaseHost = DATABASE.getContainerIpAddress();
    Integer databasePort = DATABASE.getFirstMappedPort();
    entityManagerProviderProperties
        .put("javax.persistence.jdbc.url", String.format("jdbc:postgresql://%s:%d/postgres", databaseHost, databasePort));
  }

  /**
   * HOWTO: 4. get host and port from container - set host to container ip address - set port to container mapped port
   */
  @BeforeClass
  public static void setUpRequestSpecification() {
    requestSpecification = new RequestSpecBuilder()
        .setPort(SERVICE.getFirstMappedPort())
        .setBasePath("todo-list-service")
        .build();
  }

  @Rule
  public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("postgres-db", entityManagerProviderProperties);

  @Rule
  public DBUnitRule dbUnitRule = DBUnitRule.instance(entityManagerProvider.connection());

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-create-expected.yml", ignoreCols = "tod_id")
  public void createTodoShouldReturn201() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean fridge\",\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.CREATED.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todo-schema.json"))
        .body("id", Matchers.notNullValue())
        .body("title", Matchers.equalTo("clean fridge"))
        .body("description", Matchers.equalTo("It's a mess"))
        .body("dueDate", Matchers.notNullValue())
        .body("done", Matchers.equalTo(false))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-empty.yml")
  public void createTodoShouldReturn400ForEmptyRequestBody() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(2))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-empty.yml")
  public void createTodoShouldReturn400ForMissingDueDate() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean fridge\",\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-empty.yml")
  public void createTodoShouldReturn400ForMissingTitle() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-empty.yml")
  public void createTodoShouldReturn400ForTooLargeDescription() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"clean fridge\",\n"
              + "  \"description\": \"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-empty.yml")
  public void createTodoShouldReturn400ForTooLongTitle() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula e\",\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-empty.yml")
  public void createTodoShouldReturn400ForTooShortTitle() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\n"
              + "  \"title\": \"\",\n"
              + "  \"description\": \"It's a mess\",\n"
              + "  \"dueDate\": \"2018-01-01T12:34:56Z\",\n"
              + "  \"done\": false\n"
              + "}")
        .when()
        .post("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-delete.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-delete-expected.yml")
  public void deleteTodoShouldReturn204() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", DELETED_TODO_ID)
        .when()
        .delete("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NO_CONTENT.getStatusCode())
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-delete.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-delete.yml")
  public void deleteTodoShouldReturn404ForUnknownTodo() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UNKNOWN_TODO_ID)
        .when()
        .delete("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  public void getTodoShouldReturn200() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", TODO_ID)
        .when()
        .get("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.OK.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todo-schema.json"))
        .body("id", Matchers.equalTo(1))
        .body("title", Matchers.equalTo("clean fridge"))
        .body("description", Matchers.equalTo("It's a mess"))
        .body("dueDate", Matchers.notNullValue())
        .body("done", Matchers.equalTo(false))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  public void getTodoShouldReturn404ForUnknownTodo() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UNKNOWN_TODO_ID)
        .when()
        .get("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  public void getTodosShouldReturn200() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get("/api/todos")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.OK.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Todos-schema.json"))
        .body("size()", Matchers.is(7))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update-expected.yml", ignoreCols = "tod_duedate")
  public void updateTodoShouldReturn204() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NO_CONTENT.getStatusCode())
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForEmptyRequestBody() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(3))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForMissingDueDate() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForMissingDone() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\"\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForMissingTitle() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForTooLongDescription() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForTooLongTitle() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"title\": \"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula e\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn400ForTooShortTitle() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UPDATED_TODO_ID)
        .body("{\n"
              + "  \"title\": \"\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .rootPath("'errors'")
        .body("size()", Matchers.is(1))
        .log().ifValidationFails(LogDetail.ALL);
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update.yml")
  public void updateTodoShouldReturn404ForUnknownTodo() {
    RestAssured.given(requestSpecification)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .pathParam("todoId", UNKNOWN_TODO_ID)
        .body("{\n"
              + "  \"title\": \"clean bathroom\",\n"
              + "  \"description\": \"It's really dirty :(\",\n"
              + "  \"dueDate\": \"2018-01-02T10:30:00Z\",\n"
              + "  \"done\": true\n"
              + "}")
        .when()
        .put("/api/todos/{todoId}")
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .log().ifValidationFails(LogDetail.ALL);
  }
}
