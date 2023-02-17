package HistoryManager;
import Task.Task;
import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> viewedTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (viewedTasks.size() >= 10) {
            viewedTasks.remove(0);
        }
        viewedTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        for (Task task : viewedTasks)
            System.out.print(task.getId() + " ");
        System.out.println();
        return viewedTasks;
    }
}
