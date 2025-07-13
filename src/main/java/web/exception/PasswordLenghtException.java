package web.exception;

public class PasswordLenghtException extends RuntimeException{
    public PasswordLenghtException() {
        super("Паполь меньше допустимых 4 символов");
    }
}
