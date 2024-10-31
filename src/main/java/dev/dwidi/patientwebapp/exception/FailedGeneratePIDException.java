package dev.dwidi.patientwebapp.exception;

public class FailedGeneratePIDException extends RuntimeException {
    public FailedGeneratePIDException(String message) {
        super(message);
    }
}
