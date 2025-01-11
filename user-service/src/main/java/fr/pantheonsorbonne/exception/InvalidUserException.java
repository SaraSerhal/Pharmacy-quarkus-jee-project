package fr.pantheonsorbonne.exception;

public class InvalidUserException extends Throwable {
    public InvalidUserException(String message) {
        super(message);
    }
}
