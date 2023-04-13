import exceptions.ManagerSaveException;
import historyManager.FileBackedHistoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedHistoryManagerTest {

    @Test
    void historyFromStringTest() throws ManagerSaveException {
        final ManagerSaveException exception = assertThrows(
                ManagerSaveException.class,
                new Executable() {
                    @Override
                    public void execute() throws ManagerSaveException {
                        FileBackedHistoryManager.historyFromString("5,6,w");
                    }
                });
        assertTrue(exception.getMessage().contains("Can't convert string "));
    }
}
