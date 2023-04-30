package manager;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.ManagerSaveException;
import historyManager.*;
import server.HttpTaskServer;
import server.KVServer;
import taskManager.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {
    private static final int TASK_SERVER_PORT = 8080;
    private static final int KEY_VALUE_SERVER_PORT = 8078;
    private static final String KEY_VALUE_URL = "http://localhost:" + KEY_VALUE_SERVER_PORT + "/";
    private static HttpTaskServer taskServer;
    private static KVServer kvServer;
    private static Gson gson;

    public static TaskManager getDefault() {
        return getServerTaskManager();
    }

    public static TaskManager getInMemory() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getBacked() {
        return new FileBackedTasksManager(new File("resources/tasks_file.csv"));
    }

    public static TaskManager getServerTaskManager()  {
        return new HttpTaskManager(KEY_VALUE_URL, true);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static HttpTaskServer getTaskServer() throws IOException, ManagerSaveException {
        if (taskServer == null) {
            taskServer = new HttpTaskServer(TASK_SERVER_PORT, getServerTaskManager());
        }
        return taskServer;
    }

    public static KVServer getKvServer() throws IOException {
        if (kvServer == null) {
            kvServer = new KVServer(KEY_VALUE_SERVER_PORT);
        }
        return kvServer;
    }

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
            return gsonBuilder.create();
        }
        return gson;
    }
}