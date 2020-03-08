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
package de.openknowledge.projects.helloworld;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Cucumber-Test for the resource {@link HelloWorldResource}.
 *
 * @see HelloWorldResourceCucumberSteps
 * @see HelloWorldResourceCucumberTestContainerBaseClass
 */
/**
 * EXERCISE 2: HelloWorld cucumber test with manual container management (JUnit 4)
 *
 * TODO:
 * 1. prepare Dockerfile
 * 2. add Generic Container with ImageFromDockerfile
 * 3. call start/stop
 * 4. get host and port from container
 *
 * @see HelloWorldResourceCucumberSteps
 *
 * HINT: Dockerfile is located at /testing-with-containers/hello-world/Dockerfile
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "de.openknowledge.projects.helloworld.HelloWorldResourceCucumberTestContainerBaseClass"}, features = "src/test/resources/it/feature")
public class HelloWorldResourceCucumberIT {
}
