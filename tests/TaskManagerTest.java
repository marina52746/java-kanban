import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import task.*;
import taskManager.InMemoryTaskManager;
import taskManager.TaskManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static task.TaskStatus.*;

public abstract class TaskManagerTest<TTaskManager extends TaskManager> {
    public TTaskManager taskManager;

    @Test
    void createTaskTest() throws ManagerSaveException {
        Task task = new Task("Test createTask", "Test createTask description", NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        final int taskId = taskManager.createTask(task).getId();
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTaskTest() throws ManagerSaveException {
        Task taskNotCreated = new Task("Test updateTask1", "Test updateTask1 description", NEW,
                30,LocalDateTime.of(2022,5,3,14,50));
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.updateTask(taskNotCreated);
                    }
                });
        assertEquals("Задачи с таким номером не существует. Обновление невозможно.", exception.getMessage());

        Task task = new Task("Test updateTask", "Test updateTask description", NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        final int taskId = taskManager.createTask(task).getId();

        Task taskNew = new Task("Test UpdatedTask", "Test UpdatedTask description", taskId, DONE,
                80, LocalDateTime.of(2022,5,3,14,50));
        taskManager.updateTask(taskNew);

        final int newTaskId = taskNew.getId();
        final Task updatedTask = taskManager.getTaskById(newTaskId);
        assertNotNull(updatedTask, "Задача не найдена.");
        assertEquals(taskNew, updatedTask, "Задачи не совпадают.");
        final List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(taskNew, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteTaskTest() throws ManagerSaveException {
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.deleteTask(5);
                    }
                });
        assertEquals("Нельзя удалить задачу с номером " + 5 + ", её нет в списке", exception.getMessage());
        Task task = new Task("Test deleteTask", "Test deleteTask description", NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        final int taskId = taskManager.createTask(task).getId();
        final List<Task> tasks = taskManager.getAllTasks();
        final int tasksBeforeCount = tasks.size();
        taskManager.deleteTask(taskId);
        assertEquals(tasksBeforeCount - 1, taskManager.getAllTasks().size(),
                "Неверное количество задач.");
    }

    @Test
    void getAllTasksTest() throws ManagerSaveException {
        Arrays.equals(new Task[]{},taskManager.getAllTasks().toArray());
        Task task1 = new Task("Test getTasks", "Test getTasks description", NEW,  30,
                LocalDateTime.of(2022,5,3,14,50));
        task1 = taskManager.createTask(task1);
        Task task2 = new Task("Test getTasks", "Test getTasks description", NEW,
                0, null);
        task2 = taskManager.createTask(task2);
        Task task3 = new Task("Test getTasks", "Test getTasks description", NEW, 350,
                LocalDateTime.of(2022,10,17,7,30));
        task3 = taskManager.createTask(task3);
        Task[] tasks = new Task[] {task1, task2, task3};
        Arrays.equals(tasks, taskManager.getAllTasks().toArray());
    }

    @Test
    void deleteAllTasksTest() throws ManagerSaveException {
        Task task1 = new Task("Test deleteTasks1", "Test deleteTasks1 description", NEW,  30,
                LocalDateTime.of(2022,5,3,14,50));
        task1 = taskManager.createTask(task1);
        Task task2 = new Task("Test deleteTasks2", "Test deleteTasks2 description", NEW, 350,
                LocalDateTime.of(2022,10,17,7,30));
        task2 = taskManager.createTask(task2);
        assertEquals(2, taskManager.getAllTasks().size());
        taskManager.deleteAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
        assertFalse(taskManager.getAllTasks().contains(task1));
        assertFalse(taskManager.getAllTasks().contains(task2));
    }

