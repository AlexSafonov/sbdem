package com.entr.sbdem.model;

import com.entr.sbdem.model.ValidationError;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class SbdemApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime localDateTime = LocalDateTime.now();
    private String debugMessage;
    private HttpStatus status;
    private String exceptionMessage;
    private List<ValidationError> validationErrors;

    public SbdemApiError(String debugMessage,HttpStatus status, Throwable ex){
        this.debugMessage = debugMessage;
        this.status = status;
        this.exceptionMessage = ex.getMessage();
    }
    public SbdemApiError(HttpStatus status, Throwable ex){
        this.debugMessage = "Unknown error has been occurred";
        this.status = status;
        this.exceptionMessage = ex.getMessage();
    }
    public void addValidationError(ValidationError error){
        if(validationErrors == null){
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(error);
    }

    private void addFieldValidationError(FieldError fieldError){
        if(validationErrors == null){
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(new ValidationError(fieldError.getObjectName()
                ,fieldError.getField()
                ,fieldError.getRejectedValue()
                ,fieldError.getDefaultMessage()));
    }
    private void addObjectValidationError(ObjectError objectError){
        if(validationErrors == null){
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(new ValidationError(objectError.getObjectName(),objectError.getDefaultMessage()));
    }
    private void addConstrainValidationError(ConstraintViolation<?> cv){
        if(validationErrors == null){
            validationErrors = new ArrayList<>();
        }
        validationErrors.add(new ValidationError(cv.getRootBeanClass().getSimpleName()
                ,((PathImpl) cv.getPropertyPath()).getLeafNode().asString()
                ,cv.getInvalidValue()
                ,cv.getMessage()));
    }

    public void addFieldValidationErrors(List<FieldError> fieldErrors){
        fieldErrors.forEach(this::addFieldValidationError);
    }
    public void addObjectValidationErrors(List<ObjectError> objectErrors){
        objectErrors.forEach(this::addObjectValidationError);
    }
    public void addConstrainValidationErrors(Set<ConstraintViolation<?>> constraintViolations){
        constraintViolations.forEach(this::addConstrainValidationError);
    }
}
