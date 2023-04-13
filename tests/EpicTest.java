import exceptions.ManagerSaveException;
import manager.Managers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;
import taskManager.TaskManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    public TaskManager taskManager = Managers.getDefault();

    @Test
    void statusWhenListSubtasksEmpty() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusWhenAllSubtasksNew() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.NEW, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.NEW, 500,
                LocalDateTime.of(2022,3,18,15,10), epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void statusWhenSubtasksNewAndInProgress() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.NEW, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.IN_PROGRESS,500,
                LocalDateTime.of(2022,3,18,15,10),  epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusWhenAllSubtasksInProgress() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.IN_PROGRESS, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.IN_PROGRESS,500,
                LocalDateTime.of(2022,3,18,15,10),  epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusWhenSubtasksInProgressAndDone() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.IN_PROGRESS, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.DONE,500,
                LocalDateTime.of(2022,3,18,15,10),  epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusWhenAllSubtasksDone() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.DONE, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.DONE,500,
                LocalDateTime.of(2022,3,18,15,10),  epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void statusWhenSubtasksNewAndDone() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.NEW, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.DONE,500,
                LocalDateTime.of(2022,3,18,15,10),  epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void statusWhenSubtasksNewInProgressAndDone() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("subtask1", "sub 1", TaskStatus.NEW, 1000,
                LocalDateTime.of(2022, 10, 25, 12, 30), epic.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("subtask2", "sub 2", TaskStatus.IN_PROGRESS,500,
                LocalDateTime.of(2022,3,18,15,10),  epic.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("subtask3", "sub 3", TaskStatus.DONE,90,
                LocalDateTime.of(2022,8,31,11,15),  epic.getId());
        subtask3 = taskManager.createSubtask(subtask3);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void setSubtasksIdsTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);
        epic.setSubtasksIds(ids);
        Arrays.equals(ids.toArray(), epic.getSubtasksIds().toArray());
    }

    @Test
    void setStatusTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        epic.setStatus(TaskStatus.DONE);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void getSetEndTimeTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        epic.setStartTime(LocalDateTime.of(2022,10,10,21,0));
        epic.setEndTime(LocalDateTime.of(2022,10,10,23,30));
        assertEquals(LocalDateTime.of(2022,10,10,21,0), epic.getStartTime());
        assertEquals(LocalDateTime.of(2022,10,10,23,30), epic.getEndTime());
    }

    @Test
    void hashCodeTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        assertNotNull(epic.hashCode());
    }

    @Test
    void toStringTest() throws ManagerSaveException {
        Epic epic = new Epic("epic1", "new epic");
        epic = taskManager.createEpic(epic);
        assertNotNull(epic.toString());
        assertTrue(epic.toString().contains("EPIC") && epic.toString().contains("epic1")
                && epic.toString().contains("new epic") && epic.toString().contains("NEW")
                && epic.toString().contains("0") && epic.toString().contains("null"));
    }

}