package task;
import exceptions.ManagerSaveException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private TaskType type;
    private long duration;
    private LocalDateTime startTime;

    public Task(String name, String description, int id, TaskStatus status, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, TaskStatus status, long duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public long getDuration() { return duration; }

    public void setDuration(long duration) { this.duration = duration; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public static Task fromString(String taskString) throws ManagerSaveException {
        String[] elems = taskString.split(",");
        Task task = null;
        switch (elems[1]) {
            case "TASK":
                task = new Task(elems[2], elems[4], IntFromString(elems[0]), StatusFromString(elems[3]),
                        IntFromString(elems[5]), elems[6].equals("null") ? null : LocalDateTime.parse(elems[6], formatter));
                break;
            case "EPIC":
                task = new Epic(elems[2], elems[4], IntFromString(elems[0]));
                break;
            case "SUBTASK":
                task = new Subtask(elems[2], elems[4], IntFromString(elems[0]), StatusFromString(elems[3]),
                        IntFromString(elems[5]), elems[6].equals("null") ? null :LocalDateTime.parse(elems[6], formatter), IntFromString(elems[7]));
                break;
        }
        return task;
    }

    public static TaskStatus StatusFromString(String str) throws ManagerSaveException {
        switch (str) {
            case "NEW":
                return TaskStatus.NEW;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "DONE":
                return TaskStatus.DONE;
            default:
                throw new ManagerSaveException(str + " can't convert to task status");
        }
    }

    public static int IntFromString(String str) throws ManagerSaveException {
        int i;
        try {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new ManagerSaveException(str + " can't convert to int id");
        }
        return i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status && type == task.type
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, type, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", type=" + type +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static final class Dto {
        public static final String HEADER = "id,type,name,status,description,epic";
        private final int id;
        private final String type;
        private final String name;
        private final String status;
        private final String description;
        private final String epic; // not null for subtasks
        private final long duration;
        private final String startTime;

        public Dto(Task task) {
            this.id = task.getId();
            this.type = String.valueOf(task.getType());
            this.name = task.getName();
            this.status = String.valueOf(task.getStatus());
            this.description = task.getDescription();
            this.epic = task instanceof Subtask
                    ? ((Subtask) task).getEpicId().toString()
                    : "";
            this.duration = task.getDuration();
            if (task.startTime == null) this.startTime = "null";
            else this.startTime = task.startTime.format(formatter);
        }

        public static Dto cons(Task task) {
            return new Dto(task);
        }

        public String asString() {
            return String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%s",
                    id,
                    type,
                    name,
                    status,
                    description,
                    duration,
                    startTime,
                    epic
            );
        }
    }
}
