import exceptions.ManagerSaveException;
import historyManager.HistoryManager;
import manager.Managers;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import taskManager.TaskManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static task.TaskStatus.NEW;

public abstract class HistoryManagerTest<THistoryManager extends HistoryManager> {
    THistoryManager historyManager;
    TaskManager taskManager = Managers.getDefault();

    protected HistoryManagerTest() {
    }

    @Test
    void addTest() {
        try {
            Arrays.equals(new Task[]{}, historyManager.getHistory().toArray());
            assertEquals(0, historyManager.getHistory().size());
            Task task = new Task("Test addNewTask", "Test addNewTask description", NEW, 30,
                    LocalDateTime.of(2022, 5, 3, 14, 50));
            task = taskManager.createTask(task);
            historyManager.add(task);
            final List<Task> history = historyManager.getHistory();
            assertNotNull(history, "История не пустая.");
            assertEquals(1, history.size(), "История не пустая.");
            Task task1 = new Task("Test addNewTask", "Test addNewTask description", 1, NEW, 150,
                    LocalDateTime.of(2022, 11, 23, 18, 10));
            task1 = taskManager.createTask(task1);
            historyManager.add(task1);
            assertEquals(1, history.size(), "История не пустая.");
        } catch (ManagerSaveException e) {
            System.out.println("ManagerSaveException");
        }
    }

    @Test
    void removeAndGetHistoryTest() {
    try {
        Arrays.equals(new Task[]{}, historyManager.getHistory().toArray());
        historyManager.remove(9);
        Task task1 = new Task("Test task1", "Test task1 description", NEW, 30,
                LocalDateTime.of(2022, 5, 3, 14, 50));
        task1 = taskManager.createTask(task1);
        Epic epic1 = new Epic("Test epic1", "Test epic1 description");
        final int epic1Id = taskManager.createEpic(epic1).getId();
        Subtask subtask1 = new Subtask("Test subtask1", "Test subtask1 description", NEW, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic1Id);
        subtask1 = taskManager.createSubtask(subtask1);
        Arrays.equals(new Task[]{task1, epic1, subtask1}, historyManager.getHistory().toArray());
        historyManager.remove(3);
        Arrays.equals(new Task[]{task1, epic1}, historyManager.getHistory().toArray());
        Task task2 = new Task("Test task2", "Test task2 description", NEW, 150,
                LocalDateTime.of(2022, 11, 23, 18, 10));
        task2 = taskManager.createTask(task2);
        Arrays.equals(new Task[]{task1, epic1, task2}, historyManager.getHistory().toArray());
        historyManager.remove(1);
        Arrays.equals(new Task[]{epic1, task2}, historyManager.getHistory().toArray());
        Epic epic2 = new Epic("Test epic2", "Test epic2 description");
        final int epic2Id = taskManager.createEpic(epic2).getId();
        Arrays.equals(new Task[]{epic1, task2, epic2}, historyManager.getHistory().toArray());
        historyManager.remove(4);
        Arrays.equals(new Task[]{epic1, epic2}, historyManager.getHistory().toArray());
        Subtask subtask2 = new Subtask("Test subtask2", "Test subtask2 description", NEW, 500,
                LocalDateTime.of(2022, 3, 18, 15, 10), epic2Id);
        subtask2 = taskManager.createSubtask(subtask2);
        Arrays.equals(new Task[]{epic1, epic2, subtask2}, historyManager.getHistory().toArray());
    } catch (ManagerSaveException e) {
        System.out.println("ManagerSaveException");
    }
    }
}
