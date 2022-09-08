package studentcourseassignment;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import studentcourseassignment.handlers.CourseHandler;
import studentcourseassignment.handlers.DefaultHandler;
import studentcourseassignment.handlers.StudentHandler;


public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    LOGGER.debug("Starting Main Verticle...");

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    setBodyHandler(router);

    setCourseRoute(router);
    setStudentRoute(router);
    setRegisterRoute(router);
    setGradeRoute(router);
    setGetStudentCoursesRoute(router);
    setGetGradePointRoute(router);
    setGetCourseGradePointRoute(router);

    router.route().handler(ctx -> {
      DefaultHandler defaultHandler = new DefaultHandler();
      defaultHandler.handle(ctx);
    });

    server.requestHandler(router).listen(8080);

    router.route().failureHandler( ctx -> {
      ctx.response().setStatusCode(500);
      ctx.response().end("Unknown Error Occurred");
    });

  }

  private void setBodyHandler(Router router) {
    BodyHandler bodyHandler = BodyHandler.create();
    router.post().handler(bodyHandler);
    router.patch().handler(bodyHandler);
  }

  private void setCourseRoute(Router router) {
    router.post("/course").handler(ctx -> {
      CourseHandler courseHandler = new CourseHandler();
      courseHandler.createCourse(ctx);
    });

    router.patch("/course").handler(ctx -> {
      CourseHandler courseHandler = new CourseHandler();
      courseHandler.updateCourse(ctx);
    });
  }

  private void setStudentRoute(Router router) {
    router.post("/student").handler(ctx -> {
      StudentHandler studentHandler = new StudentHandler();
      studentHandler.createStudent(ctx);
    });

    router.patch("/student").handler(ctx -> {
      StudentHandler studentHandler = new StudentHandler();
      studentHandler.updateStudent(ctx);
    });
  }

  private void setRegisterRoute(Router router) {
    router.post("/student/register").handler(ctx -> {
      StudentHandler studentHandler = new StudentHandler();
      studentHandler.registerStudent(ctx);
    });
  }

  private void setGradeRoute(Router router) {
    router.post("/student/course/grade").handler(ctx -> {
      StudentHandler studentHandler = new StudentHandler();
      studentHandler.postGradeStudent(ctx);
    });
  }

  private void setGetStudentCoursesRoute(Router router) {
    router.get("/student/:studentId/course").handler(ctx -> {
      StudentHandler studentHandler = new StudentHandler();
      studentHandler.getStudentCourses(ctx);
    });
  }

  private void setGetGradePointRoute(Router router) {
    router.get("/student/:studentId/gradePoint").handler(ctx -> {
      StudentHandler studentHandler = new StudentHandler();
      studentHandler.getStudentGradePoint(ctx);
    });
  }

  private void setGetCourseGradePointRoute(Router router) {
    router.get("/course/:courseId/gradePoint").handler(ctx -> {
      CourseHandler courseHandler = new CourseHandler();
      courseHandler.getCourseGradePoint(ctx);
    });
  }

}
