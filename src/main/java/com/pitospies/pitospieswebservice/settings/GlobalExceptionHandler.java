package com.pitospies.pitospieswebservice.settings;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> httpResponse(String message, Exception ex, WebRequest request, HttpStatus status){
        ErrorAttributes errorAttributes = new DefaultErrorAttributes();

        Map<String, Object> map =
                errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults()); //edw pernei mono to timestamp
        map.put("error", message + ": "+ ex.getMessage());
        map.put("status", status);
        map.put("path", request.getDescription(false));

        return map;
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleException(MethodArgumentTypeMismatchException ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, Object> map = httpResponse("Bad Request", ex, request, status);
        return new ResponseEntity<>(map, status);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> map = httpResponse("Internal Server Error", ex, request, status);
        return new ResponseEntity<>(map, status);
    }



}
