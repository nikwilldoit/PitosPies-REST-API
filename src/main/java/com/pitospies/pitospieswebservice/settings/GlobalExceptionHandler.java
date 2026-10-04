package com.pitospies.pitospieswebservice.settings;

import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException; // Για Spring Boot 3+

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {
    private Map<String, Object> httpResponse(String message, Exception ex, WebRequest request, HttpStatus status) {
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();
        Map<String, Object> map =
                errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        map.put("error", message);
        map.put("status", status);
        map.put("path", request.getDescription(false));
        return map;
    }
    private Map<String, Object> httpResponseErrorList(List<String> errors, WebRequest request, HttpStatus status) {
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();
        Map<String, Object> map =
                errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        map.put("error", errors);
        map.put("status", status);
        map.put("path", request.getDescription(false));
        return map;
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> map= httpResponse("Bad Request", ex, request, status);

        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleException(MethodArgumentNotValidException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = new ArrayList<>();
        for (var violation: ex.getBindingResult().getAllErrors())
            errors.add(violation.getDefaultMessage());

        Map<String, Object> map= httpResponseErrorList(
                errors, request, status);

        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<Map<String, Object>> handleException(BindException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errors = new ArrayList<>();
        for (var violation: ex.getAllErrors())
            errors.add(violation.getDefaultMessage());

        Map<String, Object> map= httpResponseErrorList(
                errors, request, status);

        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleException(ConstraintViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        List<String> errorMessage = new ArrayList<>();
        for (var violation: ex.getConstraintViolations()) {
            errorMessage.add(violation.getMessageTemplate());
        }

        Map<String, Object> map = httpResponse(String.join(", ", errorMessage), ex, request, status);

        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value =   ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleException(ResourceNotFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        Map<String, Object> map = httpResponse(ex.getMessage(), ex, request, status);

        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value = ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleException(ResourceAlreadyExistsException ex, WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        Map<String, Object> map = httpResponse(ex.getMessage(), ex, request, status);

        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> map= httpResponse("Internal Server Error", ex,request, status);

        return new ResponseEntity<>(map, status);
    }
}
