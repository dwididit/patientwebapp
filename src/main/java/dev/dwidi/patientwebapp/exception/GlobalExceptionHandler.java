package dev.dwidi.patientwebapp.exception;

import dev.dwidi.patientwebapp.constant.ApplicationConstant;
import dev.dwidi.patientwebapp.dto.BaseResponse;
import dev.dwidi.patientwebapp.utils.RequestIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;
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

    @ExceptionHandler(InvalidPostcodeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<String> handleInvalidPostcodeException(InvalidPostcodeException ex, WebRequest request) {
        String requestId = RequestIdUtils.generateRequestId();
        logException("Failed generate PID exception", requestId, request, ex);

        return new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                requestId
        );
    }

    @ExceptionHandler(DateInvalidFormatException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<String> handleDateInvalidFormatException(DateInvalidFormatException ex, WebRequest request) {
        String requestId = RequestIdUtils.generateRequestId();
        logException("Date must on format YYYY-MM-DD", requestId, request, ex);

        return new BaseResponse<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                requestId
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requestId = RequestIdUtils.generateRequestId();
        String message;

        if (ex.getParameter().getParameterType().equals(LocalDate.class)) {
            message = String.format(
                    "Invalid date format for parameter '%s'. Expected format: %s",
                    ex.getName(),
                    ApplicationConstant.DATE_PATTERN
            );
        } else {
            message = String.format(
                    "Invalid value for parameter '%s'. Expected type: %s",
                    ex.getName(),
                    ex.getParameter().getParameterType().getSimpleName()
            );
        }

        log.error("Parameter conversion error: {}", message);

        return new BaseResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                message,
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