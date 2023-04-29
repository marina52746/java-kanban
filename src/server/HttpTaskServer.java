package server;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exceptions.ManagerSaveException;
import manager.Managers;
import task.Epic;
import task.Subtask;
import task.Task;
import taskManager.TaskManager;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import java.util.*;

public class HttpTaskServer implements Closeable {
    private static final int PORT = 8080;
    private static final String CONTENT_TYPE = "Content-type";
    private static final String APPLICATION_JSON = "application/json;charset=utf8";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String DELETE = "DELETE";
    private static final String TASKS_ENDPOINT = "/tasks";
    private final TaskManager taskManager;
    private final HttpServer httpServer;
    private Gson gson;

    public HttpTaskServer() throws IOException {
        this(HttpTaskServer.PORT,Managers.getDefault());
    }

    public HttpTaskServer(int port, TaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost",port), 0);
        this.taskManager = taskManager;
        this.httpServer.createContext(TASKS_ENDPOINT, this::tasksEndpoint);
        this.gson = Managers.getGson();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    @Override
    public void close() throws IOException { //???
        httpServer.stop(0);
        System.out.println("Server stopped");
    }

    public void start() {
        System.out.println("Starting server");
        httpServer.start();
    }

    public void stop() throws IOException {
        httpServer.stop(0);
        System.out.println("Server stopped");
    }

    private void tasksEndpoint(HttpExchange httpExchange) {
        try {
            String method = httpExchange.getRequestMethod();
            String uri = String.valueOf(httpExchange.getRequestURI());
            if (uri.startsWith("/tasks/task/") && method.equals(POST)) {
                createUpdateTask(httpExchange);
            } else if (uri.startsWith("/tasks/epic/") && !uri.contains("update-status")
                    && !uri.contains("update-times") && method.equals(POST)) {
                createUpdateEpic(httpExchange);
            } else if (uri.startsWith("/tasks/subtask/") && method.equals(POST)) {
                createUpdateSubtask(httpExchange);
            } else if (uri.startsWith("/tasks/task/?id=")  && method.equals(GET)) {
                getTask(httpExchange, parseId(uri.replace("/tasks/task/?id=","")));
            } else if (uri.startsWith("/tasks/epic/?id=")  && method.equals(GET)) {
                getEpic(httpExchange, parseId(uri.replace("/tasks/epic/?id=","")));
            } else if (uri.startsWith("/tasks/subtask/?id=")  && method.equals(GET)) {
                getSubtask(httpExchange, parseId(uri.replace("/tasks/subtask/?id=","")));
            } else if (uri.startsWith("/tasks/task/?id=")  && method.equals(DELETE)) {
                deleteTask(httpExchange, parseId(uri.replace("/tasks/task/?id=","")));
            } else if (uri.startsWith("/tasks/epic/?id=")  && method.equals(DELETE)) {
                deleteEpic(httpExchange, parseId(uri.replace("/tasks/epic/?id=","")));
            } else if (uri.startsWith("/tasks/subtask/?id=")  && method.equals(DELETE)) {
                deleteSubtask(httpExchange, parseId(uri.replace("/tasks/subtask/?id=","")));
            } else if (uri.equals("/tasks/task/") && method.equals(GET)) {
                getTasks(httpExchange);
            } else if (uri.equals("/tasks/") && method.equals(GET)) {
                getPrioritizedTasks(httpExchange);
            } else if (uri.equals("/tasks/epic/") && method.equals(GET)) {
                getEpics(httpExchange);
            } else if (uri.equals("/tasks/subtask/") && method.equals(GET)) {
                getSubtasks(httpExchange);
            } else if (uri.contains("/tasks/subtask/epic/?id=") && method.equals(GET)) {
                getEpicSubtasks(httpExchange, parseId(uri.replace("/tasks/subtask/epic/?id=","")));
            } else if (uri.equals("/tasks/task/")  && method.equals(DELETE)) {
                deleteTasks(httpExchange);
            } else if (uri.equals("/tasks/epic/")  && method.equals(DELETE)) {
                deleteEpics(httpExchange);
            } else if (uri.equals("/tasks/subtask/")  && method.equals(DELETE)) {
                deleteSubtasks(httpExchange);
            } else if (uri.equals("/tasks/history/")  && method.equals(GET)) {
                getHistory(httpExchange);
            }

        } catch (IOException e) {
            System.out.println("Error on performing request");
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private void getHistory(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> tasks = taskManager.getHistory();
            String jsonArray = gson.toJson(tasks);
            writeJsonResponse(httpExchange, jsonArray);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void createUpdateSubtask(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try {
            Task.Dto subtaskToCreate = gson.fromJson(body, Task.Dto.class);
            Subtask added = taskManager.createSubtask((Subtask)subtaskToCreate.toTask());
            //Subtask added = taskManager.createSubtask((Subtask)Task.Dto.toTask(subtaskToCreate));
            writeJsonResponse(exchange, gson.toJson(new Task.Dto(added)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void createUpdateEpic(HttpExchange exchange) throws  IOException {
        String body = readBody(exchange);
        try {
            Task.Dto epicToCreate = gson.fromJson(body, Task.Dto.class);
            Epic added = taskManager.createEpic((Epic)epicToCreate.toTask());
            writeJsonResponse(exchange, gson.toJson(new Task.Dto(added)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }
    private int parseId(String idStr) {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }

    }

    private void getTasks(HttpExchange httpExchange) throws IOException {
        try {
            List<Task> tasks = taskManager.getAllTasks();
            String jsonArray = gson.toJson(tasks);
            writeJsonResponse(httpExchange, jsonArray);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void getPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        try {
            Set<Task> tasks = taskManager.getPrioritizedTasks();
            String jsonArray = gson.toJson(tasks);
            writeJsonResponse(httpExchange, jsonArray);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void getEpics(HttpExchange httpExchange) throws IOException {
        try {
            List<Epic> epics = taskManager.getAllEpics();
            String jsonArray = gson.toJson(epics);
            writeJsonResponse(httpExchange, jsonArray);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void getSubtasks(HttpExchange httpExchange) throws IOException {
        try {
            List<Subtask> subtasks = taskManager.getAllSubtasks();
            String jsonArray = gson.toJson(subtasks);
            writeJsonResponse(httpExchange, jsonArray);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void getTask(HttpExchange httpExchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                notFound(httpExchange);
                return;
            }
            writeJsonResponse(httpExchange, gson.toJson(new Task.Dto(task)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void getEpic(HttpExchange httpExchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                notFound(httpExchange);
                return;
            }
            writeJsonResponse(httpExchange, gson.toJson(new Task.Dto(epic)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void getSubtask(HttpExchange httpExchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) {
                notFound(httpExchange);
                return;
            }
            writeJsonResponse(httpExchange, gson.toJson(new Task.Dto(subtask)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private void createUpdateTask(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);
        try {
            Task.Dto taskToCreate = gson.fromJson(body, Task.Dto.class);
            Task added = taskManager.createTask(taskToCreate.toTask());
            writeJsonResponse(exchange, gson.toJson(new Task.Dto(added)));
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void deleteTask(HttpExchange exchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            taskManager.deleteTask(id);
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void deleteTasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllTasks();
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            taskManager.deleteEpic(id);
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void deleteEpics(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllEpics();
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void deleteSubtask(HttpExchange exchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            taskManager.deleteSubtask(id);
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void deleteSubtasks(HttpExchange exchange) throws IOException {
        try {
            taskManager.deleteAllSubtasks();
            exchange.sendResponseHeaders(200, 0);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(exchange);
        }
    }

    private void getEpicSubtasks(HttpExchange httpExchange, int id) throws IOException {
        try {
            if (id == -1) {
                throw new ManagerSaveException("Incorrect id");
            }
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                notFound(httpExchange);
                return;
            }
            List<Integer> subtasksIds = epic.getSubtasksIds();
            String jsonArray = gson.toJson(subtasksIds);
            writeJsonResponse(httpExchange, jsonArray);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            internalError(httpExchange);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void writeJsonResponse(HttpExchange exchange, String stringJson) throws IOException {
        writeResponse(exchange, APPLICATION_JSON, stringJson);
    }

    private static void writeResponse(HttpExchange exchange, String contentType, String text) throws IOException {
        exchange.getResponseHeaders().add(CONTENT_TYPE, contentType);
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
    }

    private void badRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
    }

    private void notFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
    }

    private void internalError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, 0);
    }

}
