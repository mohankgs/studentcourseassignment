package studentcourseassignment.handlers;

import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import studentcourseassignment.DataService;

public class CourseHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CourseHandler.class);

  public void createCourse(RoutingContext ctx){
    DataService ds = new DataService();
    Promise<String> resultPromise = ds.createCourse(ctx);
    resultPromise.future().onSuccess( result -> {
      ctx.response().putHeader("Content-Type", "application/json");
      ctx.response().setStatusCode(201).end("{'message' : 'successfully created Course'}");
    });
    resultPromise.future().onFailure( error -> {
      ctx.response().putHeader("Content-Type", "application/json");
      ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
    });
  }

  public void updateCourse(RoutingContext ctx){
    DataService ds = new DataService();
    Promise<String> resultPromise = ds.updateCourse(ctx);
    resultPromise.future().onSuccess( result -> {
      ctx.response().putHeader("Content-Type", "application/json");
      ctx.response().setStatusCode(201).end("{'message' : 'successfully updated Course'}");
    });
    resultPromise.future().onFailure( error -> {
      ctx.response().putHeader("Content-Type", "application/json");
      ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
    });
  }

  public void getCourseGradePoint(RoutingContext ctx){
    DataService ds = new DataService();
    Promise<Double> resultPromise = ds.getCourseGradePoint(ctx);

    resultPromise.future().onSuccess( result -> {
      ctx.response().putHeader("Content-Type", "application/json");
      ctx.response().setStatusCode(200).end("{\n\t'gradePointAverage': " + result.toString() + "}");
    });

    resultPromise.future().onFailure( error -> {
      ctx.response().putHeader("Content-Type", "application/json");
      ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
    });
  }


}
