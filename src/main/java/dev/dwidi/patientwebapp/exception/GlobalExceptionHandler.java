package dev.dwidi.patientwebapp.exception;

import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.utils.RequestIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private void logException(String errorType, String requestId, WebRequest request, Exception ex) {
        String requestURI = request.getDescription(false);
        String requestMethod = request.getParameterValues("_method") != null ?
                Objects.requireNonNull(request.getParameterValues("_method"))[0] : "UNKNOWN";

        log.error("{} - RequestId: {}, URI: {}, Method: {}, Message: {}",
                errorType,
                requestId,
                requestURI,
                requestMethod,
                ex.getMessage());
    }

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<String> handlePatientNotFoundException(PatientNotFoundException ex, WebRequest request) {
        String requestId = RequestIdUtils.generateRequestId();
        logException("Patient not found exception", requestId, request, ex);

        return new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                requestId
        );
    }

    @ExceptionHandler(FailedGeneratePIDException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<String> handleFailedGeneratePIDException(FailedGeneratePIDException ex, WebRequest request) {
        String requestId = RequestIdUtils.generateRequestId();
        logException("Failed generate PID exception", requestId, request, ex);

        return new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                requestId
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<String> handleGlobalException(Exception ex, WebRequest request) {
        String requestId = RequestIdUtils.generateRequestId();
        logException("Unhandled exception", requestId, request, ex);

        log.error("Stack trace for unhandled exception: ", ex);
        return new BaseResponse<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                null,
                requestId
        );
    }
}