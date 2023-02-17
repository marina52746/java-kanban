import Managers.Managers;
import Task.*;
import TaskManager.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача1", "Описание задачи1", TaskStatus.IN_PROGRESS);
        task1 = taskManager.createTask(task1);

        Task task2 = new Task("Задача2", "Описание задачи2", TaskStatus.NEW);
        task2 = taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик1", "Описание эпика1", TaskStatus.NEW);
        epic1 = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", TaskStatus.NEW,
                epic1.getId());
        subtask1 = taskManager.createSubtask(subtask1);
        
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", TaskStatus.NEW,
                epic1.getId());
        subtask2 = taskManager.createSubtask(subtask2);
        
        Epic epic2 = new Epic("Эпик2", "Описание эпика2", TaskStatus.NEW);
        epic2 = taskManager.createEpic(epic2);
        
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", TaskStatus.NEW,
                epic2.getId());
        subtask3 = taskManager.createSubtask(subtask3);
        
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1 = taskManager.updateTask(task1);
        
        task2.setStatus(TaskStatus.DONE);
        task2 = taskManager.updateTask(task2);
        
        subtask1.setStatus(TaskStatus.DONE);
        subtask1 = taskManager.updateSubtask(subtask1);
        
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask2 = taskManager.updateSubtask(subtask2);
        
        subtask3.setStatus(TaskStatus.DONE);
        subtask3 = taskManager.updateSubtask(subtask3);
        
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getHistory();
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(4);
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(5);
        taskManager.getHistory();
        taskManager.getTaskById(2);
        taskManager.getHistory();
        taskManager.getTaskById(2);
        taskManager.getHistory();
        taskManager.getEpicById(6);
        taskManager.getHistory();
        taskManager.getTaskById(2);
        taskManager.getTaskById(2);
        taskManager.getHistory();
/*
        taskManager.deleteTask(task1.getId());
        taskManager.deleteSubtask(subtask2.getId());
        taskManager.deleteEpic(epic2.getId());
        
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
*/
    }
}
