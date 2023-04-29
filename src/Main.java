import manager.Managers;
import taskManager.HttpTaskManager;
import taskManager.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        ((HttpTaskManager)taskManager).load();
    }
}
