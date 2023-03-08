package HistoryManager;
import Task.Task;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

class Node {
    Node nextNode;
    Node previousNode;
    Task task;

    public Node(Task task) {
        nextNode = null;
        previousNode = null;
        this.task = task;
    }
}

public class InMemoryHistoryManager implements HistoryManager{
    private List<Task> CustomLinkedList = new ArrayList<>();
    private HashMap<Integer, Node> tasksPlaceInLinkedList = new HashMap<>();
    private Node Head = null;
    private Node Tail = null;

    Node linkLast(Task task) {
        Node newNode = new Node(task);
        if (Head == null) {
            Head = newNode;
            Tail = newNode;
        } else {
            Tail.nextNode = newNode;
            newNode.previousNode = Tail;
            Tail = newNode;
        }
        return newNode;
    }

    void getTasks() {
        if (Head == null)
            return;
        CustomLinkedList.clear();
        Node currentNode = Head;
        while (true) {
            CustomLinkedList.add(currentNode.task);
            currentNode = currentNode.nextNode;
            if (currentNode == null) break;
        }
    }

    void removeNode(Node node) {
        if (node == null) return;
        if (node.previousNode != null) {
            if (node.nextNode != null) {
                node.previousNode.nextNode = node.nextNode;
                node.nextNode.previousNode = node.previousNode;
            } else {
                Tail = node.previousNode;
                node.previousNode.nextNode = null;
            }
        } else {
            if (node.nextNode != null) {
                Head = node.nextNode;
                node.nextNode.previousNode = null;
            } else {
                Head = null;
                Tail = null;
            }
        }
    }

    @Override
    public void add(Task task) {
        if (tasksPlaceInLinkedList.containsKey(task.getId())) {
            removeNode(tasksPlaceInLinkedList.get(task.getId()));
        }
        tasksPlaceInLinkedList.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(tasksPlaceInLinkedList.get(id));
        tasksPlaceInLinkedList.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        getTasks();
        for (Task task : CustomLinkedList)
            System.out.print(task.getId() + " ");
        System.out.println();
        return CustomLinkedList;
    }
}
