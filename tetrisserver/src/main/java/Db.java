import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;


public class Db {
    Logger logger = LoggerFactory.getLogger(Db.class);
    String url = System.getenv("url");
    Connection connection;

    public Db(){
        this.loadDriver();
        this.connection = this.getConnection();
    }

    private Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.debug("driver loaded");
        } catch (ClassNotFoundException ex) {
            logger.debug(ex.getMessage());
        }
        try{
            Connection conn = DriverManager.getConnection(this.url);
            logger.debug("connected");
            return conn;

        } catch (SQLException ex) {
            logger.debug("SQLException: " + ex.getMessage());
            logger.debug("SQLState: " + ex.getSQLState());
            logger.debug("VendorError: " + ex.getErrorCode());
        }
        return null;
    }

    private void loadDriver(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.debug("driver loaded");
        } catch (ClassNotFoundException ex){
            logger.debug(ex.getMessage());
        }
    }

    public String getTopTen(){
        try{
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM scores ORDER BY score DESC LIMIT 10;");
            String str = this.convertToJSON(rs);
            System.out.println(str);
            return str;
        } catch(SQLException e){
            logger.info(e.getMessage());
            return null;
        } catch (Exception e){
            logger.info(e.getMessage());
            return null;
        }
    }
    public ArrayList<Score> getAllScores(){
        try{
            ArrayList<Score> allScores = new ArrayList<>();
            Statement statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM scores ORDER BY score DESC;");
            while(rs.next()){
                allScores.add(new Score(rs.getString("name"), rs.getInt("score")));
            }
            return allScores;
        } catch(SQLException e){
            logger.info(e.getMessage());
            return null;
        } catch (Exception e){
            logger.info(e.getMessage());
            return null;
        }
    }
    public void insertOne(String json){
        Gson gson = new Gson();
        Score score = gson.fromJson(json, Score.class);
        System.out.println(score.score);
        System.out.println(score.name);
        try{
            Statement statement = this.connection.createStatement();
            statement.executeUpdate("INSERT INTO scores(score, name) VALUES (\"" + score.score +"\", \"" + score.name +"\");");
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static String convertToJSON(ResultSet rs) throws Exception {
        JsonArray jsArr = new JsonArray();
        while(rs.next()){
            JsonObject jsObj = new JsonObject();
            jsObj.addProperty("name", rs.getString("name"));
            jsObj.addProperty("score", rs.getInt("score"));
            jsObj.addProperty("id", rs.getInt("id"));
            jsArr.add(jsObj);
        }
        return jsArr.toString();
        }
    }
