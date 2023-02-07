package Task;

public class Subtask extends Task {
    private Integer epicId;
    private Integer oldEpicId = 0;

    public Subtask(String name, String description, int id, TaskStatus status, Integer epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getOldEpicId() {
        return oldEpicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        oldEpicId = this.epicId;
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String substringTask = super.toString().substring(0, super.toString().length() - 1).replace("Task", "Task.Task.Subtask");
        return substringTask + ", " +
                "epicId=" + epicId +
                '}';
    }
}
