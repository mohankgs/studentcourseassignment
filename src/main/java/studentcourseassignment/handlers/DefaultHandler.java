package studentcourseassignment.handlers;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import studentcourseassignment.MainVerticle;

public class DefaultHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultHandler.class);

  public void handle(RoutingContext ctx){

    LOGGER.debug("default route");

    // This handler will be called for every request
    HttpServerResponse response = ctx.response();
    response.putHeader("content-type", "text/plain");

    // Write to the response and end it
    response.end("Hello World from Vert.x-Web!");
  }
}
