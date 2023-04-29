package server;
import manager.Managers;
import task.Task;
import task.TaskStatus;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class KVTaskClient {
    private final HttpClient client;
    private final URI url;
    private final String API_TOKEN;
    public KVTaskClient(String uri) {
        client = HttpClient.newHttpClient();
        url = URI.create(uri);
        API_TOKEN = "DEBUG"; //todo
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //tests
        KVTaskClient client = new KVTaskClient("http://localhost:8078/");
        client.put("key1", Managers.getGson().toJson(new Task("t1", "d1", TaskStatus.NEW,
                700, LocalDateTime.now())));
        client.load("key1");
        client.put("key1", Managers.getGson().toJson(new Task("t7", "d7", TaskStatus.DONE,
                900, LocalDateTime.now())));
        client.load("key1");
        client.put("key9", Managers.getGson().toJson(new Task("t22", "d22", TaskStatus.DONE,
                200, LocalDateTime.now())));
        client.load("key9");
    }

    public String load(String key) throws IOException, InterruptedException {
        // GET /load/<ключ>?API_TOKEN=
        String uri = url.toString() + "load/" + key + "?API_TOKEN=" + API_TOKEN;
        URI url1 = URI.create(uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.toString();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        // POST /save/<ключ>?API_TOKEN=
        String uri = url.toString() + "save/" + key + "?API_TOKEN=" + API_TOKEN;
        URI url2 = URI.create(uri);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url2).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
