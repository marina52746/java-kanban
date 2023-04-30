package server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import manager.Managers;
import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import taskManager.TaskManager;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;



class HttpTaskServerTest {
    private HttpTaskServer taskServer;
    private TaskManager taskManager;
    private KVServer kvServer;
    private final Gson gson = Managers.getGson();
    private Task task;
    private Epic epic;
    private Subtask subtask;
    @BeforeEach
    void setUp() throws IOException, ManagerSaveException {
        kvServer = Managers.getKvServer();
        kvServer.start();
        taskManager = Managers.getDefault();
        taskServer = new HttpTaskServer(8080,taskManager);
        taskServer.start();
        task = new Task("task", "description", 15, TaskStatus.NEW, 90,
                LocalDateTime.of(2022,3,30,10,20));
        task = taskManager.createTask(task);
        epic = new Epic("epic", "descriptionepic", 3);
        epic = taskManager.createEpic(epic);
        subtask = new Subtask("subtask1", "descriptionsubtask1", 10, TaskStatus.NEW, 90,
                LocalDateTime.of(2022,5,3,14,50), 3);
        subtask = taskManager.createSubtask(subtask);
    }

    @AfterEach
    void tearDown() throws IOException {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void createUpdateTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        Gson gson = new Gson();
        Task task1 = new Task("task1", "description1", 5, TaskStatus.DONE, 950,
                LocalDateTime.of(2022,3,12,10,50));
        String json = gson.toJson(new Task.Dto(task1));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(2,taskManager.getAllTasks().size());
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Tasks don't return");
        assertEquals(1, actual.size(), "Incorrect number of tasks");
        assertEquals(task, actual.get(0), "Tasks not the same");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=15");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<Task>(){}.getType();
        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Tasks don't return");
        assertEquals(task, actual, "Tasks not the same");
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=15");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void createUpdateSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        Gson gson = new Gson();
        Subtask subtask = new Subtask("subtask1", "descriptionsub1", 7, TaskStatus.IN_PROGRESS,
                50, LocalDateTime.of(2022,11,10,11,10), 3);
        String json = gson.toJson(new Task.Dto(subtask));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(2,taskManager.getAllSubtasks().size());
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type subtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> actual = gson.fromJson(response.body(), subtaskType);
        assertNotNull(actual, "Subtasks don't return");
        assertEquals(1, actual.size(), "Incorrect number of subtasks");
    }

    @Test
    void getEpicSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type subtaskType = new TypeToken<Subtask>(){}.getType();
        Subtask actual = gson.fromJson(response.body(), subtaskType);
        assertNotNull(actual, "Subtasks doesn't return");
    }

    @Test
    void deleteSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=10");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void createUpdateEpic() throws IOException, InterruptedException, ManagerSaveException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        Gson gson = new Gson();
        Epic epic2 = new Epic("epic2", "descriptionepic2", 9);
        String json = gson.toJson(new Task.Dto(epic2));
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(2,taskManager.getAllEpics().size());
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type epicType = new TypeToken<ArrayList<Epic>>(){}.getType();
        List<Epic> actual = gson.fromJson(response.body(), epicType);
        assertNotNull(actual, "Epics don't return");
        assertEquals(1, actual.size(), "Incorrect number of epics");
        assertEquals(epic, actual.get(0), "Epics not the same");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type epicType = new TypeToken<Epic>(){}.getType();
        Epic actual = gson.fromJson(response.body(), epicType);
        assertNotNull(actual, "Epics don't return");
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getHistory() throws IOException, InterruptedException, ManagerSaveException {
        taskManager.getTaskById(15);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "History doesn't return");
        assertEquals(1, actual.size(), "Incorrect number of tasks");
        assertEquals(task, actual.get(0), "Tasks not the same");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Tasks don't return");
        assertEquals(3, actual.size(), "Incorrect number of tasks");
    }
}