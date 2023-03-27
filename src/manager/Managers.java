package manager;
import historyManager.*;
import taskManager.*;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(InMemoryTaskManager taskManager) {
        return new InMemoryHistoryManager();
    }
}