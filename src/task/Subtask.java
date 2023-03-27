package task;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, int id, TaskStatus status, Integer epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
        setType(TaskType.SUBTASK);
    }

    public Subtask(String name, String description, TaskStatus status, Integer epicId) {
        super(name, description, status);
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
}
