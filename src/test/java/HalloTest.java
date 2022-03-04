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
    public void testHello_should_return200OK() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:4567/todos"))
                .GET()
                .header("accept", "application/json")
                .build();

        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//checkt das Header content type = application/json;charset=utf-8
        Assert.assertEquals("application/json;charset=utf-8", response.headers().firstValue("content-type").get());

        final List<TodoItem> todos = new JSONSerializer().deserialize(response.body(), new TypeReference<List<TodoItem>>() {});

        Assert.assertEquals(5, todos.size());
    }
}

