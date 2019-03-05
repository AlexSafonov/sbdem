package com.entr.sbdem.error;

import com.entr.sbdem.exception.UserAlreadyExistsException;
import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.model.SbdemApiError;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@Log4j2
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    public RestResponseEntityExceptionHandler() {super();}

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Supported methods are: ");
        for (String s: ex.getSupportedMethods()) {
            builder.append(s);
        }
        builder.append(".");
        String message = builder.toString();
        SbdemApiError error = new SbdemApiError(message,status,ex);
        return new ResponseEntity<>(error,status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are: ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        SbdemApiError error = new SbdemApiError(builder.substring(0, builder.length() - 2),HttpStatus.UNSUPPORTED_MEDIA_TYPE,ex);
        return new ResponseEntity<>(error,HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        SbdemApiError error = new SbdemApiError("Malformed JSON request", HttpStatus.BAD_REQUEST,ex);
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        SbdemApiError error = new SbdemApiError(status,ex);
        return new ResponseEntity<>(error,status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        SbdemApiError error = new SbdemApiError("Validation error",HttpStatus.BAD_REQUEST, ex);
        error.addFieldValidationErrors(ex.getBindingResult().getFieldErrors());
        error.addObjectValidationErrors(ex.getBindingResult().getGlobalErrors());
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        SbdemApiError error = new SbdemApiError("Could not handle error",status,ex);
        return new ResponseEntity<>(error,status);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(final UserNotFoundException ex){
        SbdemApiError error = new SbdemApiError("Handling UserNotFoundException",HttpStatus.NOT_FOUND,ex);
        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsExeption(final UserAlreadyExistsException ex){
        SbdemApiError error = new SbdemApiError("User already exists",HttpStatus.CONFLICT, ex);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex){
        SbdemApiError error = new SbdemApiError("Validation error",HttpStatus.BAD_REQUEST, ex);
        error.addConstrainValidationErrors(ex.getConstraintViolations());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(final UserNotFoundException ex){
        SbdemApiError error = new SbdemApiError("Please log in or register",HttpStatus.FORBIDDEN,ex);
        return new ResponseEntity<>(error,HttpStatus.FORBIDDEN);
    }



}
