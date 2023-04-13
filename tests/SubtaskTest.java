import exceptions.ManagerSaveException;
import manager.Managers;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import taskManager.TaskManager;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static task.TaskStatus.DONE;

public class SubtaskTest {
    public TaskManager taskManager = Managers.getDefault();
    @Test
    void setEpicIdTest() throws ManagerSaveException {
        Epic epic1 = new Epic("Epic1", "Description epic1", 1);
        epic1 = taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic2", "Description epic2", 2);
        epic2 = taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Sub Task1", "Description sub task1", 3, DONE, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), 2);
        subtask1 = taskManager.createSubtask(subtask1);
        subtask1.setEpicId(1);
        assertEquals(1, subtask1.getEpicId());
    }

    @Test
    void hashCodeTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub Task1", "Description sub task1", DONE, 20,
                LocalDateTime.of(2022,12,10,5,30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        assertNotNull(subtask1.hashCode());
    }

    @Test
    void toStringTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Sub Task1", "Description Sub Task1", DONE, 20,
                LocalDateTime.of(2022,12,10,5,30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        assertTrue(subtask1.toString().contains("Sub Task1")
                && subtask1.toString().contains("Description Sub Task1")
                && subtask1.toString().contains("SUBTASK") && subtask1.toString().contains("DONE")
                && subtask1.toString().contains("20"));
    }
}
