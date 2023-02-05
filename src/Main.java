public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Задача1", "Описание задачи1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача2", "Описание задачи2");
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic1.getId());
        taskManager.createSubtask(subtask2);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic2.getId());
        taskManager.createSubtask(subtask3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(TaskStatus.DONE);
        taskManager.updateTask(task2);
        subtask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
        taskManager.deleteTask(task1.getId());
        taskManager.deleteSubtask(subtask2.getId());
        taskManager.deleteEpic(epic2.getId());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());
    }
}
