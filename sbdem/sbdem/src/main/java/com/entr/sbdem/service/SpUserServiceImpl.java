package com.entr.sbdem.service;

import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.entity.SpUser;
import com.entr.sbdem.exception.UserAlreadyExistsException;
import com.entr.sbdem.exception.UserNotFoundException;
import com.entr.sbdem.model.UserModifyForm;
import com.entr.sbdem.repository.SpUserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class SpUserServiceImpl implements SpUserService {

    private final SpUserRepository userRepository;
    private final SpRoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;

    public SpUserServiceImpl( final SpUserRepository userRepository,
                              final SpRoleServiceImpl roleService,
                              final BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public SpUser findUserByUsername(String username) throws UserNotFoundException{
            SpUser user = userRepository.findByUsername(username);
            if (user == null)
                throw new UserNotFoundException("User with username: " + username + " does not exists");
            return user;
    }

    @Override
    public SpUser findUserByEmail(String email) throws UserNotFoundException{
            SpUser user = userRepository.findByEmail(email);
            if (user == null)
                throw new UserNotFoundException("User with email: " + email + " does not exists");
            return user;
    }
    @Override
    public SpUser findUserById(Long id) throws UserNotFoundException{
            SpUser user = userRepository.findBySpUserId(id);
            if (user == null)
                throw new UserNotFoundException("User with id: " + id + " does not exists");
            return user;
    }

    @Override
    public List<SpUser> findAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public Set<String> getUserRoles(String username) throws UserNotFoundException{
        this.findUserByUsername(username);
        return userRepository.findRolesForUserByUsername(username);
    }

    @Override
    public boolean userAlreadyExist(String username, String email){
        if (userRepository.findByUsername(username) == null && userRepository.findByEmail(email) == null){
            return false;
        }
        else return true;
    }
    @Override
    public boolean userAlreadyExist(SpUser user){
        String username = user.getUsername();
        String email = user.getEmail();
        if (userRepository.findByUsername(username) == null && userRepository.findByEmail(email) == null){
            return false;
        }
        else return true;
    }

    @Override
    public void updateUsersAvatarImg(String url, String username) throws UserNotFoundException{
        SpUser user = this.findUserByUsername(username);
        user.setAvatarImgUrl(url);
        userRepository.save(user);
    }

    /*Update almost all fields of user entity not included : created_at, id
     */

    @Override
    public void fullUpdateUserEntity(SpUser newUser, Long id) throws UserNotFoundException {
        SpUser user = findUserById(id);//Checking if user exists before update
        //Encoding password if password has changed
        if(!user.getPassword().equals(newUser.getPassword())){
           String encodedPswrd = passwordEncoder.encode(newUser.getPassword());
           newUser.setPassword(encodedPswrd);
        }
        userRepository.save(newUser);
    }


    @Override
    public void updateUser(String email, String about, String fullName, String username)throws UserNotFoundException{
        SpUser user = this.findUserByUsername(username);
        user.setEmail(email);
        user.setAboutUser(about);
        user.setFullname(fullName);
        userRepository.save(user);
    }
    @Override
    public void updateUser(String username, UserModifyForm umf){
        SpUser user = this.findUserByUsername(username);
        user.setEmail(umf.getEmail());
        user.setAboutUser(umf.getAboutUser());
        user.setFullname(umf.getFullName());
        userRepository.save(user);
    }

    @Override
    public SpUser saveUserWithROLE_USER(SpUser user) throws UserAlreadyExistsException {
        if(userAlreadyExist(user)){
            throw new UserAlreadyExistsException("User with such email or username already exists");
        }
        String cryptedPssword = passwordEncoder.encode(user.getPassword());
        SpRole userRole = roleService.findByRole("ROLE_USER");
        Set<SpRole> roleSet = user.getRoles();
        roleSet.add(userRole);
        SpUser newUser = userRepository.saveAndFlush(new SpUser(user.getUsername(), cryptedPssword, user.getEmail(), roleSet));
        log.info("New user created " + user.getUsername());
        return newUser;
    }

    @Override
    public SpUser saveUserWithROLE_USER(String username, String password, String email)  throws UserAlreadyExistsException {
        if(userAlreadyExist(username,email)){
            throw new UserAlreadyExistsException("User with such email or username already exists");
        }
        if(password==null){log.error("PASSWORD IS NULL");}
        String cryptedPssword = passwordEncoder.encode(password);
        SpRole userRole = roleService.findByRole("ROLE_USER");
        Set<SpRole> roleSet = new HashSet<>();
        roleSet.add(userRole);
        SpUser newUser = userRepository.saveAndFlush(new SpUser(username, cryptedPssword, email, roleSet));
        log.info("New user created " + username);
        return newUser;
    }
    @Override
    public void deleteUserByUsername(String username) throws UserNotFoundException{
        SpUser user = findUserByUsername(username);
        for(SpRole role :user.getRoles() ){
            user.removeSpRole(role);
        }
        userRepository.deleteSpUserByUsername(username);
    }


}
