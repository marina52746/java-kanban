package server;
import manager.Managers;
import java.io.IOException;

public class KVServerRunner {
    public static void main(String[] args) throws IOException {
        KVServer kvServer = Managers.getKvServer();
        kvServer.start();
    }
}
