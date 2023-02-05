import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int currentTaskId = 0;

    private HashMap<Integer, Task> tasksById = new HashMap<>();
    private HashMap<Integer, Epic> epicsById = new HashMap<>();
    private HashMap<Integer, Subtask> subtasksById = new HashMap<>();

    public void createTask(Task task) {
        task.setId(++currentTaskId);
        updateTask(task);
    }

    public void updateTask(Task task) {
        tasksById.put(task.getId(), task);
    }

    public void deleteTask(Integer taskId) {
        if (tasksById.get(taskId) == null) {
            System.out.println("Нельзя удалить задачу с номером " + taskId + ", её нет в списке");
            return;
        }
        tasksById.remove(taskId);
    }

    public void createEpic(Epic epic) {
        epic.setId(++currentTaskId);
        updateEpic(epic);
    }

    public void updateEpic(Epic epic) {
        ArrayList<Subtask> oldEpicSubtasks;
        oldEpicSubtasks = getEpicSubtasks(epic.getId());
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
    }

    public void deleteEpic(Integer epicId) {
        if (epicsById.get(epicId) == null) {
            System.out.println("Нельзя удалить эпик с номером " + epicId + ", его нет в списке");
            return;
        }
        for (Integer subtaskId : epicsById.get(epicId).getSubtasksIds()) {
            subtasksById.remove(subtaskId);
        }
        epicsById.remove(epicId);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(++currentTaskId);
        updateSubtask(subtask);
    }

    public void updateSubtask(Subtask subtask) {
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
        if (!epicsById.get(subtask.getEpicId()).getSubtasksIds().contains(subtask.getId())) {
            epicsById.get(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        }
        updateEpicStatus(subtask.getEpicId());
    }

    public void deleteSubtask(Integer subtaskId) {
        if (subtasksById.get(subtaskId) == null) {
            System.out.println("Нельзя удалить подзадачу с номером " + subtaskId + ", её нет в списке");
            return;
        }
        for (int i = 0; i < epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds().size(); i++) {
            if (epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds().get(i) == subtaskId) {
                epicsById.get(subtasksById.get(subtaskId).getEpicId()).getSubtasksIds().remove(i);
                break;
            }
        }
        updateEpicStatus(subtasksById.get(subtaskId).getEpicId());
        subtasksById.remove(subtaskId);
    }

    public ArrayList<Task> getAllTasks() {
        if (tasksById.size() != 0) {
            ArrayList<Task> allTasks = new ArrayList<>();
            for (Task task : tasksById.values()) {
                allTasks.add(task);
            }
            return allTasks;
        } else {
            return null;
        }
    }

    public ArrayList<Epic> getAllEpics() {
        if (epicsById.size() != 0) {
            ArrayList<Epic> allEpics = new ArrayList<>();
            for (Epic epic : epicsById.values()) {
                allEpics.add(epic);
            }
            return allEpics;
        } else {
            return null;
        }
    }

    public ArrayList<Subtask> getAllSubtasks() {
        if (subtasksById.size() != 0) {
            ArrayList<Subtask> allSubtasks = new ArrayList<>();
            for (Subtask subtask : subtasksById.values()) {
                allSubtasks.add(subtask);
            }
            return allSubtasks;
        } else {
            return null;
        }
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
        int i = 0;
            for (Integer subtaskId : epicsById.get(epicId).getSubtasksIds()) {
                if (subtasksById.get(subtaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                    i += 1;
                } else if (subtasksById.get(subtaskId).getStatus() == TaskStatus.DONE) {
                    i += 2;
                }
            }
        if (i == 0) {
            epicsById.get(epicId).setStatusNew();
        }
        else if (epicsById.get(epicId).getSubtasksIds().size() * 2 == i) {
            epicsById.get(epicId).setStatusDone();
        } else {
            epicsById.get(epicId).setStatusInProgress();
        }
    }
}
