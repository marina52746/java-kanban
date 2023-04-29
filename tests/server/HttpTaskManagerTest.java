package server;

import exceptions.ManagerSaveException;
import manager.Managers;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;
import task.TaskStatus;
import taskManager.HttpTaskManager;
import taskManager.TaskManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest {
    @Test
    public void taskManagerTest() throws ManagerSaveException {
        TaskManager taskManager = new HttpTaskManager("http://localhost:8078/");
        taskManager.createTask(new Task("t1", "d1", TaskStatus.IN_PROGRESS,
                500, LocalDateTime.of(2022,10,15,19,40)));
        taskManager.createEpic(new Epic("e1", "de1"));
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
    }


}
