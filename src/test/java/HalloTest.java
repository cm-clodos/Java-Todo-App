import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class HalloTest {

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

        // das enthaltene JSON deserialiseren und TodoItem Objekte in List abfüllen. Kann Anzahl items getestet werden
        final List<TodoItem> todos = new JSONSerializer().deserialize(response.body(), new TypeReference<List<TodoItem>>() {});

        Assert.assertEquals(5, todos.size());
    }
    @Test
    public void deleteTodos_should_ReturnContentTypeStatusCode200() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos/1"))
                .DELETE()
                .header("accept", "application/json")
                .build();



        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());
        Assert.assertEquals(200, response.statusCode());



    }
    @Test
    public void deleteTodos_should_ReturnStatusCode406() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos/0"))
                .DELETE()
                .header("accept", "application/json")
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(406, response.statusCode());
    }
}

