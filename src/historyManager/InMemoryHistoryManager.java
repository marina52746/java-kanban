package historyManager;
import task.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> customLinkedList = new ArrayList<>();
    private final Map<Integer, Node> tasksPlaceInLinkedList = new HashMap<>();
    private Node head = null;
    private Node tail = null;

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (tasksPlaceInLinkedList.containsKey(task.getId())) {
            removeNode(tasksPlaceInLinkedList.get(task.getId()));
        }
        tasksPlaceInLinkedList.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(tasksPlaceInLinkedList.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        getTasks();
        return customLinkedList;
    }

    private static class Node {
        Node nextNode;
        Node previousNode;
        Task task;

        public Node(Task task) {
            nextNode = null;
            previousNode = null;
            this.task = task;
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
        } else {
            tail.nextNode = newNode;
            newNode.previousNode = tail;
        }
        tail = newNode;
        return newNode;
    }

    private void getTasks() {
        customLinkedList.clear();
        Node currentNode = head;
        while (currentNode != null) {
            customLinkedList.add(currentNode.task);
            currentNode = currentNode.nextNode;
        }
    }

    private void removeNode(Node node) {
        if (node == null) return;
        final Node prev = node.previousNode;
        final Node next = node.nextNode;
        if (prev != null) {
            if (next != null) {
                prev.nextNode = next;
                next.previousNode = prev;
            } else {
                tail = prev;
                prev.nextNode = null;
            }
        } else {
            if (next != null) {
                head = next;
                next.previousNode = null;
            } else {
                head = null;
                tail = null;
            }
        }
    }
}
