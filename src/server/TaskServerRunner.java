package server;
import exceptions.ManagerSaveException;
import manager.Managers;

import java.io.IOException;

public class TaskServerRunner {
    public static void main(String[] args) throws IOException, ManagerSaveException {
        HttpTaskServer taskServer = Managers.getTaskServer();
        taskServer.start();
    }
}