    @Test
    void getTaskByIdTest() throws ManagerSaveException {
        Task task1 = new Task("Test getTaskById", "Test getTaskById description", NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        final int task1Id = taskManager.createTask(task1).getId();
        assertEquals(task1, taskManager.getTaskById(task1Id));
    }

    @Test
    void getHistoryTest() throws ManagerSaveException {
        Arrays.equals(new Integer[] {}, taskManager.getHistory().toArray());
        Task task = new Task("Test getHistoryTask", "Test getHistoryTask description", NEW,  30,
                LocalDateTime.of(2022,5,3,14,50));
        final int taskId = taskManager.createTask(task).getId();
        Epic epic = new Epic("Test getHistoryEpic", "Test getHistoryEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask = new Subtask("Test getHistorySubtask", "Test getHistorySubtask description",
                NEW, 1000, LocalDateTime.of(2022, 10, 25, 12, 30), epicId);
        final int subtaskId = taskManager.createSubtask(subtask).getId();
        Integer[] Ids = new Integer[] {taskId, epicId, subtaskId};
        Arrays.equals(Ids, taskManager.getHistory().toArray());
    }

    @Test
    void createEpicTest() throws ManagerSaveException {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        final Epic savedEpic = taskManager.getEpicById(epicId);
        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateEpicTest() throws ManagerSaveException {
        Epic epicNotCreated = new Epic("Test updateEpic1", "Test updateEpic1 description");
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.updateEpic(epicNotCreated);
                    }
                });
        assertEquals("Эпика с таким номером не существует. Обновление невозможно.", exception.getMessage());

        Epic epic = new Epic("Test updateEpic", "Test updateEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask = new Subtask("Test updateEpicSubtask", "Test updateEpicSubtasc description",
                DONE, 1000, LocalDateTime.of(2022, 10, 25, 12, 30), epicId);
        final int subtascId = taskManager.createSubtask(subtask).getId();

