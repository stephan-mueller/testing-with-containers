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

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Activator
 */
@ApplicationPath("api")
@OpenAPIDefinition(info =
@Info(title = "Todo-List Service API", description = "Provides access to the API operations", version = "1.0.0",
    contact = @Contact(email = "kontakt@openknowledge.de"),
    license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")),
    servers = @Server(url = "http://{host}:{port}/{context-root}", variables = {
        @ServerVariable(name = "host", defaultValue = "localhost"),
        @ServerVariable(name = "port", defaultValue = "9080"),
        @ServerVariable(name = "context-root", defaultValue = "todo-list-service")
    }))
public class JaxRsActivator extends Application {

}
