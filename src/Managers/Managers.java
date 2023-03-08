package Managers;
import HistoryManager.*;
import TaskManager.*;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        if (taskManager == null)
            taskManager = new InMemoryTaskManager();
        return taskManager;

    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null)
            historyManager = new InMemoryHistoryManager();
        return historyManager;
    }
}
