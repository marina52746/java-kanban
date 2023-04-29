package taskManager;
import exceptions.ManagerSaveException;
import task.*;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    Task createTask(Task task) throws ManagerSaveException;

    Task updateTask(Task task) throws ManagerSaveException;

    void deleteTask(Integer taskId) throws ManagerSaveException;

    Epic createEpic(Epic epic) throws ManagerSaveException;

    Epic updateEpic(Epic epic) throws ManagerSaveException;

    void deleteEpic(Integer epicId) throws ManagerSaveException;

    Subtask createSubtask(Subtask subtask) throws ManagerSaveException;

    Subtask updateSubtask(Subtask subtask) throws ManagerSaveException;

    void deleteSubtask(Integer subtaskId) throws ManagerSaveException;

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    List<Subtask> getEpicSubtasks(Integer epicId);

    void deleteAllTasks() throws ManagerSaveException;

    void deleteAllEpics() throws ManagerSaveException;

    void deleteAllSubtasks() throws ManagerSaveException;

    Task getTaskById(int id) throws ManagerSaveException;

    Subtask getSubtaskById(int id) throws ManagerSaveException;

    Epic getEpicById(int id) throws ManagerSaveException;

    void updateEpicStatus(Integer epicId) throws ManagerSaveException;

    void updateDurationEpicStartTimeEndTime(Integer epicId) throws ManagerSaveException;

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks() throws ManagerSaveException;

}
