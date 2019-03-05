package com.entr.sbdem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Data
@AllArgsConstructor
public class ValidationError {
    private String object;
    private String field;
    private Object rejectedValue;
    private String message;

    public ValidationError(String object, String message) {
        this.object = object;
        this.message = message;
    }

}
