import historyManager.HistoryManager;
import manager.Managers;
import task.*;
import taskManager.*;

import java.util.List;

public class Main {
        public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task("Задача1", "Описание задачи1", TaskStatus.IN_PROGRESS);
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Задача2", "Описание задачи2", TaskStatus.NEW);
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        epic1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", TaskStatus.NEW,
                epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", TaskStatus.NEW,
                epic1.getId());
        subtask2 = taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", TaskStatus.NEW,
                epic1.getId());
        subtask3 = taskManager.createSubtask(subtask3);
        
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        epic2 = taskManager.createEpic(epic2);

        taskManager.getTaskById(1);
        printHistory(taskManager.getHistory());
        taskManager.getTaskById(2);
        printHistory(taskManager.getHistory());
        taskManager.getTaskById(1);
        printHistory(taskManager.getHistory());
        taskManager.getEpicById(3);
        printHistory(taskManager.getHistory());
        taskManager.getTaskById(2);
        printHistory(taskManager.getHistory());
        taskManager.getTaskById(2);
        printHistory(taskManager.getHistory());
        taskManager.getSubtaskById(4);
        printHistory(taskManager.getHistory());
        taskManager.getSubtaskById(6);
        printHistory(taskManager.getHistory());
        taskManager.getSubtaskById(5);
        printHistory(taskManager.getHistory());
        taskManager.getSubtaskById(4);
        printHistory(taskManager.getHistory());
        taskManager.getEpicById(7);
        printHistory(taskManager.getHistory());
        taskManager.deleteTask(2);
        printHistory(taskManager.getHistory());
        taskManager.deleteEpic(3);
        printHistory(taskManager.getHistory());
    }
    
    public static void printHistory(List<Task> tasks) {
        for (Task task : tasks) {
            System.out.print(task.getId() + " ");
        }
        System.out.println();
    }
}
