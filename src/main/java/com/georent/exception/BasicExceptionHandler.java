package com.georent.exception;

import com.georent.message.GeoRentIHttpStatus;
import com.georent.message.Message;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class BasicExceptionHandler {

    /**
     * Exception handler for LotNotFoundException
     * @param ex - The exception.
     * @param request The http request that caused the exception.
     * @return 404 "Not found" status and additional info.
     */
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
        response.setBody(Message.ERROR.getDescription());
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Exception handler for SuchUserExistsException.
     * @param ex - The exception.
     * @param request The http request that caused the exception.
     * @return 452 "REGISTRATION_USER_ERROR" and "HttpStatus.CONFLICT"  status and additional info.
     */
    @ExceptionHandler({RegistrationSuchUserExistsException.class})
    protected ResponseEntity<?> handleSuchUserExists(RegistrationSuchUserExistsException ex,
                                                  HttpServletRequest request){
        final String method = request.getMethod();
        final String requestURI = request.getRequestURI();
        final String message = ex.getMessage();
        final int statusCode = GeoRentIHttpStatus.getValue(ex.getMessage());

        GenericResponse<String> response = new GenericResponse<>();
        response.setMethod(method);
        response.setCause(message);
        response.setPath(requestURI);
        response.setBody(Message.ERROR.getDescription());
        response.setStatusCode(statusCode);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Exception handler for SuchUserExistsException.
     * @param ex - The exception.
     * @param request The http request that caused the exception.
     * @return 453 or 454 "INVALID_FILE_... " (multipartfile to s3) and "HttpStatus.PRECONDITION_FAILED"  status and additional info.
     */
    @ExceptionHandler({MultiPartFileValidationException.class})
    protected ResponseEntity<?> handleMultiPartFile(MultiPartFileValidationException ex,
                                                    HttpServletRequest request){
        final String method = request.getMethod();
        final String requestURI = request.getRequestURI();
        final String message = ex.getMessage();
        final int statusCode = GeoRentIHttpStatus.getValue(ex.getMessage());

        GenericResponse<String> response = new GenericResponse<>();
        response.setMethod(method);
        response.setCause(message);
        response.setPath(requestURI);
        response.setBody(Message.ERROR.getDescription());
        response.setStatusCode(statusCode);
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(response);
    }

    /**
     * if failed, redirects user-agent to "error.jsp", with return code of 301.
     * @param ex
     * @param request
     * @return
     */

    @ExceptionHandler({ForgotException.class})
    protected ResponseEntity<?> handleForgotException(ForgotException ex,
                                                      HttpServletRequest request){

        GenericResponse<String> response = new GenericResponse<>();
        response.setMethod(request.getMethod());
        response.setCause(ex.getMessage());
        response.setPath(request.getRequestURI());
        response.setBody(Message.MAIL_NOT_SENT.getDescription());
        response.setStatusCode(HttpStatus.MOVED_PERMANENTLY.value());
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).body(response);
    }


    @Data
    public static class GenericResponse<T> {
        private T body;
        private String method;
        private String path;
        private String cause;
        private int statusCode;
    }
}
