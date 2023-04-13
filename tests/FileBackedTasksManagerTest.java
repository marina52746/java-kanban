import exceptions.ManagerSaveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import task.Epic;
import task.Subtask;
import task.Task;
import taskManager.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static task.TaskStatus.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    @BeforeEach
    void setUp() throws ManagerSaveException {
        taskManager = new FileBackedTasksManager(new File("resources/tasks_test_file.csv"));
    }

    @Test
    void loadFromFileTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(
                new File("resources/tasks_test_file.csv"));
        assertEquals(1,taskManager1.tasksById.size());
        assertEquals(1,taskManager1.subtasksById.size());
        assertEquals(1,taskManager1.epicsById.size());
        Epic epic = new Epic("Epic2", "Description epic2", 2);
        Subtask subtask = new Subtask("Sub Task2", "Description sub task3", 3, DONE, 500,
                LocalDateTime.of(2022,3,18,15,10), 2);
        Arrays.equals(new Task[]{epic, subtask}, taskManager1.getHistory().toArray());
    }

    @Test
    void loadFromFileWithoutHistoryTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(
                new File("resources/tasks_test_file_without_history.csv"));
        assertEquals(1,taskManager2.tasksById.size());
        assertEquals(2,taskManager2.subtasksById.size());
        assertEquals(1,taskManager2.epicsById.size());
        Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        Epic epic = new Epic("Epic2", "Description epic2", 2);
        Subtask subtask1 = new Subtask("Sub Task1", "Description sub task1", 3, DONE, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), 2);
        Subtask subtask2 = new Subtask("Sub Task2", "Description sub task2", 4, IN_PROGRESS,
                500, LocalDateTime.of(2022,3,18,15,10), 2);
        Arrays.equals(new Task[]{task}, taskManager2.getAllTasks().toArray());
        Arrays.equals(new Subtask[]{subtask1, subtask2}, taskManager2.getAllSubtasks().toArray());
        Arrays.equals(new Epic[]{epic}, taskManager2.getAllEpics().toArray());
    }

    @Test
    void loadFromFileWithoutTasksTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager3 = FileBackedTasksManager.loadFromFile(
                new File("resources/tasks_test_file_without_tasks.csv"));
        assertEquals(0,taskManager3.tasksById.size());
        assertEquals(0,taskManager3.subtasksById.size());
        assertEquals(0,taskManager3.epicsById.size());
        Arrays.equals(new Task[]{}, taskManager3.getHistory().toArray());
    }

    @Test
    void loadFromFileWithoutSubtasksTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager1 = FileBackedTasksManager.loadFromFile(
                new File("resources/tasks_test_file_without_subtasks.csv"));
        assertEquals(1,taskManager1.tasksById.size());
        assertEquals(0,taskManager1.subtasksById.size());
        assertEquals(1,taskManager1.epicsById.size());
        Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        Epic epic = new Epic("Epic2", "Description epic2", 2);
        Arrays.equals(new Task[]{task, epic}, taskManager1.getHistory().toArray());
    }

    @Test
    void prioritizedTaskTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(
                new File("resources/tasks_test_file_without_history_priorit.csv"));
        String str = "";
        for(Task task : taskManager2.getPrioritizedTasks())
            str += task.getId();
        assertTrue(str.equals("41356"));
    }

    @Test
    void writeToFileTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager1 = new FileBackedTasksManager(
                new File("resources/tasks_test_file.csv"));
        Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        task = taskManager1.createTask(task);
        Epic epic = new Epic("Epic2", "Description epic2", 2);
        epic = taskManager1.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub Task2", "Description sub task3", 3, DONE,
                500, LocalDateTime.of(2022,3,18,15,10), 2);
        subtask1 = taskManager1.createSubtask(subtask1);
        taskManager1.getEpicById(epic.getId());
        taskManager1.getSubtaskById(subtask1.getId());
    }

    @Test
    void writeToFileWithoutHistoryTest() throws ManagerSaveException {
        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(
                new File("resources/tasks_test_file_without_history.csv"));
        Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        task = taskManager2.createTask(task);
        Epic epic = new Epic("Epic2", "Description epic2", 2);
        epic = taskManager2.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub Task1", "Description sub task1", 3, DONE,
                1000, LocalDateTime.of(2022, 10, 25, 12, 30), 2);
        subtask1 = taskManager2.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Sub Task2", "Description sub task2", 4, IN_PROGRESS,
                500, LocalDateTime.of(2022,3,18,15,10), 2);
        subtask2 = taskManager2.createSubtask(subtask2);
    }

    @Test
    void writeToBadPath() throws ManagerSaveException {
        FileBackedTasksManager taskManager2 = new FileBackedTasksManager(new File("ABC/123.xp"));
        final Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022,5,3,14,50));
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        taskManager2.createTask(task);
                    }
                });
    }
}
