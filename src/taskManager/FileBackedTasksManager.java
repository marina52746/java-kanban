package taskManager;
import exceptions.ManagerSaveException;
import historyManager.FileBackedHistoryManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws ManagerSaveException {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        String line;
        boolean isFirstLine = true;
        boolean isLastLine = false;
        List<String> lines = new ArrayList<>();
        try {
            FileReader reader = new FileReader(file.getAbsolutePath());
            BufferedReader br = new BufferedReader(reader);
            while (br.ready()) {
                line = br.readLine();
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.isEmpty()) {
                    isLastLine = true;
                    continue;
                }
                if (!isLastLine)
                    lines.add(line);
                else {
                    //in lines - all tasks, in line - history
                    int maxId = 0;
                    for (String taskString : lines) {
                        Task task = Task.fromString(taskString);
                        if (task.getId() > maxId)   maxId = task.getId();
                        switch (task.getType()) {
                            case TASK:
                                tasksManager.tasksById.put(task.getId(), task);
                                break;
                            case EPIC:
                                tasksManager.epicsById.put(task.getId(), (Epic)task);
                                break;
                            case SUBTASK:
                                tasksManager.subtasksById.put(task.getId(), (Subtask) task);
                                break;
                        }
                    }
                    InMemoryTaskManager.currentTaskId = maxId;
                    List<Integer> tasksIdsHistory = FileBackedHistoryManager.historyFromString(line);
                    for(Integer taskId : tasksIdsHistory) {
                        if (tasksManager.tasksById.containsKey(taskId)) {
                            Task task = tasksManager.tasksById.get(taskId);
                            tasksManager.historyManager.add(task);
                        }
                        if (tasksManager.epicsById.containsKey(taskId)) {
                            Epic epic = tasksManager.epicsById.get(taskId);
                            tasksManager.historyManager.add(epic);
                        }
                        if (tasksManager.subtasksById.containsKey(taskId)) {
                            Subtask subtask = tasksManager.subtasksById.get(taskId);
                            tasksManager.historyManager.add(subtask);
                        }
                    }
                }
            }
            br.close();
        } catch (ManagerSaveException e) {
            System.out.println("Can't load file " + file.getAbsolutePath());
        } catch (Exception e) {
            throw new ManagerSaveException();
        }
        return tasksManager;
    }

    public static void main(String[] args) throws ManagerSaveException {
        TaskManager taskManager = new FileBackedTasksManager(new File("resources/tasks_file.csv"));

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
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getEpicById(3);

        FileBackedTasksManager taskManager2 = FileBackedTasksManager.loadFromFile(new File("resources/tasks_file.csv"));
    }


    @Override
    public Task createTask(Task task) throws ManagerSaveException {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task task) throws ManagerSaveException {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void deleteTask(Integer taskId) throws ManagerSaveException {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) throws ManagerSaveException {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) throws ManagerSaveException {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public void deleteEpic(Integer epicId) throws ManagerSaveException {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) throws ManagerSaveException {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) throws ManagerSaveException {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteSubtask(Integer subtaskId) throws ManagerSaveException {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public Task getTaskById(int id) throws ManagerSaveException {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) throws ManagerSaveException {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) throws ManagerSaveException {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    public void deleteAllTasks() throws ManagerSaveException {
        super.deleteAllTasks();
        save();
    }

    public void deleteAllEpics() throws ManagerSaveException {
        super.deleteAllEpics();
        save();
    }

    public void deleteAllSubtasks() throws ManagerSaveException {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateEpicStatus(Integer epicId) throws ManagerSaveException {
        super.updateEpicStatus(epicId);
        save();
    }

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = getWriter()) {
            saveTasksToFile(writer);
        } catch (IOException e) {
            throw new ManagerSaveException("Error on saving tasks to file");
        }
    }

    private BufferedWriter getWriter() throws ManagerSaveException {
        try {
            return new BufferedWriter(
                    new FileWriter(file)
            );
        } catch (IOException e) {
            throw new ManagerSaveException();
        }

    }

    private void saveTasksToFile(BufferedWriter writer) throws ManagerSaveException {
        try {
            writer.write(Task.Dto.HEADER);
            writer.write("\n");

            for (Task task : tasksById.values()) {
                writer.write(Task.Dto.cons(task).asString());
                writer.write("\n");
            }
            for (Epic epic : epicsById.values()) {
                writer.write(Task.Dto.cons(epic).asString());
                writer.write("\n");
            }
            for (Subtask subtask : subtasksById.values()) {
                writer.write(Task.Dto.cons(subtask).asString());
                writer.write("\n");
            }
            writer.write("\n");
            writer.write(FileBackedHistoryManager.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Can't write to file " + file.getAbsolutePath());
        }

    }
}