        Epic epicNew = new Epic("Test updateEpic", "Test updateEpic description", epicId);
        epicNew = taskManager.updateEpic(epicNew);
        taskManager.updateEpicStatus(epicId);
        assertEquals(DONE, epicNew.getStatus());
        final int newEpicId = epicNew.getId();
        final Epic updatedEpic = taskManager.getEpicById(newEpicId);
        assertNotNull(updatedEpic, "Эпик не найден.");
        assertEquals(epicNew, updatedEpic, "Эпики не совпадают.");
        final List<Epic> epics = taskManager.getAllEpics();
        assertNotNull(epics, "Эпики на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epicNew, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void deleteEpicTest() throws ManagerSaveException {
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.deleteEpic(8);
                    }
                });
        assertEquals("Нельзя удалить эпик с номером " + 8 + ", его нет в списке", exception.getMessage());

        Epic epic = new Epic("Test deleteEpic", "Test deleteEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test deleteSubtask1InEpic",
                "Test deleteSubtask1InEpic description", NEW, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epicId);
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test deleteSubtask2InEpic",
                "Test deleteSubtask2InEpic description", NEW, 750,
                LocalDateTime.of(2022,12,7,6,30), epicId);
        subtask2 = taskManager.createSubtask(subtask2);
        List<Epic> epics = taskManager.getAllEpics();
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertTrue(epics.size() == 1 && subtasks.size() == 2
                && epics.contains(epic) && subtasks.contains(subtask1) && subtasks.contains(subtask2));
        taskManager.deleteEpic(epicId);
        epics = taskManager.getAllEpics();
        subtasks = taskManager.getAllSubtasks();
        assertTrue(epics.size() == 0 && subtasks.size() == 0
                && !(epics.contains(epic)) && !(subtasks.contains(subtask1)) && !(subtasks.contains(subtask2)));
    }

    @Test
    void getAllEpicsTest() throws ManagerSaveException {
        Arrays.equals(new Epic[]{},taskManager.getAllEpics().toArray());
        Epic epic1 = new Epic("Test getEpics1", "Test getEpics1 description");
        epic1 = taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Test getEpics2", "Test getEpics2 description");
        epic2 = taskManager.createEpic(epic2);
        Epic[] epics = new Epic[] {epic1, epic2};
        Arrays.equals(epics, taskManager.getAllEpics().toArray());
    }

    @Test
    void getEpicSubtasksTest() throws ManagerSaveException {
        assertEquals(null, taskManager.getEpicSubtasks(10));
        Epic epic = new Epic("Test getEpicSubtasks", "Test getEpicSubtasks description");
        final int epicId = taskManager.createEpic(epic).getId();
        assertEquals(null, taskManager.getEpicSubtasks(epicId));
        Subtask subtask1 = new Subtask("Test getEpicSubtasks1", "Test getEpicSubtasks1 description", NEW, 1000, LocalDateTime.of(2022, 10, 25, 12, 30), epicId);
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test getEpicSubtasks2", "Test getEpicSubtasks2 description",
                NEW, 800,LocalDateTime.of(2022,11,1,16,10), epicId);
        subtask2 = taskManager.createSubtask(subtask2);

        Integer[] subtasksIds = new Integer[] {subtask1.getId(), subtask2.getId()};
        Arrays.equals(subtasksIds, taskManager.getEpicSubtasks(epicId).toArray());
    }

    @Test
    void deleteAllEpicsTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Test deleteEpics1", "Test deleteEpics1 description");
        epic1 = taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Test deleteEpics1", "Test deleteEpics1 description");
        epic2 = taskManager.createEpic(epic2);
        assertEquals(2, taskManager.getAllEpics().size());
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
        assertFalse(taskManager.getAllEpics().contains(epic1));
        assertFalse(taskManager.getAllEpics().contains(epic2));
        assertTrue(taskManager.getAllSubtasks().size() == 0);
    }

    @Test
    void getEpicByIdTest() throws ManagerSaveException {
        Epic epic = new Epic("Test getEpicById", "Test getEpicById description");
        final int epicId = taskManager.createEpic(epic).getId();
        assertEquals(epic, taskManager.getEpicById(epicId));
    }

    @Test
    void createSubtaskTest() throws ManagerSaveException {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask = new Subtask("Test createSubtask", "Test createSubtask description", NEW,
                1000, LocalDateTime.of(2022, 10, 25, 12, 30), epicId);
        final int subtaskId = taskManager.createSubtask(subtask).getId();
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");
        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Подзадачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        Subtask subtask1 = new Subtask("Test createSubtask1", "Test createSubtask1 description", NEW,
                1000, LocalDateTime.of(2022, 10, 25, 12, 30), 7);
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.updateSubtask(subtask1);
                    }
                });
        assertEquals("Не существует Эпик с Id = 7", exception.getMessage());
    }

    @Test
    void updateSubtask() throws ManagerSaveException {
        Epic epic1 = new Epic("Test updateSubtascEpic1", "Test updateSubtascEpic1 description");
        final int epic1Id = taskManager.createEpic(epic1).getId();

        Subtask subtaskNotCreated = new Subtask("Test updateSubtask1", "Test updateSubtask1 description",
                NEW, 1000, LocalDateTime.of(2022, 10, 25, 12, 30), epic1Id);
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.updateSubtask(subtaskNotCreated);
                    }
                });
        assertEquals("Подзадачи с таким номером не существует. Обновление невозможно.", exception.getMessage());

        Epic epic2 = new Epic("Test updateSubtascEpic2", "Test updateSubtascEpic2 description");
        final int epic2Id = taskManager.createEpic(epic2).getId();
        Subtask subtask = new Subtask("Test updateSubtask", "Test updateSubtask description",
                IN_PROGRESS, 200,LocalDateTime.of(2022,6,14,17,40), epic1Id);
        final int subtaskId = taskManager.createSubtask(subtask).getId();

        Subtask subtaskNew = new Subtask("Test updateSubtaskNew", "Test updateSubtaskNew description",
                subtaskId, DONE, 600,LocalDateTime.of(2022,5,4,16,0), epic2Id);
        subtaskNew = taskManager.updateSubtask(subtaskNew);

        assertTrue(epic1.getStatus() == NEW && epic2.getStatus() == DONE
        && !(epic1.getSubtasksIds().contains(subtaskId))
        && epic2.getSubtasksIds().contains(subtaskId)
        && taskManager.getSubtaskById(subtaskId).equals(subtaskNew));

        Subtask subtask1New = new Subtask("Test updateSubtask1New",
                "Test updateSubtask1New description", subtaskId, DONE, 50,
                LocalDateTime.of(2022,8,19,16,50), 10);
        final ManagerSaveException exception1 = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.updateSubtask(subtask1New);
                    }
                });
        assertEquals("Не существует Эпик с Id = 10", exception1.getMessage());

        Subtask subtask2New = new Subtask("Test SubtaskNewName",
                "Test New description", subtaskId, DONE, 500,
                LocalDateTime.of(2022,2,22,10,20), epic2Id);
        subtask2New = taskManager.updateSubtask(subtask2New);
        assertEquals("Test SubtaskNewName", subtask2New.getName());
        assertEquals("Test New description", subtask2New.getDescription());
    }

    @Test
    void deleteSubtaskTest() throws ManagerSaveException {
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager.deleteSubtask(7);
                    }
                });
        assertEquals("Нельзя удалить подзадачу с номером " + 7 + ", её нет в списке", exception.getMessage());

        Epic epic = new Epic("Test deleteSubtascEpic", "Test deleteSubtascEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask = new Subtask("Test deleteSubtasc", "Test deleteSubtasc description",
                IN_PROGRESS, 1000, LocalDateTime.of(2022, 10, 25, 12, 30),
                epicId);
        final int subtaskId = taskManager.createSubtask(subtask).getId();
        assertTrue(epic.getSubtasksIds().contains(subtaskId) && epic.getStatus() == IN_PROGRESS);

        taskManager.deleteSubtask(subtaskId);

        assertTrue(epic.getSubtasksIds().isEmpty() && epic.getStatus() == NEW);
    }

    @Test
    void getAllSubtasksTest() throws ManagerSaveException {
        Arrays.equals(new Subtask[]{}, taskManager.getAllSubtasks().toArray());
        Epic epic1 = new Epic("Test getSubtasksEpic1", "Test getSubtasksEpic1 description");
        final int epic1Id = taskManager.createEpic(epic1).getId();
        Epic epic2 = new Epic("Test getSubtasksEpic2", "Test getSubtasksEpic2 description");
        final int epic2Id = taskManager.createEpic(epic2).getId();
        Subtask subtask1 = new Subtask("Test getSubtasks1", "Test getSubtasks1 description", NEW,
                1000, LocalDateTime.of(2022, 10, 25, 12, 30), epic1Id);
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test getSubtasks2", "Test getSubtasks2 description", NEW,
                550,LocalDateTime.of(2022,3,9,22,30), epic2Id);
        subtask2 = taskManager.createSubtask(subtask2);
        Subtask[] subtasks = new Subtask[] {subtask1, subtask2};
        Arrays.equals(subtasks, taskManager.getAllSubtasks().toArray());
    }

    @Test
    void deleteAllSubtasks() throws ManagerSaveException {
        Epic epic1 = new Epic("Test deleteSubtasksEpic1", "Test deleteSubtasksEpic1 description");
        final int epic1Id = taskManager.createEpic(epic1).getId();
        Epic epic2 = new Epic("Test deleteSubtasksEpic2", "Test deleteSubtasksEpic2 description");
        final int epic2Id = taskManager.createEpic(epic2).getId();
        Subtask subtask1 = new Subtask("Test deleteSubtasks", "Test deleteSubtasks description", NEW,
                1000, LocalDateTime.of(2022, 10, 25, 12, 30), epic1Id);
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test deleteSubtasks", "Test deleteSubtasks description", NEW,
                640,LocalDateTime.of(2022,9,28,12,15), epic2Id);
        subtask2 = taskManager.createSubtask(subtask2);
        assertEquals(2, taskManager.getAllSubtasks().size());

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size());
        for (Epic epic : taskManager.getAllEpics()) {
            assertEquals(0, epic.getSubtasksIds().size());
        }
    }

    @Test
    void getSubtaskByIdTest() throws ManagerSaveException {
        Epic epic = new Epic("Test getSubtaskByIdTestEpic", "Test getSubtasksEpic1 description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test getSubtaskByIdTest", "Test getSubtasks description", NEW,
                1000, LocalDateTime.of(2022, 10, 25, 12, 30), epicId);
        final int subtask1Id = taskManager.createSubtask(subtask1).getId();
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1Id));
    }


    @Test
    void getTaskEndTimeTest() throws ManagerSaveException {
        Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        task = taskManager.createTask(task);
        assertEquals(LocalDateTime.of(2022,5,3,15,20), task.getEndTime());
    }

    @Test
    void epicStartEndDurationTest() throws ManagerSaveException {
        Epic epic = new Epic("Test StartEndDurationEpic", "Test StartEndDurationEpic description");
        final int epicId = taskManager.createEpic(epic).getId();
        Subtask subtask1 = new Subtask("Test StartEndDurationSubtask1",
                "Test StartEndDurationSubtask1 description", NEW, 600,
                LocalDateTime.of(2022, 10, 25, 7, 30), epicId); //end 17.30
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test StartEndDurationSubtask2",
                "Test StartEndDurationSubtask2 description", NEW, 120,
                LocalDateTime.of(2022,10,25,5,0), epicId); //end 7.00
        subtask2 = taskManager.createSubtask(subtask2);
        assertTrue(epic.getStartTime().equals(LocalDateTime.of(2022,10,25,5,0))
        && epic.getEndTime().equals(LocalDateTime.of(2022, 10, 25, 17, 30))
        && epic.getDuration() == 750);
        final Subtask[] subtask3 = {new Subtask("Test StartEndDurationSubtask3",
                "Test StartEndDurationSubtask3 description", NEW, 240,
                LocalDateTime.of(2022, 10, 25, 15, 0), epicId)};

        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        subtask3[0] = taskManager.createSubtask(subtask3[0]);
                    }
                });
        assertEquals("Interval is used, change startDate or Duration", exception.getMessage());
    }

    @Test
    void getAllTaskIntervalsTest() throws ManagerSaveException {
        Task task = new Task("Test task", "Test task description", NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        List<LocalDateTime> intervals = new ArrayList<LocalDateTime>();
        intervals.add(LocalDateTime.of(2022,5,3,14,45));
        intervals.add(LocalDateTime.of(2022,5,3,15,0));
        intervals.add(LocalDateTime.of(2022,5,3,15,15));
        assertEquals(intervals, InMemoryTaskManager.getAllTaskIntervals(task));
        Task task1 = new Task("Test task1", "Test task1 description", NEW, 0,
                null);
        intervals.clear();
        assertEquals(intervals, InMemoryTaskManager.getAllTaskIntervals(task1));
    }

    @Test
    void existIntersectionsTest() throws ManagerSaveException {
        InMemoryTaskManager taskManager1 = new InMemoryTaskManager();
        Task task1 = new Task("Test task1", "Test task1 description", NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        task1 = taskManager1.createTask(task1);
        Task task2 = new Task("Test task2", "Test task2 description", NEW, 100,
                LocalDateTime.of(2022,5,3,16,20));
        assertFalse(taskManager1.existIntersections(task2));
        final Task[] task3 = {new Task("Test task3", "Test task3 description", NEW, 20,
                LocalDateTime.of(2022, 5, 3, 15, 10))};
        assertTrue(taskManager1.existIntersections(task3[0]));
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        task3[0] = taskManager1.createTask(task3[0]);
                    }
                });
        assertEquals("Interval is used, change startDate or Duration",exception.getMessage());
        Task task4 = new Task("Test task4", "Test task4 description", NEW, 15,
                LocalDateTime.of(2022,5,3,14,30));
        assertFalse(taskManager1.existIntersections(task4));
    }
}
