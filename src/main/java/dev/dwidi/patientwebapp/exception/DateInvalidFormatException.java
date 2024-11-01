package dev.dwidi.patientwebapp.exception;

public class DateInvalidFormatException extends RuntimeException {
    public DateInvalidFormatException(String message) {
        super(message);
    }
}
