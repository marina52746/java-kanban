package exceptions;

public class ManagerSaveException extends Exception {

    public ManagerSaveException() { super(); }

    public ManagerSaveException(final String message) {
        super(message);
    }
}