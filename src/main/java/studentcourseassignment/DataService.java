package studentcourseassignment;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.RoutingContext;
import studentcourseassignment.models.Course;
import studentcourseassignment.models.Grade;
import studentcourseassignment.models.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    private static final String DB_PATH = "studentcourse.db";
    JsonObject config = null;

    public DataService() {
        config = new JsonObject()
                .put("url", "jdbc:sqlite:" + DB_PATH)
                .put("driver_class", "org.sqlite.JDBC")
                .put("max_pool_size", 30);
    }

    public Promise<String> createCourse(RoutingContext ctx) {

        LOGGER.debug("Create Course Start...");

        Vertx vertx = ctx.vertx();
        JsonObject obj = ctx.body().asJsonObject();
        Course c = obj.mapTo(Course.class);

        Promise<String> resultPromise = Promise.promise();

        if (c != null && c.getName() != null) {
            SQLClient client = JDBCClient.create(vertx, config);
            JsonArray jsonArray = new JsonArray()
                    .add(0, c.getName())
                    .add(1, c.getStartDate())
                    .add(2, c.getEndDate())
                    .add(3, c.getStartTime())
                    .add(4, c.getEndTime())
                    .add(5, c.getCredit())
                    .add(6, c.getCapacity());

            client.queryWithParams("Insert into Course (name, startDate, endDate, startTime, endTime, credit, capacity) values" +
                    "($0, $1, $2, $3, $4, $5, $6)", jsonArray, result -> {
                if (result.failed()) {
                    LOGGER.error("DB Error", result.cause());
                    resultPromise.fail("Insert into Course table Failed." + result.cause().getMessage());
                } else {
                    LOGGER.info("Insert into Course succeeded for course "+ c.getName());
                    resultPromise.complete();
                }
                client.close();
            });
        } else {
            LOGGER.error("POST Body does not contain valid Course JSON");
            resultPromise.fail("POST Body does not contain valid Course JSON");
        }
        return resultPromise;
    }

    public Promise<String> updateCourse(RoutingContext ctx) {

        LOGGER.debug("Update Course Start...");

        Vertx vertx = ctx.vertx();
        JsonObject obj = ctx.body().asJsonObject();
        Course c = obj.mapTo(Course.class);

        Promise<String> resultPromise = Promise.promise();

        if (c != null && c.getId() != null) {
            SQLClient client = JDBCClient.create(vertx, config);
            JsonArray jsonArray = new JsonArray()
                    .add(0, c.getName())
                    .add(1, c.getStartDate())
                    .add(2, c.getEndDate())
                    .add(3, c.getStartTime())
                    .add(4, c.getEndTime())
                    .add(5, c.getCredit())
                    .add(6, c.getCapacity())
                    .add(7, c.getId());


            client.queryWithParams("Update Course Set name=?, startDate=?, endDate=?, startTime=?, " +
                    "endTime=?, credit=?, capacity=? where id=?", jsonArray, result -> {

                if (result.failed()) {
                    LOGGER.error("DB Error", result.cause());
                    resultPromise.fail("Update Course table Failed." + result.cause().getMessage());
                } else {
                    LOGGER.info("Update Course succeeded for course "+ c.getId());
                    resultPromise.complete();
                }
                client.close();
            });
        } else {
            LOGGER.error("POST Body does not contain valid Course JSON");
            resultPromise.fail("POST Body does not contain valid Course JSON");
        }
        return resultPromise;
    }

    public Promise<Double> getCourseGradePoint(RoutingContext ctx) {

        LOGGER.debug("getCourseGradePoint Start...");

        String id = ctx.pathParam("courseId");
        Promise<Double> coursesPromise = Promise.promise();
        if(id != null){
            SQLClient client = JDBCClient.create(ctx.vertx(), config);
            JsonArray jsonArray = new JsonArray().add(0, id);
            client.queryWithParams("Select grade from StudentCourse sc join Course c on sc.courseId = c.Id where sc.courseId = ?",
                    jsonArray, result -> {
                        if (result.failed()) {
                            LOGGER.error("DB Error", result.cause());
                            coursesPromise.fail("Select grade from StudentCourse sc join Course c on sc.courseId = c.Id. Failed." + result.cause().getMessage());
                        } else {
                            ResultSet resultSet = result.result();
                            int sum = 0;
                            int count = 0;
                            for(JsonArray arr : resultSet.getResults()){
                                String grade = arr.getString(0);
                                if(grade != null) {
                                    Grade g = new Grade(grade);
                                    sum += g.getPoints();
                                    count++;
                                }
                            }
                            Double avg = new Double(0.0);
                            if(count > 0)
                                avg = (double)sum/(double)count;

                            coursesPromise.complete(avg);
                        }
                        client.close();
                    });
        }else{
            coursesPromise.fail("POST Body does not contain valid Student JSON");
        }
        return coursesPromise;
    }

    public Promise<String> createStudent(RoutingContext ctx) {

        LOGGER.debug("Create Student Start...");

        Vertx vertx = ctx.vertx();
        JsonObject obj = ctx.body().asJsonObject();
        Student s = obj.mapTo(Student.class);

        Promise<String> resultPromise = Promise.promise();

        if (s != null && s.getName() != null) {
            SQLClient client = JDBCClient.create(vertx, config);
            JsonArray jsonArray = new JsonArray()
                    .add(0, s.getName())
                    .add(1, s.getCredit());

            client.queryWithParams("Insert into Student (name, credit) values" +
                    "($0, $1)", jsonArray, result -> {
                if (result.failed()) {
                    LOGGER.error("DB Error", result.cause());
                    resultPromise.fail("Insert into Student table Failed." + result.cause().getMessage());
                } else {
                    LOGGER.info("Insert into Student succeeded for course "+ s.getName());
                    resultPromise.complete();
                }
                client.close();
            });
        } else {
            LOGGER.error("POST Body does not contain valid Student JSON");
            resultPromise.fail("POST Body does not contain valid Student JSON");
        }
        return resultPromise;
    }

    public Promise<String> registerStudent(RoutingContext ctx) {

        LOGGER.debug("Register Student Start...");
        Student s = getStudent(ctx);
        Promise<String> registerPromise = Promise.promise();

        if(s.getId() != null && s.getCourse() != null){
            Promise<String> resultPromise = getCourseIdByName(s.getCourse(), ctx);
            resultPromise.future().onSuccess(courseId -> {
                SQLClient client = JDBCClient.create(ctx.vertx(), config);

                JsonArray jsonArray = new JsonArray()
                        .add(0, s.getId())
                        .add(1, courseId);

                client.queryWithParams("Insert into StudentCourse (studentId, courseId) values ($0, $1)", jsonArray, result -> {
                    if (result.failed()) {
                        LOGGER.error("DB Error", result.cause());
                        registerPromise.fail("Insert into StudentCourse Failed." + result.cause().getMessage());
                    } else {
                        LOGGER.info("Insert into StudentCourse for student "+ s.getId());
                        registerPromise.complete();
                    }
                    client.close();
                });
            });
        }else{
            registerPromise.fail("POST Body does not contain valid Student JSON");
        }

        return registerPromise;
    }

    public Promise<String> postGradeStudent(RoutingContext ctx) {

        LOGGER.debug("Register Student Start...");
        Student s = getStudent(ctx);
        Promise<String> registerPromise = Promise.promise();

        if(s.getId() != null && s.getCourse() != null){
            Promise<String> resultPromise = getCourseIdByName(s.getCourse(), ctx);
            resultPromise.future().onSuccess(courseId -> {
                SQLClient client = JDBCClient.create(ctx.vertx(), config);

                JsonArray jsonArray = new JsonArray()
                        .add(0, s.getGrade())
                        .add(1, s.getId())
                        .add(2, courseId);

                client.queryWithParams("Update StudentCourse Set Grade=? where studentId=? and courseId=?", jsonArray, result -> {
                    if (result.failed()) {
                        LOGGER.error("DB Error", result.cause());
                        registerPromise.fail("Update StudentCourse Failed." + result.cause().getMessage());
                    } else {
                        LOGGER.info("Posted Gradefor student "+ s.getId());
                        registerPromise.complete();
                    }
                    client.close();
                });
            });
        }else{
            registerPromise.fail("POST Body does not contain valid Student JSON");
        }

        return registerPromise;
    }

    public Promise<Map<String,String>> getStudentCourses(RoutingContext ctx) {

        LOGGER.debug("getStudentCourses Start...");

        String id = ctx.pathParam("studentId");
        Promise<Map<String,String>> coursesPromise = Promise.promise();
        if(id != null){
            SQLClient client = JDBCClient.create(ctx.vertx(), config);
            JsonArray jsonArray = new JsonArray().add(0, id);
            client.queryWithParams("Select c.name, grade from StudentCourse sc join Course c on sc.courseId = c.Id where sc.studentId = ?",
                    jsonArray, result -> {
                if (result.failed()) {
                    LOGGER.error("DB Error", result.cause());
                    coursesPromise.fail("Update StudentCourse Failed." + result.cause().getMessage());
                } else {
                    //getCourseNameById(result.result().getResults(), ctx, coursesPromise);
                    ResultSet resultSet = result.result();
                    Map<String,String> coursesGradeMap = new HashMap();
                    for(JsonArray arr : resultSet.getResults()){
                        coursesGradeMap.put(arr.getString(0), arr.getString(1));
                    }
                    coursesPromise.complete(coursesGradeMap);
                }
                client.close();
            });
        }else{
            coursesPromise.fail("POST Body does not contain valid Student JSON");
        }
        return coursesPromise;
    }

    public Promise<Double> getStudentGradePoint(RoutingContext ctx) {

        LOGGER.debug("getStudentCourses Start...");

        String id = ctx.pathParam("studentId");
        Promise<Double> coursesPromise = Promise.promise();
        if(id != null){
            SQLClient client = JDBCClient.create(ctx.vertx(), config);
            JsonArray jsonArray = new JsonArray().add(0, id);
            client.queryWithParams("Select grade from StudentCourse sc join Course c on sc.courseId = c.Id where sc.studentId = ?",
                    jsonArray, result -> {
                        if (result.failed()) {
                            LOGGER.error("DB Error", result.cause());
                            coursesPromise.fail("Select grade from StudentCourse sc join Course c on sc.courseId = c.Id where sc.studentId = Failed." + result.cause().getMessage());
                        } else {
                            ResultSet resultSet = result.result();
                            int sum = 0;
                            int count = 0;
                            for(JsonArray arr : resultSet.getResults()){
                                String grade = arr.getString(0);
                                if(grade != null) {
                                    Grade g = new Grade(grade);
                                    sum += g.getPoints();
                                    count++;
                                }
                            }
                            Double avg = new Double(0.0);
                            if(count > 0)
                                avg = (double)sum/(double)count;

                            coursesPromise.complete(avg);
                        }
                        client.close();
                    });
        }else{
            coursesPromise.fail("POST Body does not contain valid Student JSON");
        }
        return coursesPromise;
    }

    public Promise<String> updateStudent(RoutingContext ctx) {

        LOGGER.debug("Update Student Start...");

        Promise<String> resultPromise = Promise.promise();

        Vertx vertx = ctx.vertx();
        JsonObject obj = ctx.body().asJsonObject();
        Student s = obj.mapTo(Student.class);

        if(s.getId() != null && s.getName() != null) {
            SQLClient client = JDBCClient.create(vertx, config);
            JsonArray jsonArray = new JsonArray()
                    .add(0, s.getName())
                    .add(1, s.getCredit())
                    .add(2, s.getId());

            client.queryWithParams("Update Student Set name=?, credit=? where id=?", jsonArray, result -> {
                client.close();

                if (result.failed()) {
                    LOGGER.error("DB Error", result.cause());
                    resultPromise.fail("Update Student table Failed." + result.cause().getMessage());
                } else {
                    LOGGER.info("Update Student succeeded for course " + s.getId());
                    resultPromise.complete();
                }
            });
        }else{
            resultPromise.fail("POST Body does not contain valid Student JSON");
        }

        return resultPromise;
    }

    private Promise<String> getCourseIdByName(String name, RoutingContext ctx){

        Vertx vertx = ctx.vertx();
        Promise<String> resultPromise = Promise.promise();
        if (name == null) {
            LOGGER.error("POST Body does not contain valid Student JSON");
            resultPromise.fail("POST Body does not contain valid Student JSON");
            return resultPromise;
        }
        SQLClient client = JDBCClient.create(vertx, config);
        JsonArray jsonArray = new JsonArray().add(0, name);
        client.queryWithParams("Select Id from Course where name=?", jsonArray, result -> {
            if (result.failed()) {
                LOGGER.error("DB Error", result.cause());
                resultPromise.fail("Update Student table Failed." + result.cause().getMessage());
            } else {
                ResultSet resultSet = result.result();
                Integer courseId = resultSet.getResults().get(0).getInteger(0);
                LOGGER.info("Id = " + courseId);
                resultPromise.complete(courseId.toString());
            }
            client.close();
        });

        return resultPromise;
    }

    private void getCourseNameById(List<JsonArray> ids, RoutingContext ctx, Promise<List<String>> coursesPromise){

        Vertx vertx = ctx.vertx();
        SQLClient client = JDBCClient.create(vertx, config);

        JsonArray jsonArray = new JsonArray();
        StringBuilder builder = new StringBuilder("(");
        int count = 0;
        for(JsonArray arr : ids){
            jsonArray.add(arr.getInteger(0));
            builder.append("?");
            if(count != ids.size() - 1)
                builder.append(",");
            count++;
        }
        builder.append(")");

        client.queryWithParams("Select name from Course where id in "+builder.toString(), jsonArray, result -> {
            if (result.failed()) {
                LOGGER.error("DB Error", result.cause());
                coursesPromise.fail("Get Courses for Student Failed." + result.cause().getMessage());
            } else {
                ResultSet resultSet = result.result();
                List<String> courses = new ArrayList<>();
                for(JsonArray arr : resultSet.getResults()){
                    courses.add(arr.getString(0));
                }
                coursesPromise.complete(courses);
            }
            client.close();
        });

    }

    private Student getStudent(RoutingContext ctx){
        JsonObject obj = ctx.body().asJsonObject();
        Student s = obj.mapTo(Student.class);
        return s;
    }



}
