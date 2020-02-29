/*
 * 02.07.2019 AP6T6
 * Copyright (c) 2019 HUK-COBURG. All Rights Reserved.
 * Copyright (C) 2019 open knowledge GmbH
 */
package de.openknowledge.projects.helloworld;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A resource that provides access to the world.
 */
@Path("hello")
@Produces(MediaType.TEXT_PLAIN)
public class HelloWorldResource {

  private static final Logger LOG = LoggerFactory.getLogger(HelloWorldResource.class);

  @GET
  @Path("{name}")
  @Operation(description = "Say hello to someone")
  @APIResponse(responseCode = "200", description = "Ok")
  public Response sayHello(@Parameter(description = "name") @PathParam("name") final String name) {
    LOG.info("Say 'Hello' to {}", name);

    String hello = String.format("Hello %s!", name);

    LOG.info(hello);

    return Response.status(Response.Status.OK)
        .entity(hello)
        .build();
  }

  @GET
  @Operation(description = "Say hello world")
  @APIResponse(responseCode = "200", description = "Ok")
  public Response sayHelloWorld() {
    return sayHello("World");
  }
}
