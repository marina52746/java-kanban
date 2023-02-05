public class Subtask extends Task {
    private Integer epicId;
    private Integer oldEpicId = 0;

    public Integer getOldEpicId() {
        return oldEpicId;
    }

    public Subtask() {
        super();
    }
    public Subtask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
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
        String substringTask = super.toString().substring(0, super.toString().length() - 1).replace("Task", "Subtask");
        return substringTask + ", " +
                "epicId=" + epicId +
                '}';
    }
}
