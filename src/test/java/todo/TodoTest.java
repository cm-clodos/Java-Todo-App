package todo;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import shared.infra.JSONSerializer;
import spark.Service;
import todo.infra.TodoController;
import todo.model.TodoItem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TodoTest {
    Service server;


    //Startet vor jedem Test einen eigenen Server
    @Before
    public void start() {
        server = Service.ignite();
        server.port(4567);
        new TodoController(server, true);
    }

    // Stoppt den Server nach jedem Test
    @After
    public void stopp() {
        server.stop();
    }
   /* @Test
    public void getHelloWorld_returnsText() throws IOException, InterruptedException {
        //request erstellen
        final HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:4567/hello")).build();
        //Client
        final HttpClient client = HttpClient.newBuilder().build();
        //response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //response von Server gleich Erwartung?
        Assert.assertEquals("Hello World", response.body());
    }*/

    @Test
    public void getTodos_should_returnContentTypeTodosList() throws URISyntaxException, IOException, InterruptedException {
        //Zuerst einen request erstellen
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .GET()
                .header("accept", "application/json")
                .build();
        //danach Client erstellen der den zuvor erstellten request sendet und den response als HttpResponse abspeichert.
        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        //nun kann der erhaltene Response überprüft/getestet werden...
        //checkt das Header content type = application/json;charset=utf-8
        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());

        // das enthaltene JSON deserialiseren und tudu model TodoItem Objekte in List abfüllen. Kann Anzahl items getestet werden
        final List<TodoItem> todos = new JSONSerializer().deserialize(response.body(), new TypeReference<List<TodoItem>>() {
        });

        Assert.assertTrue("Should have any items", 0 < todos.size());
    }

    @Test
    public void getToDoByI_should_ReturnCode200() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos/1"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, response.statusCode());

    }
    @Test
    public void getToDoById_should_ReturnCode404IdNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos/500"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(404, response.statusCode());

    }

    @Test
    public void deleteTodo_should_deleteAndNoMoreRead() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos/1"))
                .DELETE()
                .header("accept", "application/json")
                .build();


        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());
        Assert.assertEquals(200, response.statusCode());

        HttpRequest verifyDeletedRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos/1"))
                .GET()
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> verifyDeletedResponse = HttpClient.newHttpClient().send(verifyDeletedRequest, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());
        Assert.assertEquals(404, verifyDeletedResponse.statusCode());

    }
    @Test
    public void deleteTodo_should_ReturnCode404NoIdFoundToDelete() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos/500"))
                .DELETE()
                .header("accept", "application/json")
                .build();


        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());


        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());
        Assert.assertEquals(404, response.statusCode());

    }

    @Test
    public void createTodo_should_return202CreatedAndReadIsPossible() throws URISyntaxException, IOException, InterruptedException {
        //gibt hardcodetes item mit
        final TodoItem todoItem = TodoItem.create(100L, "putzen");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());
        //status created
        Assert.assertEquals(202, response.statusCode());

        //überprüfe
        final TodoItem todo = new JSONSerializer().deserialize(response.body(), new TypeReference<>() {
        });

        Assert.assertEquals("putzen", todo.description);
        Assert.assertNotNull(todo.id);

        //GET request created Item
        HttpRequest requestForCreatedTodo = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:4567/todos/100"))
                .header("accept", "application/json")
                .build();

        HttpClient client = HttpClient.newBuilder().build();
        var response2 = client.send(requestForCreatedTodo, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, response2.statusCode());
    }

    @Test
    public void getFilterTodos_should_returnTrueIfDescriptionContainsEinkaufen() throws URISyntaxException, IOException, InterruptedException {

        final TodoItem todoItem1 = TodoItem.create(200L, "Einkaufen für Geburtstag");
        final TodoItem todoItem2 = TodoItem.create(400L, "Einkaufen für Oma");

        //POST 1
        HttpRequest requestNewTodo1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem1)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response1 = HttpClient.newHttpClient().send(requestNewTodo1, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(202, response1.statusCode());

        //POST 2
        HttpRequest requestNewTodo2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem2)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response2 = HttpClient.newHttpClient().send(requestNewTodo2, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(202, response2.statusCode());

        // GET With filter einkaufen
        HttpRequest requestTodoSearch = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos?description=einkaufen"))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> searchResponse1 = HttpClient.newHttpClient().send(requestTodoSearch, HttpResponse.BodyHandlers.ofString());

        //checkt das Header content type = application/json;charset=utf-8
        Assert.assertEquals("application/json;charset=utf-8", searchResponse1.headers().firstValue("content-type").get());

        //Response a List of all Todos with description einkaufen
        final List<TodoItem> foundTodos = new JSONSerializer().deserialize(searchResponse1.body(), new TypeReference<>() { });

        //check if all founded Todos with description einkaufen contains einkaufen
        for (TodoItem item: foundTodos) {
            System.out.println(item);
            Assert.assertTrue(item.description.toLowerCase(Locale.ROOT).contains("einkaufen"));
        }
    }

    @Test
    public void getFilterTodos_should_returnTrueIfFoundTodosSize3() throws URISyntaxException, IOException, InterruptedException {

        final TodoItem todoItem1 = TodoItem.create(200L, "Einkaufen für Geburtstag");
        final TodoItem todoItem2 = TodoItem.create(400L, "Einkaufen für Oma");

        //POST 1
        HttpRequest requestNewTodo1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem1)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response1 = HttpClient.newHttpClient().send(requestNewTodo1, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(202, response1.statusCode());

        //POST 2
        HttpRequest requestNewTodo2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem2)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response2 = HttpClient.newHttpClient().send(requestNewTodo2, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(202, response2.statusCode());


        // GET With filter einkaufen
        HttpRequest requestTodoSearch = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos?description=einkaufen"))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> searchResponse1 = HttpClient.newHttpClient().send(requestTodoSearch, HttpResponse.BodyHandlers.ofString());

        //checkt das Header content type = application/json;charset=utf-8
        Assert.assertEquals("application/json;charset=utf-8", searchResponse1.headers().firstValue("content-type").get());

        //Response a List of all Todos with description einkaufen
        final List<TodoItem> foundTodos = new JSONSerializer().deserialize(searchResponse1.body(), new TypeReference<>() { });


        Assert.assertTrue((foundTodos.size() == 3));

    }
    @Test
    public void getFilterTodosWithUppercaseFilter_should_returnTrueIfFoundTodosSize3() throws URISyntaxException, IOException, InterruptedException {

        final TodoItem todoItem1 = TodoItem.create(200L, "Einkaufen für Geburtstag");
        final TodoItem todoItem2 = TodoItem.create(400L, "Einkaufen für Oma");

        //POST 1
        HttpRequest requestNewTodo1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem1)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response1 = HttpClient.newHttpClient().send(requestNewTodo1, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(202, response1.statusCode());

        //POST 2
        HttpRequest requestNewTodo2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .POST(HttpRequest.BodyPublishers.ofString(new JSONSerializer().serialize(todoItem2)))
                .header("accept", "application/json")
                .build();
        final HttpResponse<String> response2 = HttpClient.newHttpClient().send(requestNewTodo2, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(202, response2.statusCode());


        // GET With filter einkaufen
        HttpRequest requestTodoSearch = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos?description=EINKAUFEN"))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> searchResponse1 = HttpClient.newHttpClient().send(requestTodoSearch, HttpResponse.BodyHandlers.ofString());

        //checkt das Header content type = application/json;charset=utf-8
        Assert.assertEquals("application/json;charset=utf-8", searchResponse1.headers().firstValue("content-type").get());

        //Response a List of all Todos with description einkaufen
        final List<TodoItem> foundTodos = new JSONSerializer().deserialize(searchResponse1.body(), new TypeReference<>() { });


        Assert.assertTrue((foundTodos.size() == 3));

    }
    @Test
    public void getFilterTodos_should_returnCode404NoTodoFoundWithThisDescription() throws URISyntaxException, IOException, InterruptedException {

        HttpRequest requestTodoSearch = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos?description=hund"))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> searchResponse = HttpClient.newHttpClient().send(requestTodoSearch, HttpResponse.BodyHandlers.ofString());

        String bodyMessage = new JSONSerializer().deserialize(searchResponse.body(), new TypeReference<>() { });

        Assert.assertEquals(404, searchResponse.statusCode());
        Assert.assertEquals("No Todo with this description was found!", bodyMessage);

    }
    @Test
    public void getFilterTodos_should_returnCode404EmptyDescription() throws URISyntaxException, IOException, InterruptedException {

        HttpRequest requestTodoSearch = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos?description="))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> searchResponse = HttpClient.newHttpClient().send(requestTodoSearch, HttpResponse.BodyHandlers.ofString());

        String bodyMessage = new JSONSerializer().deserialize(searchResponse.body(), new TypeReference<>() { });

        Assert.assertEquals(404, searchResponse.statusCode());
        Assert.assertEquals("No Todo with this description was found!", bodyMessage);

    }




    }

