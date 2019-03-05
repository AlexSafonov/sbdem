package com.entr.sbdem.validation;

import com.entr.sbdem.model.UserRegisterForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches,Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation){}

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UserRegisterForm urf = (UserRegisterForm) value;
        return urf.getPassword().equals(urf.getMatchPassword());
    }
}
