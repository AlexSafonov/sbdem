package com.entr.sbdem.service;

import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.model.UserModifyForm;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


public interface SpUserService {

    SpUser findUserByUsername(String username);
    SpUser findUserByEmail(String email);
    SpUser findUserById(Long id);
    List<SpUser> findAllUsers();
    Set<String> getUserRoles(String username);
    boolean userAlreadyExist(String username, String email);
    boolean userAlreadyExist(SpUser user);
    SpUser saveUserWithROLE_USER(String username, String password, String email);
    void updateUser(String email, String about, String fullname, String username);
    void updateUser(String username,UserModifyForm umf);
    void updateUsersAvatarImg(String url, String username);
    SpUser saveUserWithROLE_USER(SpUser user);
    void deleteUserByUsername(String username);
    void fullUpdateUserEntity(SpUser newUser, Long id);

}
