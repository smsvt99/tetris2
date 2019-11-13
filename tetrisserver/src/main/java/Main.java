import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;


import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Db db = new Db();

        staticFiles.location("public/build");


//        get("/", (req, res) -> {
//            res.redirect("index.html");
//            return null;
//        });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        get("/hello", (req, res) -> "Hello World");
        get("/getTopTen", (req, res) -> {
            res.type("application/json");
            return db.getTopTen();
        });
        post("/insertOne", (req,res) -> {
            System.out.println(req.body());
            db.insertOne(req.body());
//            return res.redirect("/allScores");
//            res.redirect("http://localhost:4567/allScores");
            redirect.any("/insertOne", "/allScores");
            res.status(301);
            return "";
        });
        get("/allScores", (req, res) -> {
//            VelocityEngine velocityEngine = new VelocityEngine();
//            velocityEngine.init();
//
//            Template t = velocityEngine.getTemplate("templates/allScores.vm");
//
//            VelocityContext context = new VelocityContext();
//            context.put("name", "World");
//
//            StringWriter writer = new StringWriter();
//            t.merge( context, writer );

            Map<String, Object> model = new HashMap<>();
            model.put("scores", db.getAllScores());
            return new VelocityTemplateEngine().render(
                    new ModelAndView(model, "templates/allScores.vm")
            );
        });
    }
}