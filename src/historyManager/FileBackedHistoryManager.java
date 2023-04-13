package historyManager;
import exceptions.ManagerSaveException;
import task.Task;
import java.util.ArrayList;
import java.util.List;

public class FileBackedHistoryManager {

    public static String historyToString(HistoryManager manager) {
        String str;
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.getId());
            sb.append(",");
        }
        str = sb.toString();
        if (str.length() > 1)
            str = str.substring(0, str.length() - 1);
        return str;
    }

    public static List<Integer> historyFromString(String value) throws ManagerSaveException {
        List<Integer> ids = new ArrayList<>();
        String[] str = value.split(",");
        for (String str_el : str) {
            int i;
            try {
                i = Integer.parseInt(str_el);
                ids.add(i);
            } catch (Exception e) {
                throw new ManagerSaveException("Can't convert string " + value + " to tasks ids");
            }
        }
        return ids;
    }
}
