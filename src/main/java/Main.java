import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        //http://localhost:4567/hello

        get("/todos","application/json", (req, res) -> {
            req.headers("accept").equalsIgnoreCase("application/json;charset=utf-8");
                //content type verändern der Response als info für den client welches format
                res.header("content-type", "application/json;charset=utf-8");
                List<TodoItem>todos = List.of(TodoItem.create("Kochen",1L),TodoItem.create("Radeln",2L),
                        TodoItem.create("Einkaufen", 3L), TodoItem.create("Reise buchen", 4L),
                        TodoItem.create("Wäsche waschen", 5L));
                return new JSONSerializer().serialize(todos);
        });

        delete("/todos/:id", (req, res) -> {
            req.headers("accept").equalsIgnoreCase("application/json;charset=utf-8");
            //Lies die Id aus dem get
            Long id = Long.valueOf(req.params().get(":id"));
            //content type verändern der Response als info für den client welches format
            res.header("content-type", "application/json;charset=utf-8");

           // todos.removeIf(n -> (Long.compare(n.getId(),id)==0))


            return null;
        });

    }
}

