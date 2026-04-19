package ru.hogwarts.school.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    // DTO в теле (@RequestBody @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                 HttpServletRequest request, Locale locale) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            // локализованный текст для конкретного FieldError
            details.put(fe.getField(), messageSource.getMessage(fe, locale));
        }
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                messageSource.getMessage("error.validation_failed", null, locale),
                ApiErrorCode.VALIDATION_FAILED,
                details,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // PathVariable / RequestParam валидация (ConstraintViolation)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest request, Locale locale) {
        Map<String, String> details = new HashMap<>();
        ex.getConstraintViolations().forEach(v -> {
            // propertyPath like "getFacultyById.id" — можно упростить при необходимости
            details.put(v.getPropertyPath().toString(), v.getMessage());
        });
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                messageSource.getMessage("error.validation_failed", null, locale),
                ApiErrorCode.CONSTRAINT_VIOLATION,
                details,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // 404 - нет хэндлера или статический ресурс не найден
    // NoHandlerFoundException срабатывает если включено throwExceptionIfNoHandlerFound, но для статических ресурсов
    // удобнее перехватывать через ErrorController (ниже). Здесь тоже добавим общий NoHandlerFoundException на всякий случай.
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request, Locale locale) {
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                messageSource.getMessage("error.404", null, locale),
                ApiErrorCode.RESOURCE_NOT_FOUND,
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<ApiError> handleFacultyNotFound(FacultyNotFoundException ex, HttpServletRequest request, Locale locale) {
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                ApiErrorCode.RESOURCE_NOT_FOUND,
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(InvalidFileSizeException.class)
    public ResponseEntity<ApiError> handleInvalidFileSize(Exception ex, HttpServletRequest request, Locale locale){
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                ApiErrorCode.VALIDATION_FAILED,
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ApiError> handleStudentNotFound(Exception ex, HttpServletRequest request, Locale locale){
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                ApiErrorCode.RESOURCE_NOT_FOUND,
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    // Общий fallback — чтобы ничего не ускользало (при этом в лог уходит stacktrace)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex, HttpServletRequest request, Locale locale) {
        ex.printStackTrace();
        ApiError err = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                messageSource.getMessage("error.internal", null, "Internal server error", locale),
                ApiErrorCode.INTERNAL_ERROR,
                null,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }


}
