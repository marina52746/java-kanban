package server;
import exceptions.LoadingFromServerException;
import exceptions.SavingToServerException;
import manager.Managers;
import task.Task;
import task.TaskStatus;
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
        API_TOKEN = getAPI_TOKEN();
    }

    public static void main(String[] args) {
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

    public String load(String key) {
        HttpResponse<String> response = null;
        try {
            String uri = url.toString() + "load/" + key + "?API_TOKEN=" + API_TOKEN;
            URI url1 = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder().uri(url1).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() > 299) {
                throw new LoadingFromServerException("Can't load from server, status code = " + response.statusCode());
            }
        } catch (Exception e) {
            if (response.statusCode() >= 400 && response.statusCode() <= 499)
                System.out.println("Client error");
            else if (response.statusCode() >= 500 && response.statusCode() <= 599)
                System.out.println("Server error");
            else System.out.println("Undefined error");
        }
        return response.toString();
    }

    public void put(String key, String json) {
        HttpResponse<String> response = null;
        try {
            String uri = url.toString() + "save/" + key + "?API_TOKEN=" + API_TOKEN;
            URI url2 = URI.create(uri);
            final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
            HttpRequest request = HttpRequest.newBuilder().uri(url2).POST(body).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() > 299) {
                throw new SavingToServerException("Can't save on server, status code = " + response.statusCode());
            }
        } catch (Exception e) {
            if (response.statusCode() >= 400 && response.statusCode() <= 499)
                System.out.println("Client error");
            else if (response.statusCode() >= 500 && response.statusCode() <= 599)
                System.out.println("Server error");
            else System.out.println("Undefined error");
        }
    }

    private String getAPI_TOKEN() {
        try {
            String uri = url.toString() + "register/";
            URI url3 = URI.create(uri);
            HttpRequest request = HttpRequest.newBuilder().uri(url3).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.out.println("Failed getApiToken");
        }
        return "DEBUG";
    }
}
