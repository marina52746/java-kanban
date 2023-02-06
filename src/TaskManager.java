import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TaskManager {
    private int currentTaskId = 0;

    private HashMap<Integer, Task> tasksById = new HashMap<>();
    private HashMap<Integer, Epic> epicsById = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksById = new HashMap<>();

    public Task createTask(Task task) {
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        newTask.setId(++currentTaskId);
        tasksById.put(newTask.getId(), newTask);
        return newTask;
    }

    public Task updateTask(Task task) {
        if (tasksById.get(task.getId()) == null) {
            System.out.println("Задачи с таким номером не существует. Обновление невозможно.");
            return null;
        }
        Task updatedTask = new Task(task.getName(), task.getDescription(), task.getId(), task.getStatus());
        tasksById.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }

    public void deleteTask(Integer taskId) {
        if (tasksById.get(taskId) == null) {
            System.out.println("Нельзя удалить задачу с номером " + taskId + ", её нет в списке");
            return;
        }
        tasksById.remove(taskId);
    }

    public Epic createEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getStatus());
        newEpic.setId(++currentTaskId);
        epicsById.put(newEpic.getId(), newEpic);
        return newEpic;
    }

    public Epic updateEpic(Epic epic) {
        if (epicsById.get(epic.getId()) == null) {
            System.out.println("Эпика с таким номером не существует. Обновление невозможно.");
            return null;
        }
        ArrayList<Subtask> oldEpicSubtasks;
        oldEpicSubtasks = getEpicSubtasks(epic.getId());
        Epic updatedEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus());
        updatedEpic.setSubtasksIds(epic.getSubtasksIds());
        epicsById.put(updatedEpic.getId(), updatedEpic);
        if (oldEpicSubtasks != null) {
            for (Subtask subtask : oldEpicSubtasks) {
                if (!updatedEpic.getSubtasksIds().contains(subtask.getId()) && subtasksById.get(subtask.getId()) != null) {
                    if (subtasksById.get(subtask.getId()).getEpicId() == updatedEpic.getId()) {
                        subtasksById.remove(subtask.getId());
                    } else {
                        updateSubtask(subtask);
                    }
                }
            }
        }
        if (getEpicSubtasks(updatedEpic.getId()) != null) {
            for (Subtask newSubtask : getEpicSubtasks(updatedEpic.getId())) {
                if (subtasksById.get(newSubtask.getId()) == null) {
                    subtasksById.put(newSubtask.getId(), newSubtask);
                } else {
                    updateSubtask(newSubtask);
                }
            }
        }
        updateEpicStatus(updatedEpic.getId());
        return updatedEpic;
    }

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

    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(),
                subtask.getEpicId());
        newSubtask.setId(++currentTaskId);
        subtasksById.put(newSubtask.getId(), newSubtask);
        epicsById.get(subtask.getEpicId()).getSubtasksIds().add(newSubtask.getId());
        return newSubtask;
    }

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
        Subtask updatedSubtask = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getId(),
                subtask.getStatus(), subtask.getEpicId());
        subtasksById.put(updatedSubtask.getId(), updatedSubtask);
        if (epicsById.get(updatedSubtask.getEpicId()).getSubtasksIds() != null
            && !epicsById.get(updatedSubtask.getEpicId()).getSubtasksIds().contains(updatedSubtask.getId())) {
            epicsById.get(updatedSubtask.getEpicId()).getSubtasksIds().add(updatedSubtask.getId());
        }
        updateEpicStatus(updatedSubtask.getEpicId());
        return updatedSubtask;
    }

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

    public Collection<Task> getAllTasks() {
        Collection<Task> allTasks = tasksById.values();
        return allTasks;
    }

    public Collection<Epic> getAllEpics() {
        Collection<Epic> allEpics = epicsById.values();
        return allEpics;
    }

    public Collection<Subtask> getAllSubtasks() {
        Collection<Subtask> allSubtasks = subtasksById.values();
        return allSubtasks;
    }

    public ArrayList<Subtask> getEpicSubtasks(Integer epicId) {
        if (epicsById.size() != 0 && epicsById.get(epicId) != null
                && epicsById.get(epicId).getSubtasksIds().size() != 0) {
            ArrayList<Subtask> subtasks = new ArrayList<>();
            for (Integer subtaskId : epicsById.get(epicId).getSubtasksIds()) {
                subtasks.add(subtasksById.get(subtaskId));
            }
            return subtasks;
        } else {
            return null;
        }
    }

    public void deleteAllTasks() {
        tasksById.clear();
    }

    public void deleteAllEpics() {
        epicsById.clear();
        deleteAllSubtasks();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epicsById.values()) {
            epic.getSubtasksIds().clear();
            updateEpicStatus(epic.getId());
        }
        subtasksById.clear();
    }
    public Task getTaskById(int id) {
        return tasksById.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasksById.get(id);
    }

    public Epic getEpicById(int id) {
        return epicsById.get(id);
    }


    private void updateEpicStatus(Integer epicId) {
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
}
