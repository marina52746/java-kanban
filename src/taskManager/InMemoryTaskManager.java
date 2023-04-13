package taskManager;
import exceptions.ManagerSaveException;
import historyManager.HistoryManager;
import manager.Managers;
import task.*;
import java.time.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager{
    static int currentTaskId;
    static LocalDateTime tasksGridFirstCell;
    static LocalDateTime tasksGridLastCell;
    final HistoryManager historyManager = Managers.getDefaultHistory();
    public final Map<Integer, Task> tasksById = new HashMap<>();
    public final Map<Integer, Epic> epicsById = new HashMap<>();
    public final Map<Integer, Subtask> subtasksById = new HashMap<>();
    public final Map<LocalDateTime, Boolean> tasksGrid = new HashMap<>();
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getStartTime() == null && t2.getStartTime() == null || (t1.getStartTime() == t2.getStartTime()))
                return t1.getId() - t2.getId(); // sort by id
            if (t1.getStartTime() == null)
                return 1;
            if (t2.getStartTime() == null)
                return -1;
            if (t1.getStartTime().isBefore(t2.getStartTime()))
                return -1;
            return 1;
        }
    };

    Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    public static List<LocalDateTime> getAllTaskIntervals(Task task) {
        List<LocalDateTime> intervals = new ArrayList<>();
        if (task.getStartTime() == null)
            return intervals;
        long delta15minutes = task.getStartTime().getMinute() % 15;
        LocalDateTime firstInterval = task.getStartTime().minusMinutes(delta15minutes); //to 15-min intervals
        intervals.add(firstInterval);
        delta15minutes = task.getEndTime().getMinute() % 15;
        LocalDateTime lastInterval;
        if (delta15minutes != 0)
            lastInterval = task.getEndTime().minusMinutes(delta15minutes);
        else
            lastInterval = task.getEndTime().minusMinutes(15);
        LocalDateTime currentInterval = firstInterval;
        while (currentInterval.isBefore(lastInterval)) {
            currentInterval = currentInterval.plusMinutes(15);
            intervals.add(currentInterval);
        }
        return intervals;
    }

    public boolean existIntersections(Task addedTask) {
        if (addedTask.getStartTime() == null || tasksGrid.isEmpty()) return false;
        List<LocalDateTime> intervals = InMemoryTaskManager.getAllTaskIntervals(addedTask);
        for(LocalDateTime interval : intervals) {
            if (tasksGrid.get(interval))
                return true;
        }
        return false;
    }

    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        beforeAddingIntervals(task, true);
        if (existIntersections(task))
            throw new ManagerSaveException("Interval is used, change startDate or Duration");
        if (task.getId() == 0) task.setId(++currentTaskId);
        occupyIntervals(InMemoryTaskManager.getAllTaskIntervals(task));
        tasksById.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        if (!tasksById.containsKey(task.getId())) {
            throw new ManagerSaveException("Задачи с таким номером не существует. Обновление невозможно.");
        }
        beforeAddingIntervals(tasksById.get(task.getId()), false);
        if (existIntersections(task)) {
            occupyIntervals(InMemoryTaskManager.getAllTaskIntervals(tasksById.get(task.getId())));  //rollback
            throw new ManagerSaveException("Interval is used, change startDate or Duration");
        }
        occupyIntervals(InMemoryTaskManager.getAllTaskIntervals(task));
        tasksById.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public void deleteTask(Integer taskId) throws ManagerSaveException {
        if (tasksById.get(taskId) == null) {
            throw new ManagerSaveException("Нельзя удалить задачу с номером " + taskId + ", её нет в списке");
            //System.out.println("Нельзя удалить задачу с номером " + taskId + ", её нет в списке");
            //return;
        }
        prioritizedTasks.remove(tasksById.get(taskId));
        tasksById.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        if (epic.getId() == 0) epic.setId(++currentTaskId);
        epicsById.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        final Epic updateEpic = epicsById.get(epic.getId());
        if (updateEpic == null) {
            throw new ManagerSaveException("Эпика с таким номером не существует. Обновление невозможно.");
            //System.out.println("Эпика с таким номером не существует. Обновление невозможно.");
            //return null;
        }
        updateEpic.setDescription(epic.getDescription());
        updateEpic.setName(epic.getName());
        return updateEpic;
    }

    @Override
    public void deleteEpic(Integer epicId) throws ManagerSaveException {
        final Epic epic = epicsById.remove(epicId);
        if (epic == null) {
            throw new ManagerSaveException("Нельзя удалить эпик с номером " + epicId + ", его нет в списке");
            //System.out.println("Нельзя удалить эпик с номером " + epicId + ", его нет в списке");
            //return;
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
        if (!(epicsById.containsKey(subtask.getEpicId())))
            throw new ManagerSaveException("Не существует Эпик с Id = " + subtask.getEpicId());
        beforeAddingIntervals(subtask, true);
        if (existIntersections(subtask))
            throw new ManagerSaveException("Interval is used, change startDate or Duration");
        if (subtask.getId() == 0) subtask.setId(++currentTaskId);
        occupyIntervals(InMemoryTaskManager.getAllTaskIntervals(subtask));
        subtasksById.put(subtask.getId(), subtask);
        epicsById.get(subtask.getEpicId()).getSubtasksIds().add(subtask.getId());
        updateEpicStatus(subtask.getEpicId());
        updateDurationEpicStartTimeEndTime(subtask.getEpicId());
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws ManagerSaveException {
        if (!(epicsById.containsKey(subtask.getEpicId())))
            throw new ManagerSaveException("Не существует Эпик с Id = " + subtask.getEpicId());
        final Subtask oldSubtask = subtasksById.get(subtask.getId());
        if (oldSubtask == null) {
            throw new ManagerSaveException("Подзадачи с таким номером не существует. Обновление невозможно.");
        }
        beforeAddingIntervals(oldSubtask, false);
        if (existIntersections(subtask)) {
            occupyIntervals(InMemoryTaskManager.getAllTaskIntervals(oldSubtask)); //rollback
            throw new ManagerSaveException("Interval is used, change startDate or Duration");
        }
        occupyIntervals(InMemoryTaskManager.getAllTaskIntervals(subtask));
        final Epic oldEpic = epicsById.get(oldSubtask.getEpicId());
        final Epic newEpic = epicsById.get(subtask.getEpicId());
        if (!Objects.equals(subtask.getEpicId(), oldSubtask.getEpicId())) {
            oldEpic.getSubtasksIds().remove((Integer)subtask.getId());
            newEpic.getSubtasksIds().add(subtask.getId());
            updateEpicStatus(oldEpic.getId());
            updateDurationEpicStartTimeEndTime(oldEpic.getId());
            subtasksById.put(subtask.getId(), subtask);
            updateEpicStatus(newEpic.getId());
        } else
            subtasksById.put(subtask.getId(), subtask);
        updateDurationEpicStartTimeEndTime(newEpic.getId());
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public void deleteSubtask(Integer subtaskId) throws ManagerSaveException {
        final Subtask subtask = subtasksById.remove(subtaskId);
        if (subtask == null) {
            throw new ManagerSaveException("Нельзя удалить подзадачу с номером " + subtaskId + ", её нет в списке");
            //System.out.println("Нельзя удалить подзадачу с номером " + subtaskId + ", её нет в списке");
            //return;
        }
        final Epic epic = epicsById.get(subtask.getEpicId());
        if (epic.getSubtasksIds() != null) {
            epic.getSubtasksIds().remove(subtaskId);
        }
        updateEpicStatus(epic.getId());
        updateDurationEpicStartTimeEndTime(epic.getId());
        historyManager.remove(subtaskId);
        prioritizedTasks.remove(subtask);
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
            updateDurationEpicStartTimeEndTime(epic.getId());
        }
        for(int id : subtasksById.keySet()) {
            historyManager.remove(id);
        }
        subtasksById.clear();
    }

    @Override
    public Task getTaskById(int id) throws ManagerSaveException {
        final Task task = tasksById.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) throws ManagerSaveException {
        final Subtask subtask = subtasksById.get(id);
        if (subtask != null) historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) throws ManagerSaveException {
        final Epic epic = epicsById.get(id);
        if (epic != null) historyManager.add(epic);
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

    public void updateDurationEpicStartTimeEndTime(Integer epicId) throws ManagerSaveException {
        LocalDateTime start = null;
        LocalDateTime end = null;
        Epic epic = epicsById.get(epicId);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            if (start == null || subtasksById.get(subtaskId).getStartTime().isBefore(start))
                start = subtasksById.get(subtaskId).getStartTime();
            if (end == null || subtasksById.get(subtaskId).getEndTime().isAfter(end))
                end = subtasksById.get(subtaskId).getEndTime();
        }
        epic.setStartTime(start);
        epic.setEndTime(end);
        if(start == null || end == null) epic.setDuration(0);
        else epic.setDuration(java.time.Duration.between(start, end).toMinutes()); //!!!!!!!
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void occupyIntervals(List<LocalDateTime> intervals) {
        for (LocalDateTime interval : intervals) {
            tasksGrid.put(interval, true);
        }
    }

    private void freeIntervals(List<LocalDateTime> intervals) {
        for (LocalDateTime interval : intervals) {
            tasksGrid.put(interval, false);
        }
    }

    private void beforeAddingIntervals(Task oldTask, boolean createOnly) {
        increaseTaskGrid();
        if (!createOnly) freeIntervals(InMemoryTaskManager.getAllTaskIntervals(oldTask));
    }

    private void increaseTaskGrid() {
        LocalDate firstGridDate = LocalDate.of(2022,1,1);
        LocalTime firstGridTime = LocalTime.of(0,0,0);
        LocalDateTime firstGridDateTime = LocalDateTime.of(firstGridDate, firstGridTime);
        LocalDateTime currentCell;
        if (tasksGrid.isEmpty()) {
            tasksGridFirstCell = firstGridDateTime;
            currentCell = tasksGridFirstCell;
            for (int i = 0; i < 365 * 24 * 4; i++) {
                tasksGrid.put(currentCell, false);
                currentCell = currentCell.plusMinutes(15);
            }
            tasksGridLastCell = currentCell.minusMinutes(15);
        } else {
            LocalDateTime newLastCell = firstGridDateTime.plusYears(1);
            if (newLastCell.isAfter(tasksGridLastCell.plusMinutes(15))) {
                currentCell = tasksGridLastCell;
                Duration between = Duration.between(tasksGridLastCell, newLastCell);
                long addedCellsCount = between.toMinutes() / 15;
                for (int i = 0; i < addedCellsCount; i++) {
                    tasksGrid.put(currentCell, false);
                    currentCell = currentCell.plusMinutes(15);
                }
                tasksGridLastCell = currentCell.minusMinutes(15);
            }
        }
    }
}
