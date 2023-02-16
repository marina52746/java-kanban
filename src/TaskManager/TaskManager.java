package TaskManager;
import HistoryManager.HistoryManager;
import Task.*;

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

    Task getTaskById(int id, HistoryManager historyManager);

    Subtask getSubtaskById(int id, HistoryManager historyManager);

    Epic getEpicById(int id, HistoryManager historyManager);

    void updateEpicStatus(Integer epicId);

}
