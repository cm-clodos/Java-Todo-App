package todo.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.LoggerFactory;
import shared.infra.JSONSerializer;
import spark.Service;
import todo.model.TodoItem;
import todo.service.TodoService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Locale;

import static spark.Spark.*;


public class TodoController {


    public TodoController(Service server, boolean isTest) {
        // auswählen ob Test
        TodoService todoService = new TodoService(isTest? new InMemoryRepository(): new SQliteRepository());

        //curl -i http://localhost:4567/todos



        //curl -i http://localhost:4567/todos/1
       server.get("/todos/:id", "application/json", (req, res) -> {

            final Long idToRead = Long.valueOf(req.params("id"));
            TodoItem item = todoService.getById(idToRead);

       res.status(200);
       return new JSONSerializer().serialize(item);
        });



        // curl -i http://localhost:4567/todos?description=einkaufen

       server.get("/todos", "application/json", (req, res) -> {
            if (req.queryParams("description") != null) {

                if (todoService.getFilterTodo(req.queryParams("description").toLowerCase(Locale.ROOT)).size() == 0) {
                    res.status(404);
                    return new JSONSerializer().serialize("No Todo with this description was found!");
                } else {
                    res.status(200);
                    return new JSONSerializer().serialize(todoService.getFilterTodo(req.queryParams("description").toLowerCase(Locale.ROOT)));
                }
            }   res.status(200);
                return new JSONSerializer().serialize(todoService.getAllTodos());

        });

        // curl -i -X DELETE -H 'content-type: application/json' http://localhost:4567/todos/:id
        //erstellt einen DELETE Aufruf auf ein Item mit der id xy
       server.delete("/todos/:id", "application/json", (req, res) -> {
            //content type verändern der Response als info für den client welches format
            //Liest die Id aus der URL
            Long idToDelete = Long.valueOf(req.params().get(":id"));

           if ( todoService.removeItemById(idToDelete)){
               res.status(200);
           }else {
               res.status(404);
           }
            return new JSONSerializer().serialize(todoService.getAllTodos());

        });
        //curl -i -X POST http://localhost:4567/todos -H 'content-type: application/json' -d '{"description":"auto reparieren"}'
        server.post("/todos", "application/json", (req, res) -> {

            // request body deserialisieren (json -> Objekt)
            TodoItem newItem = new JSONSerializer().deserialize(req.body(), new TypeReference<TodoItem>() {
            });

            Boolean created = todoService.createItem(newItem);

            if (created){
                res.status(202);
            }else {
                res.status(400);
                return new JSONSerializer().serialize("No Description insert!");
            }

            //gibt newItem als String zurück
            return new JSONSerializer().serialize(newItem);
        });
        //filter setzt für alle response den header content-type
      server.before(((request, response) -> {
            //content type verändern der Response als info für den client welches format
            response.header("content-type", "application/json;charset=utf-8");
        }));

        //exception handler
        //handelt exceptions.... logger erstellen der alle exception ins loggerfile schreibt

      server.exception(Exception.class, (exception, request, response) -> {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);

            LoggerFactory.getLogger("server").error(sw.toString());
            response.body("");
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        });
        server.awaitInitialization();

    }


}

