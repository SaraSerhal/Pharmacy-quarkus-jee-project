package fr.pantheonsorbonne.exception;

public class MissingAddressException extends Throwable {
    public MissingAddressException(String message) {
        super(message);
    }
}