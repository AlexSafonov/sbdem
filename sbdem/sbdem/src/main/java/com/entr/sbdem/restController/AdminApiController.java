package com.entr.sbdem.restController;

import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.exception.UserAlreadyExistsException;
import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.model.OkMessage;
import com.entr.sbdem.model.UserModifyForm;
import com.entr.sbdem.model.UserRegisterForm;
import com.entr.sbdem.service.AWSs3StorageService;
import com.entr.sbdem.service.FileSystemStorageService;
import com.entr.sbdem.service.SpUserService;
import com.entr.sbdem.service.StorageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    private final SpUserService userService;
    private final StorageService storageService;

    public AdminApiController(final SpUserService spUserService,
                              @Qualifier("AWSs3StorageService")
                              final StorageService storageService) {
        this.userService = spUserService;
        this.storageService = storageService;
    }

    @GetMapping(value = "/get-user/{username}", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<SpUser> getUserByUsername(@PathVariable("username") String username) throws UserNotFoundException {
        return new ResponseEntity<>(userService.findUserByUsername(username), HttpStatus.FOUND);
    }

    @GetMapping(value = "/get-all-users", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<List<SpUser>> getAllUsers() {
        return new ResponseEntity<>(userService.findAllUsers(), HttpStatus.OK);
    }

    @PostMapping(value = "/create-user", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<SpUser> createUser(@Valid @RequestBody UserRegisterForm userForm) throws UserAlreadyExistsException {
        SpUser user = userForm.formToDefaultUser();
        return new ResponseEntity<>(userService.saveUserWithROLE_USER(user), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-user/{username}")
    public ResponseEntity<Object> deleteUser(@PathVariable("username") String username) throws UserNotFoundException {
        userService.deleteUserByUsername(username);
        storageService.deleteAllFilesOfUser(username);
        OkMessage okMessage = new OkMessage("User was successfully deleted");
        return new ResponseEntity<>(okMessage, HttpStatus.OK);
    }

    @PutMapping(value = "/full-update-user/{id}", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public ResponseEntity<Object> fullUpdateUser(@PathVariable("id") String id,
                                                 @RequestBody @Valid SpUser user) throws UserNotFoundException {
        userService.fullUpdateUserEntity(user, Long.valueOf(id));
        OkMessage okMessage = new OkMessage("User was successfully full updated");
        return new ResponseEntity<>(okMessage, HttpStatus.OK);
    }

    @PatchMapping(value = "/update-user-info/{username}", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public  ResponseEntity<Object> updateUserInfo(@PathVariable("username") String username,
                                                 @RequestBody @Valid UserModifyForm umf){
        userService.updateUser(username,umf);
        OkMessage okMessage = new OkMessage("User was successfully updated");
        return new ResponseEntity<>(okMessage, HttpStatus.OK);
    }
}
