package TaskManager;
import HistoryManager.HistoryManager;
import Managers.Managers;
import Task.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private int currentTaskId = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private HashMap<Integer, Task> tasksById = new HashMap<>();
    private HashMap<Integer, Epic> epicsById = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksById = new HashMap<>();

    @Override
    public Task createTask(Task task) {
        task.setId(++currentTaskId);
        tasksById.put(task.getId(), task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasksById.get(task.getId()) == null) {
            System.out.println("Задачи с таким номером не существует. Обновление невозможно.");
            return null;
        }
        tasksById.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteTask(Integer taskId) {
        if (tasksById.get(taskId) == null) {
            System.out.println("Нельзя удалить задачу с номером " + taskId + ", её нет в списке");
            return;
        }
        tasksById.remove(taskId);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(++currentTaskId);
        epicsById.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epicsById.get(epic.getId()) == null) {
            System.out.println("Эпика с таким номером не существует. Обновление невозможно.");
            return null;
        }
        List<Subtask> oldEpicSubtasks;
        oldEpicSubtasks = getEpicSubtasks(epic.getId());
        epic.setSubtasksIds(epic.getSubtasksIds());
        epicsById.put(epic.getId(), epic);
        if (oldEpicSubtasks != null) {
            for (Subtask subtask : oldEpicSubtasks) {
                if (!epic.getSubtasksIds().contains(subtask.getId()) && subtasksById.get(subtask.getId()) != null) {
                    if (subtasksById.get(subtask.getId()).getEpicId() == epic.getId()) {
                        subtasksById.remove(subtask.getId());
                    } else {
                        updateSubtask(subtask);
                    }
                }
            }
        }
        if (getEpicSubtasks(epic.getId()) != null) {
            for (Subtask newSubtask : getEpicSubtasks(epic.getId())) {
                if (subtasksById.get(newSubtask.getId()) == null) {
                    subtasksById.put(newSubtask.getId(), newSubtask);
                } else {
                    updateSubtask(newSubtask);
                }
            }
        }
        updateEpicStatus(epic.getId());
        return epic;
    }

    @Override
    public void deleteEpic(Integer epicId) {
        if (epicsById.get(epicId) == null) {
            System.out.println("Нельзя удалить эпик с номером " + epicId + ", его нет в списке");
            return;
        }
        if (epicsById.get(epicId).getSubtasksIds() != null) {
            for (Integer subtaskId : epicsById.get(epicId).getSubtasksIds()) {
                subtasksById.remove(subtaskId);
            }
        }
        epicsById.remove(epicId);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++currentTaskId);
        subtasksById.put(subtask.getId(), subtask);
        epicsById.get(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtasksById.get(subtask.getId()) == null) {
            System.out.println("Подзадачи с таким номером не существует. Обновление невозможно.");
            return null;
        }
        if (subtasksById.get(subtask.getId()) != null
                && subtasksById.get(subtask.getId()).getOldEpicId() != 0
                && subtasksById.get(subtask.getId()).getOldEpicId() != subtask.getEpicId()) {
            Epic oldEpic = epicsById.get(subtasksById.get(subtask.getId()).getOldEpicId());
            for (int i = 0; i < oldEpic.getSubtasksIds().size(); i++) {
                if (oldEpic.getSubtasksIds().get(i) == subtask.getId()) {
                    oldEpic.getSubtasksIds().remove(i);
                    break;
                }
            }
            updateEpicStatus(oldEpic.getId());
        }
        subtasksById.put(subtask.getId(), subtask);
        if (epicsById.get(subtask.getEpicId()).getSubtasksIds() != null
            && !epicsById.get(subtask.getEpicId()).getSubtasksIds().contains(subtask.getId())) {
            epicsById.get(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        }
        updateEpicStatus(subtask.getEpicId());
        return subtask;
    }

    @Override
    public void deleteSubtask(Integer subtaskId) {
        if (subtasksById.get(subtaskId) == null) {
            System.out.println("Нельзя удалить подзадачу с номером " + subtaskId + ", её нет в списке");
            return;
        }
        if (epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds() != null) {
            for (int i = 0; i < epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds().size(); i++) {
                if (epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds().get(i) == subtaskId) {
                    epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds().remove(i);
                    break;
                }
            }
        }
        updateEpicStatus(subtasksById.get(subtaskId).getEpicId());
        subtasksById.remove(subtaskId);
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasksById.values());
        return allTasks;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>(epicsById.values());
        return allEpics;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> allSubtasks = new ArrayList<>(subtasksById.values());
        return allSubtasks;
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
    public void deleteAllTasks() {
        tasksById.clear();
    }

    @Override
    public void deleteAllEpics() {
        epicsById.clear();
        deleteAllSubtasks();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epicsById.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic.getId());
        }
        subtasksById.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasksById.get(id));
        return tasksById.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasksById.get(id));
        return subtasksById.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicsById.get(id));
        return epicsById.get(id);
    }

    @Override
    public void updateEpicStatus(Integer epicId) {
        int statusCounter = 0;
        if (epicsById.get(epicId).getSubtasksIds() != null) {
            for (Integer subtaskId : epicsById.get(epicId).getSubtasksIds()) {
                if (subtasksById.get(subtaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                    statusCounter += 1;
                } else if (subtasksById.get(subtaskId).getStatus() == TaskStatus.DONE) {
                    statusCounter += 2;
                }
            }
        }
        if (statusCounter == 0) {
            epicsById.get(epicId).setStatusNew();
        }
        else if (epicsById.get(epicId).getSubtasksIds().size() * 2 == statusCounter) {
            epicsById.get(epicId).setStatusDone();
        } else {
            epicsById.get(epicId).setStatusInProgress();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
