package taskManager;
import task.*;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Task updateTask(Task task);

    void deleteTask(Integer taskId);

    Epic createEpic(Epic epic);

    Epic updateEpic(Epic epic);

    void deleteEpic(Integer epicId);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(Subtask subtask);

    void deleteSubtask(Integer subtaskId);

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getEpicSubtasks(Integer epicId);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    void updateEpicStatus(Integer epicId);

    List<Task> getHistory();

}
