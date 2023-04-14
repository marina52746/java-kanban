package task;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, int id, TaskStatus status, long duration,
                   LocalDateTime startTime, Integer epicId) {
        super(name, description, id, status, duration, startTime);
        this.epicId = epicId;
        setType(TaskType.SUBTASK);
    }

    public Subtask(String name, String description, TaskStatus status, long duration,
                   LocalDateTime startTime, Integer epicId) {
        super(name, description, status, duration, startTime);
        this.epicId = epicId;
        setType(TaskType.SUBTASK);
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String substringTask = super.toString().substring(0, super.toString().length() - 1).replace("task", "Subtask");
        return substringTask + ", " +
                "epicId=" + epicId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
