package com.entr.sbdem.validation;

import com.entr.sbdem.model.UserRegisterForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

//Because our validator relies upon the Spring Context, the integration test is mandatory.
//But i am not sure if i should test ALL the forms because all POJO use same javax.validation
//So i just test one with my custom matchPassword validation
@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationTest {

    private UserRegisterForm userRegisterForm;

    @Autowired
    private Validator validator;

    @Before
    public void setup(){
        userRegisterForm = new UserRegisterForm();
    }

    @Test
    public void userRegFormValidation_returnOk(){
        //Set Data
        userRegisterForm.setUsername("username");
        userRegisterForm.setPassword("password");
        userRegisterForm.setMatchPassword("password");
        userRegisterForm.setEmail("email@test.com");
        //validate data
        Set<ConstraintViolation<UserRegisterForm>> violations = validator.validate(userRegisterForm);
        //
        assertEquals(0,violations.size());
    }

    @Test
    public void userRegFormValidation_returnFail(){
        //Set Data
        userRegisterForm.setUsername("u"); // min 4 max 20. not blank
        userRegisterForm.setPassword("655"); // min 6 max
        userRegisterForm.setMatchPassword("46sad64"); // must match password field
        userRegisterForm.setEmail("notEmail"); // must be email
        //validate data
        Set<ConstraintViolation<UserRegisterForm>> violations = validator.validate(userRegisterForm);
        //
        assertEquals(4,violations.size());


    }

    @Test
    public void userRegFormValidationSecond_returnFail(){
        //set data to test similar behavior.
        userRegisterForm.setUsername("*/*/*/*/*/"); // only letters or numbers
        userRegisterForm.setPassword("11111111111111111111111111111111111111111111111111111111111111111111"); // max 30
        userRegisterForm.setMatchPassword("nope"); //
        userRegisterForm.setEmail("almost@"); //@Email javax.validation
        //validate data
        Set<ConstraintViolation<UserRegisterForm>> violations = validator.validate(userRegisterForm);
        //
        assertEquals(4,violations.size());
    }







}
