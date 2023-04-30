package taskManager;
import com.google.gson.Gson;
import exceptions.ManagerSaveException;
import manager.Managers;
import server.KVTaskClient;
import task.Epic;
import task.Subtask;
import task.Task;
import task.Task.Dto;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String KEY = "key-saving-task-manager-state";
    private final KVTaskClient kvClient;

    public HttpTaskManager(String kvUrl, boolean needLoad) {
        super(null);
        this.kvClient = new KVTaskClient(kvUrl);
        if (needLoad)
            load();
    }
    public void load() {
        try {
            String value = kvClient.load(KEY);
            StateDto stateDto = StateDto.from(value);
            stateDto.tasks
                    .stream()
                    .map(Task.Dto::toTask)
                    .forEach(task -> this.tasksById.put(task.getId(), task));
            stateDto.subtasks
                    .stream()
                    .map(Task.Dto::toTask)
                    .forEach(subtask -> this.subtasksById.put(subtask.getId(), (Subtask) subtask));
            stateDto.epics
                    .stream()
                    .map(Task.Dto::toTask)
                    .forEach(epic -> this.epicsById.put(epic.getId(), (Epic) epic));
            stateDto.history
                    .stream()
                    .map(Task.Dto::toTask)
                    .forEach(task -> this.getHistory().add(task.getId(), task));
        }   catch (Exception e) {
            System.out.println("State not found");
        }
    }

    @Override
    public void save() throws ManagerSaveException {
        try {
            kvClient.put(KEY, new StateDto(tasksById.values(), subtasksById.values(),
                    epicsById.values(), getHistory()).asString());
        } catch (Exception e) {
            throw new ManagerSaveException("Can't save values on KVServer");
        }
    }

    private static final class StateDto {
        private static final Gson GSON = Managers.getGson();
        private final List<Dto> tasks;
        private final List<Dto> subtasks;
        private final List<Dto> epics;
        private final List<Dto> history;
        private StateDto(Collection<Task> tasks, Collection<Subtask> subtasks, Collection<Epic> epics, Collection<Task> history) {
            this.tasks = tasks
                    .stream()
                    .map(Dto::cons)
                    .collect(Collectors.toList());
            this.subtasks = subtasks
                    .stream()
                    .map(Dto::cons)
                    .collect(Collectors.toList());
            this.epics = epics
                    .stream()
                    .map(Dto::cons)
                    .collect(Collectors.toList());
            this.history = history
                    .stream()
                    .map(Dto::cons)
                    .collect(Collectors.toList());
        }

        private static StateDto from(String json) {
            return GSON.fromJson(json, StateDto.class);
        }

        private String asString() {
            return GSON.toJson(this);
        }
    }

}
