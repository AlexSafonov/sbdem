package com.entr.sbdem.controller;

import com.entr.sbdem.config.AuthenticationFacadeImpl;
import com.entr.sbdem.config.AuthenticationFacade;
import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.exception.StorageException;
import com.entr.sbdem.model.UserModifyForm;
import com.entr.sbdem.service.AWSs3StorageService;
import com.entr.sbdem.service.FileSystemStorageService;
import com.entr.sbdem.service.SpUserService;
import com.entr.sbdem.service.StorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
@RequestMapping("/user")
@Log4j2
public class UserController {

    private final SpUserService userService;
    private final StorageService storageService;
    private final AuthenticationFacade authenticationFacade;

    public UserController(final SpUserService userService,
                          @Qualifier("AWSs3StorageService")
                          final StorageService storageService ,
                          final AuthenticationFacadeImpl authenticationFacadeImpl) {
        this.userService = userService;
        this.storageService = storageService;
        this.authenticationFacade = authenticationFacadeImpl;
    }
    @GetMapping
    public String getUserPage(){
        return "user";
    }

    @GetMapping("/{username}")
    public String getUserInfo(@PathVariable("username") String username, Model model){
        Authentication au = authenticationFacade.getAuthentication();
        SpUser user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/modify-user")
    public String getProfilePage(Model model) {
        Authentication au = authenticationFacade.getAuthentication();
        SpUser user = userService.findUserByUsername(au.getName());
        model.addAttribute("userModifyForm",new UserModifyForm(user.getFullname(),user.getEmail(),user.getAboutUser()));
        model.addAttribute("userImage",user.getAvatarImgUrl());
        return "modify-user";
    }

    @PostMapping("/modify-user")
    public String updateUsPrForm(@Valid @ModelAttribute("userModifyForm") UserModifyForm umf,
                                 Errors errors,
                                 SessionStatus status,
                                 RedirectAttributes redirectAttributes,
                                 Model model){
        if(errors.hasErrors()){
            model.addAttribute("userModifyForm", umf);
            return "modify-user";
        }
        Authentication au = authenticationFacade.getAuthentication();
        userService.updateUser(umf.getEmail(),umf.getAboutUser(),umf.getFullName(),au.getName());
        redirectAttributes.addFlashAttribute("rdrAtr","Your profile was successfully updated");
        status.setComplete();
        return "redirect:/user/modify-user";
    }

    @PostMapping("/modify-user-img")
    public String updateAvImg(MultipartFile mlp, RedirectAttributes redirectAttributes)throws StorageException{
        String filename = mlp.getOriginalFilename();
        Authentication au = authenticationFacade.getAuthentication();
        storageService.storeUserAvatarImage(mlp,au.getName());
        //Saving image to desktop/sbdem/upload/uploadImages. See FileSystemStorageService
        String avatarImgUrl = "https://s3.eu-west-2.amazonaws.com/sbdems3bucket/upload/uploadImages/"+au.getName()+"/"+filename;
        userService.updateUsersAvatarImg(avatarImgUrl,au.getName());
        redirectAttributes.addFlashAttribute("imageRdrAtr", "Your image Avatar was successfully updated");

        return "redirect:/user/modify-user";
    }

    @GetMapping("/status")
    public ResponseEntity<Object> status(){
        return ResponseEntity.notFound().build();
    }


}
