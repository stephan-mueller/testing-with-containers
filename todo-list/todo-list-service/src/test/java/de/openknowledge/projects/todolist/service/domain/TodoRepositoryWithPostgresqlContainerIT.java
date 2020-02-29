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

import de.openknowledge.projects.todolist.service.domain.Todo;
import de.openknowledge.projects.todolist.service.domain.TodoRepository;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DbUnit test for the repository {@link TodoRepository}.
 */
@Ignore
@RunWith(JUnit4.class)
@DBUnit(caseSensitiveTableNames = true, escapePattern = "\"?\"")
public class TodoRepositoryWithPostgresqlContainerIT {

  @ClassRule
  public static PostgreSQLContainer<?> database = new PostgreSQLContainer<>();

  @Rule
  public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("postgres-test");

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
