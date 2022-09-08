package studentcourseassignment.handlers;

import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import studentcourseassignment.DataService;

import java.util.List;
import java.util.Map;

public class StudentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StudentHandler.class);

    public void createStudent(RoutingContext ctx){
        DataService ds = new DataService();
        Promise<String> resultPromise = ds.createStudent(ctx);
        resultPromise.future().onSuccess( result -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(201).end("{'message' : 'successfully created Student'}");
        });
        resultPromise.future().onFailure( error -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
        });
    }

    public void updateStudent(RoutingContext ctx){
        DataService ds = new DataService();
        Promise<String> resultPromise = ds.updateStudent(ctx);
        resultPromise.future().onSuccess( result -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(200).end("{'message' : 'successfully updated Student'}");
        });

        resultPromise.future().onFailure( error -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
        });
    }

    public void registerStudent(RoutingContext ctx){
        DataService ds = new DataService();
        Promise<String> resultPromise = ds.registerStudent(ctx);
        resultPromise.future().onSuccess( result -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(200).end("{'message' : 'successfully registered Student'}");
        });

        resultPromise.future().onFailure( error -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
        });
    }

    public void postGradeStudent(RoutingContext ctx){
        DataService ds = new DataService();
        Promise<String> resultPromise = ds.postGradeStudent(ctx);
        resultPromise.future().onSuccess( result -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(200).end("{'message' : 'successfully posted grade for Student'}");
        });

        resultPromise.future().onFailure( error -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
        });
    }

    public void getStudentCourses(RoutingContext ctx){
        DataService ds = new DataService();
        Promise<Map<String,String>> resultPromise = ds.getStudentCourses(ctx);

        resultPromise.future().onSuccess( result -> {
            ctx.response().putHeader("Content-Type", "application/json");
            StringBuilder builder = new StringBuilder("[\n");
            int count = 0;
            for(String course : result.keySet()){
                builder.append("{\n\t\t'course':'");
                builder.append(course);
                builder.append("'\n");
                builder.append("\t\t'grade':'");
                builder.append(result.get(course));
                if(result.keySet().size() - 1 == count)
                    builder.append("'}\n");
                else
                    builder.append("'},\n");
                count++;
            }
            builder.append("]");
            ctx.response().setStatusCode(200).end(builder.toString());
        });

        resultPromise.future().onFailure( error -> {
            ctx.response().putHeader("Content-Type", "application/json");
            ctx.response().setStatusCode(400).end(String.format("{'message' : '%s' }", error.getMessage()));
        });
    }

    public void getStudentGradePoint(RoutingContext ctx){
        DataService ds = new DataService();
        Promise<Double> resultPromise = ds.getStudentGradePoint(ctx);

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
