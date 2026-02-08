package com.wiratamap.backendlibrary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .sorted()
                .toList();

        return Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "errors", errors
        );
    }

    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDuplicateRecordException(DuplicateRecordException ex) {
        return Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "message", ex.getMessage()
        );
    }

    @ExceptionHandler(RecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        return Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "message", ex.getMessage()
        );
    }
}
