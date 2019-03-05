package com.entr.sbdem.model;

import com.entr.sbdem.validation.PasswordMatches;
import com.entr.sbdem.entity.SpRole;
import com.entr.sbdem.entity.SpUser;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@PasswordMatches(message = "Password field don't match repeated password: ")
public class UserRegisterForm {

    @NotBlank(message = "User must have a Username ")
    @Size(min=4, max=20, message = "Username length min=4 max=20 ")
    @Pattern(regexp = "^[A-Za-z0-9]+$",message = "Username can only contain alphanumeric characters (letters A-Z, numbers 0-9)")
    private String username;

    @NotBlank(message = "User must have a Password ")
    @Size(min=6, max=30, message = "Password must have length min=6, max=30 " )
    private String password;
    private String matchPassword;

    @NotBlank(message = "User must have a Email ")
    @Email(message = "Not a valid Email ")
    private String email;
//generated
    public UserRegisterForm() { }
    public UserRegisterForm(@NotBlank(message = "User must have a Username ") @Size(min = 4, max = 20, message = "User must have length min=4 max=20 ") @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Only letters and digits allowed") String username, @NotBlank(message = "User must have a Password ") @Size(min = 6, max = 30, message = "Password must have length min=6, max=30 ") String password, String matchPassword, @NotBlank(message = "User must have a Email ") @Email(message = "Not a valid Email ") String email) {
        this.username = username;
        this.password = password;
        this.matchPassword = matchPassword;
        this.email = email;
    }

    public SpUser formToUser(Set<SpRole> roleSet){
        return new SpUser(username,password,email,roleSet);
    }
    public SpUser formToDefaultUser(){
        return new SpUser(username,password,email);
    }
}
