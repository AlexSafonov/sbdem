package com.entr.sbdem.controller;

import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.model.UserRegisterForm;
import com.entr.sbdem.service.SpUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final SpUserService userService;

    public RegisterController(final SpUserService userService){
        this.userService = userService;
    }

    @GetMapping
    public String userRegForm(Model model){
        model.addAttribute("userForm", new UserRegisterForm());
        return "register";
    }

    @PostMapping
    public String processRegistration(@Valid @ModelAttribute("userForm") UserRegisterForm userForm,
                                      Errors errors, Model model, SessionStatus sessionStatus){
        if(errors.hasErrors()) {
            model.addAttribute("userForm",userForm);
            return "register";
        }
        SpUser user = userForm.formToDefaultUser();
        userService.saveUserWithROLE_USER(user);
        sessionStatus.setComplete();
        return "redirect:/login";
    }

}
