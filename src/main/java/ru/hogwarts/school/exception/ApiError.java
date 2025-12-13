package ru.hogwarts.school.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final ApiErrorCode code;
    private final Map<String, String> validationErrors;
    private final String path;

    public ApiError(int status, String error, String message, ApiErrorCode code, Map<String, String> validationErrors, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.code = code;
        this.validationErrors = validationErrors;
        this.path = path;
    }

    // геттеры (и сеттеры, если нужно)
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public ApiErrorCode getCode() { return code; }
    public Map<String, String> getValidationErrors() { return validationErrors; }
    public String getPath() { return path; }
}
