import exceptions.ManagerSaveException;
import historyManager.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import java.time.LocalDateTime;
import static task.TaskStatus.DONE;
import static task.TaskStatus.NEW;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {
    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void removeTest() {
    try {
        Task task = new Task("Task1", "Description task1", 1, NEW, 30,
                LocalDateTime.of(2022, 5, 3, 14, 50));
        historyManager.add(task);
        Epic epic = new Epic("Epic2", "Description epic2", 2);
        historyManager.add(epic);
        Subtask subtask1 = new Subtask("Sub Task1", "Description sub task1", 3, DONE, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), 2);
        historyManager.add(subtask1);
        Subtask subtask2 = new Subtask("Sub Task2", "Description sub task2", 4, DONE, 500,
                LocalDateTime.of(2022, 3, 18, 15, 10), 2);
        historyManager.add(subtask2);
        historyManager.remove(subtask1.getId()); // middle
        historyManager.remove(subtask2.getId()); // tail
        historyManager.remove(task.getId()); // head
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
    }
}

