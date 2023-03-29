package taskManager;
import exceptions.ManagerSaveException;
import historyManager.HistoryManager;
import manager.Managers;
import task.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    static int currentTaskId;
    final HistoryManager historyManager = Managers.getDefaultHistory();
    public final Map<Integer, Task> tasksById = new HashMap<>();
    public final Map<Integer, Epic> epicsById = new HashMap<>();
    public final Map<Integer, Subtask> subtasksById = new HashMap<>();

    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        task.setId(++currentTaskId);
        tasksById.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        if (!tasksById.containsKey(task.getId())) {
            System.out.println("Задачи с таким номером не существует. Обновление невозможно.");
            return null;
        }
        tasksById.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteTask(Integer taskId) throws ManagerSaveException {
        if (tasksById.get(taskId) == null) {
            System.out.println("Нельзя удалить задачу с номером " + taskId + ", её нет в списке");
            return;
        }
        tasksById.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        epic.setId(++currentTaskId);
        epicsById.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        final Epic updateEpic = epicsById.get(epic.getId());
        if (updateEpic == null) {
            System.out.println("Эпика с таким номером не существует. Обновление невозможно.");
            return null;
        }
        updateEpic.setDescription(epic.getDescription());
        updateEpic.setName(epic.getName());
        return updateEpic;
    }

    @Override
    public void deleteEpic(Integer epicId) throws ManagerSaveException {
        final Epic epic = epicsById.remove(epicId);
        if (epic == null) {
            System.out.println("Нельзя удалить эпик с номером " + epicId + ", его нет в списке");
            return;
        }
        if (epic.getSubtasksIds() != null) {
            for (Integer subtaskId : epic.getSubtasksIds()) {
                subtasksById.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
        }
        historyManager.remove(epicId);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws ManagerSaveException {
        subtask.setId(++currentTaskId);
        subtasksById.put(subtask.getId(), subtask);
        epicsById.get(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws ManagerSaveException {
        final Subtask oldSubtask = subtasksById.get(subtask.getId());
        if (oldSubtask == null) {
            System.out.println("Подзадачи с таким номером не существует. Обновление невозможно.");
            return null;
        }
        if (!Objects.equals(subtask.getEpicId(), oldSubtask.getEpicId())) {
            final Epic oldEpic = epicsById.get(oldSubtask.getEpicId());
            final Epic newEpic = epicsById.get(subtask.getEpicId());
            oldEpic.getSubtasksIds().remove((Integer)subtask.getId());
            newEpic.getSubtasksIds().add(subtask.getId());
            updateEpicStatus(oldEpic.getId());
            updateEpicStatus(newEpic.getId());
        }
        subtasksById.put(subtask.getId(), subtask);
        return subtask;
    }

    @Override
    public void deleteSubtask(Integer subtaskId) throws ManagerSaveException {
        final Subtask subtask = subtasksById.remove(subtaskId);
        if (subtask == null) {
            System.out.println("Нельзя удалить подзадачу с номером " + subtaskId + ", её нет в списке");
            return;
        }
        final Epic epic = epicsById.get(subtask.getEpicId());
        if (epic.getSubtasksIds() != null) {
            epic.getSubtasksIds().remove(subtaskId);
        }
        updateEpicStatus(epic.getId());
        historyManager.remove(subtaskId);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasksById.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epicsById.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasksById.values());
    }

    @Override
    public List<Subtask> getEpicSubtasks(Integer epicId) {
        if (epicsById.size() != 0 && epicsById.get(epicId) != null
                && epicsById.get(epicId).getSubtasksIds().size() != 0) {
            List<Subtask> subtasks = new ArrayList<>();
            for (Integer subtaskId : epicsById.get(epicId).getSubtasksIds()) {
                subtasks.add(subtasksById.get(subtaskId));
            }
            return subtasks;
        } else {
            return null;
        }
    }

    @Override
    public void deleteAllTasks() throws ManagerSaveException {
        for(int id : tasksById.keySet()) {
            historyManager.remove(id);
        }
        tasksById.clear();
    }

    @Override
    public void deleteAllEpics() throws ManagerSaveException {
        for(int id : epicsById.keySet()) {
            historyManager.remove(id);
        }
        epicsById.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() throws ManagerSaveException {
        for (Epic epic : epicsById.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic.getId());
        }
        for(int id : subtasksById.keySet()) {
            historyManager.remove(id);
        }
        subtasksById.clear();
    }

    @Override
    public Task getTaskById(int id) throws ManagerSaveException {
        final Task task = tasksById.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) throws ManagerSaveException {
        final Subtask subtask = subtasksById.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) throws ManagerSaveException {
        final Epic epic = epicsById.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void updateEpicStatus(Integer epicId) throws ManagerSaveException {
        int statusCounter = 0;
        final Epic epic = epicsById.get(epicId);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            final TaskStatus status = subtasksById.get(subtaskId).getStatus();
            if (status == TaskStatus.NEW) {
                statusCounter++;
            } else if (status == TaskStatus.DONE) {
                statusCounter--;
            } else {
                epic.setStatusInProgress();
                return;
            }
        }
        if (statusCounter == epic.getSubtasksIds().size()) {
            epic.setStatusNew();
        } else if (statusCounter == -epic.getSubtasksIds().size()) {
            epic.setStatusDone();
        } else {
            epic.setStatusInProgress();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
