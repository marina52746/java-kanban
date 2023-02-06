import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task{
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, id, status);
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }


    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    void setStatusDone() {
        super.setStatus(TaskStatus.DONE);
    }
    void setStatusInProgress() {
        super.setStatus(TaskStatus.IN_PROGRESS);
    }
    void setStatusNew() {
        super.setStatus(TaskStatus.NEW);
    }

    @Override
    public void setStatus(TaskStatus status) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIds, epic.subtasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }

    @Override
    public String toString() {
        String substringTask = super.toString().substring(0, super.toString().length() - 1).replace("Task", "Epic");
        return substringTask + ", " +
                "subtasksIds=" + subtasksIds +
                '}';
    }
}
