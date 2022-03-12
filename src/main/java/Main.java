import spark.Service;
import todo.infra.TodoController;

public class Main {
    public static void main(String[] args) {

        final Service server =  Service.ignite();
        server.port(4567);

        TodoController todoController = new TodoController(server, true);

    }
}

