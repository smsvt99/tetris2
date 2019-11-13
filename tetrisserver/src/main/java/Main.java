import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;
import static spark.Spark.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        staticFiles.location("/public/build");

        port(getHerokuAssignedPort());

        Db db = new Db();

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        get("/", (req, res) -> {
            res.redirect("index.html");
            return null;
        });

        get("/getTopTen", (req, res) -> {
            res.type("application/json");
            return db.getTopTen();
        });

        post("/insertOne", (req,res) -> {
            System.out.println(req.body());
            db.insertOne(req.body());
            redirect.any("/insertOne", "/allScores");
            res.status(301);
            return "";
        });
        get("/allScores", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("scores", db.getAllScores());
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/allScores.vm")
            );
        });
    }
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        } else {
            return Integer.parseInt(System.getenv("PORT"));
        }
        //return 4567; return default port if heroku-port isn't set (i.e. on localhost)
    }
}