package task;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status;
    private TaskType type;

    public Task(String name, String description, int id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public static Task fromString(String taskString) throws Exception {
        String[] elems = taskString.split(",");
        Task task;
        switch (elems[1]) {
            case "TASK":
                task = new Task(elems[2], elems[4], IntFromString(elems[0]), StatusFromString(elems[3]));
                break;
            case "EPIC":
                task = new Epic(elems[2], elems[4], IntFromString(elems[0]));
                break;
            case "SUBTASK":
                task = new Subtask(elems[2], elems[4], IntFromString(elems[0]), StatusFromString(elems[3]),
                        IntFromString(elems[5]));
                break;
            default:
                throw new Exception(elems[0] + " can't convert to task type");
        }
        return task;
    }

    public static TaskStatus StatusFromString(String str) throws Exception {
        switch (str) {
            case "NEW":
                return TaskStatus.NEW;
            case "IN_PROGRESS":
                return TaskStatus.IN_PROGRESS;
            case "DONE":
                return TaskStatus.DONE;
            default:
                throw new Exception(str + " can't convert to task status");
        }
    }

    public static int IntFromString(String str) throws Exception {
        int i;
        try {
            i = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new Exception(str + " can't convert to int id");
        }
        return i;
    }

    public static final class Dto {
        public static final String HEADER = "id,type,name,status,description,epic";
        private final int id;
        private final String type;
        private final String name;
        private final String status;
        private final String description;
        private final String epic; // not null for subtasks

        public Dto(Task task) {
            this.id = task.getId();
            this.type = String.valueOf(task.getType());
            this.name = task.getName();
            this.status = String.valueOf(task.getStatus());
            this.description = task.getDescription();
            this.epic = task instanceof Subtask
                    ? ((Subtask) task).getEpicId().toString()
                    : "";
        }

        public static Dto cons(Task task) {
            return new Dto(task);
        }

        public String asString() {
            return String.format(
                    "%s,%s,%s,%s,%s,%s",
                    id,
                    type,
                    name,
                    status,
                    description,
                    epic
            );
        }
    }
}
