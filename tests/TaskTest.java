import exceptions.ManagerSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import task.Task;
import task.TaskStatus;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static task.TaskStatus.NEW;

public class TaskTest {
    @Test
    void statusFromStringTest() throws ManagerSaveException {
        assertEquals(TaskStatus.NEW, Task.StatusFromString("NEW"));
        assertEquals(TaskStatus.IN_PROGRESS, Task.StatusFromString("IN_PROGRESS"));
        assertEquals(TaskStatus.DONE, Task.StatusFromString("DONE"));
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        Task.StatusFromString("ABC");
                    }
                });
        assertTrue(exception.getMessage().contains("can't convert to task status"));
    }

    @Test
    void intFromStringTest() throws ManagerSaveException {
        assertEquals(45, Task.IntFromString("45"));
        assertEquals(-5, Task.IntFromString("-5"));
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        Task.IntFromString("j%@");
                    }
                });
        assertTrue(exception.getMessage().contains("can't convert to int id"));

    }

    @Test
    void hashCodeTest() throws ManagerSaveException {
        Task task = new Task("task1", "new task", 1, NEW, 20, LocalDateTime.now());
        assertNotNull(task.hashCode());
    }
}
