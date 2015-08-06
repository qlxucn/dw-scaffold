package com.dropwizard.controller;

import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/dw")
public class DropwizardScaffoldController {
    private static final Logger LOGGER = Logger.getLogger(DropwizardScaffoldController.class);

    @GET
    @Path("/test")
    public Response testPerformance() {
        return Response.ok("This is a test.").build();
    }
}
