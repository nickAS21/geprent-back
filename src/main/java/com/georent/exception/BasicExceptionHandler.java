package com.georent.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class BasicExceptionHandler {


    @ExceptionHandler({LotNotFoundException.class})
    protected ResponseEntity<?> handleLotNotFound(LotNotFoundException ex,
                                                HttpServletRequest request){
        final String method = request.getMethod();
        final String requestURI = request.getRequestURI();
        final String message = ex.getMessage();

        GenericResponse<String> response = new GenericResponse<>();
        response.setMethod(method);
        response.setCause(message);
        response.setPath(requestURI);
        response.setBody("ERROR!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @Data
    public static class GenericResponse<T> {
        private T body;
        private String method;
        private String path;
        private String cause;
    }
}
