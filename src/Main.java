import historyManager.HistoryManager;
import manager.Managers;
import task.*;
import taskManager.*;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        FileBackedTasksManager.main(new String[0]);
    }
    
    public static void printHistory(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
    }
}
