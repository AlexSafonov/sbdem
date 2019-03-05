package com.entr.sbdem.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserModifyForm {
    @Size(min=4, max=20, message = "Username length min=4 max=20 ")
    private String fullName;

    @NotBlank(message = "User must have a Email ")
    @Email(message = "Not a valid Email ")
    private String email;

    @Size(max = 255, message = "Maximum poem size  = 255 letters")
    private String aboutUser;

    public UserModifyForm(@Size(max = 30) String fullName, @Email String email, @Size(max = 255) String aboutUser) {
        this.fullName = fullName;
        this.email = email;
        this.aboutUser = aboutUser;
    }

    public UserModifyForm() {
    }
}
