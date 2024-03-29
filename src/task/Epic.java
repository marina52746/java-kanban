package task;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, int id) {
        super(name, description, id, TaskStatus.NEW, 0, null);
        setType(TaskType.EPIC);
    }

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, 0, null);
        setType(TaskType.EPIC);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void setStatusDone() {
        super.setStatus(TaskStatus.DONE);
    }

    public void setStatusInProgress() {
        super.setStatus(TaskStatus.IN_PROGRESS);
    }

    public void setStatusNew() {
        super.setStatus(TaskStatus.NEW);
    }

    @Override
    public void setStatus(TaskStatus status) {}

    @Override
    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksIds, epic.subtasksIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds, endTime);
    }

    @Override
    public String toString() {
        String substringTask = super.toString().substring(0, super.toString().length() - 1).replace("task", "Epic");
        return substringTask + ", " +
                "subtasksIds=" + subtasksIds +
                ", endTime=" + endTime +
                '}';
    }
}
