package com.entr.sbdem.error;

import com.entr.sbdem.exception.StorageException;
import com.entr.sbdem.exception.StorageFileNotFoundException;
import com.entr.sbdem.exception.UserAlreadyExistsException;
import com.entr.sbdem.exception.UserNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;

@ControllerAdvice("com.entr.sbdem.controller")
@Log4j2
public class CustomMvcResponseEntityExceptionHandle extends ResponseEntityExceptionHandler {

    public CustomMvcResponseEntityExceptionHandle() {
        super();
    }

   /* @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }*/

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(RuntimeException exc, WebRequest request){
        String message = exc.getMessage();
        log.error(message, exc);
        return "404";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({StorageFileNotFoundException.class, StorageException.class, UserAlreadyExistsException.class})
    public String handleCustomException(RuntimeException exc, WebRequest request, Model model){
        String message = exc.getMessage();
        model.addAttribute("msg" , message);
        log.error(message, exc);
       // handleExceptionInternal(exc, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        return "error";
    }



}
