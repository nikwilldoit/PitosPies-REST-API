package com.pitospies.pitospieswebservice.settings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException; // Για Spring Boot 3+

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> httpResponse(String message, Exception ex, WebRequest request, HttpStatus status) {
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();

        Map<String, Object> map =
                errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults()); //here it gets only the timestamp for the json
        map.put("error", message + ": " + ex.getMessage());
        map.put("status", status.value());
        map.put("path", request.getDescription(false));

        return map;
    }

    //Handling Type Mismatch (400 Bad Request)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> map = httpResponse("Bad Request", ex, request, status);
        return new ResponseEntity<>(map, status);
    }

    //Handling Constraint Violations (400 Bad Request - p.x. @NotNull, @Min, @Size validation failures)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(ConstraintViolationException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String errorMessage = "";
        for(var violation: ex.getConstraintViolations()){
            errorMessage+=violation.getMessage();
        }

        Map<String, Object> map = httpResponse(errorMessage, ex, request, status);
        return new ResponseEntity<>(map, status);
    }

    //Handling 404 Not Found (Spring Boot 3+)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NoResourceFoundException ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String, Object> map = httpResponse("Resource Not Found", ex, request, status);
        return new ResponseEntity<>(map, status);
    }

    //Catch-all General Exception (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> map = httpResponse("Internal Server Error", ex, request, status);
        return new ResponseEntity<>(map, status);
    }
}