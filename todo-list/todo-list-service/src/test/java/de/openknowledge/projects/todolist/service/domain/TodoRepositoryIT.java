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
package de.openknowledge.projects.todolist.service.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;

import de.openknowledge.projects.todolist.service.AbstractIntegrationTest;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.MountableFile;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DbUnit test for the repository {@link TodoRepository}.
 */
/**
 * EXERCISE 3: DbUnit persistence test with Postgres DB (JUnit 4)
 *
 * HOWTO:
 * 1. add FixedHostPortGenericContainer with postgres image
 * 2. set environment variables for database configuration (database, user, password)
 * 3. add DDL script
 *
 * 4. replace FixedHostPortGenericContainer by GenericContainer
 * 5. override JDBC Url
 */
@RunWith(JUnit4.class)
@DBUnit(caseSensitiveTableNames = true, escapePattern = "\"?\"")
public class TodoRepositoryIT extends AbstractIntegrationTest {

  private static final Logger LOG = LoggerFactory.getLogger(TodoRepositoryIT.class);

  private static Map<String, String> entityManagerProviderProperties = new HashMap<>();

  /**
   * HOWTO:
   * 5. override JDBC url
   * - add container port to JDBC url
   *
   * HINT: use jpa property "javax.persistence.jdbc.url"
   */
  @BeforeClass
  public static void setUpDatabase() {
    entityManagerProviderProperties.put("javax.persistence.jdbc.url", String.format("jdbc:postgresql://localhost:%d/postgres", DATABASE.getFirstMappedPort()));
  }

  @Rule
  public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("postgres-db", entityManagerProviderProperties);

  @Rule
  public DBUnitRule dbUnitRule = DBUnitRule.instance(entityManagerProvider.connection());

  private TodoRepository repository;

  @Before
  public void setUp() {
    repository = new TodoRepository(entityManagerProvider.getEm());
  }

  @Test
  @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-create-expected.yml", ignoreCols = "TOD_ID")
  public void create() {
    entityManagerProvider.getEm().getTransaction().begin();

    Todo todo = Todo.newBuilder()
        .withTitle("clean fridge")
        .withDescription("It's a mess")
        .withDueDate(OffsetDateTime.now().minusDays(1))
        .setDone(false)
        .build();

    repository.create(todo);

    entityManagerProvider.getEm().getTransaction().commit();
  }

  @Test
  @DataSet(value = "datasets/todos-delete.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-delete-expected.yml")
  public void delete() {
    entityManagerProvider.getEm().getTransaction().begin();

    Todo foundTodo = repository.find(4L).get();
    repository.delete(foundTodo);

    entityManagerProvider.getEm().getTransaction().commit();
  }

  @Test
  @DataSet(value = "datasets/todos.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  public void findAll() {
    List<Todo> todos = repository.findAll();
    Assertions.assertThat(todos).hasSize(7);
  }

  @Test
  @DataSet(value = "datasets/todos.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  public void findByIdShouldReturnTodo() {
    Todo todo = repository.find(1L).get();
    Assertions.assertThat(todo).isNotNull();
  }

  @Test
  @DataSet(value = "datasets/todos.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  public void findByIdShouldReturnEmptyOptional() {
    Optional<Todo> todo = repository.find(-1L);
    assertThat(todo.isPresent()).isFalse();
  }

  @Test
  @DataSet(value = "datasets/todos-update.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true)
  @ExpectedDataSet(value = "datasets/todos-update-expected.yml")
  public void update() {
    entityManagerProvider.getEm().getTransaction().begin();

    Todo foundTodo = repository.find(2L).get();
    foundTodo.updateTodo(foundTodo.getTitle(), "It's really dirty :(", foundTodo.getDueDate(), true);
    repository.update(foundTodo);

    entityManagerProvider.getEm().getTransaction().commit();
  }
}
