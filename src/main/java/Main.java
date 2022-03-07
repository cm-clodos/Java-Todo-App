import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        //http://localhost:4567/todos
        List<TodoItem>todos = new ArrayList<>(
                Arrays.asList(
                        TodoItem.create("Kochen",1L),TodoItem.create("Radeln",2L),
                        TodoItem.create("Einkaufen", 3L), TodoItem.create("Reise buchen", 4L),
                        TodoItem.create("Wäsche waschen", 5L))
                );
        System.out.println(todos.size());


        get("/todos","application/json", (req, res) -> {
            req.headers("accept").equalsIgnoreCase("application/json;charset=utf-8");
                //content type verändern der Response als info für den client welches format
                res.header("content-type", "application/json;charset=utf-8");

                return new JSONSerializer().serialize(todos);
        });

        // curl -i -X DELETE -H 'content-type: application/json' http://localhost:4567/todos/:id
        //erstellt einen DELETE Aufruf auf ein Item mit der id xy
        delete("/todos/:id","application/json", (req, res) -> {
            req.headers("accept").equalsIgnoreCase("application/json;charset=utf-8");
            //Liest die Id aus dem get
            Long id = Long.valueOf(req.params().get(":id"));
            int oldItemsSize = todos.size();
            //content type verändern der Response als info für den client welches format
            res.header("content-type", "application/json;charset=utf-8");
            // Durchsucht die ArrayList: id die aus Get gelesen wird mit der gleichen ID vom Long des Todoitems
            //Wenn gleiche ID dann gibt die compare 0 zurück.--> folge item wird aus arrayList entfernt.
            todos.removeIf(n -> (Long.compare(n.getId(),id)==0));
            int newItemsSize = todos.size();
            System.out.println(newItemsSize);

            if (oldItemsSize == newItemsSize){
                res.status(406);
            }

            return new JSONSerializer().serialize(todos);

        });
        //curl -i -X POST -H 'content-type: application/json' http://localhost:4567/todos/
        post("/todos/", "application/json", (req,res) -> {
            req.headers("accept").equalsIgnoreCase("application/json;charset=utf-8");
            res.header("content-type", "application/json;charset=utf-8");

            //Neues Item wird erstellt
            TodoItem newItem = TodoItem.create("Hausaufgaben", 6L);
            System.out.println(newItem);

            todos.add(newItem);

            return new JSONSerializer().serialize(todos);


        });


    }
}

