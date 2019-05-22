package com.entr.sbdem.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "spusers")
public class SpUser implements UserDetails {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE)
    @NotNull
    @Column(name = "spuser_id", unique = true, columnDefinition = "serial")
    private Long spUserId;

    @Column(name = "createdAt", updatable = false,  columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt =  new Timestamp(System.currentTimeMillis());

    @NotNull
    @Size(min = 4)
    @Column(name = "username", unique = true)
    private String username;

    @NotNull
    @Size(min = 6)
    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @Column(name="about")
    private String aboutUser;

    @Column(name = "avatar_img_url")
    private String avatarImgUrl = "/images/no-image.jpg";

    @Column(name = "fullname")
    private String fullName;

    //Temporary all is TRUE
    @NotNull
    @Column(name = "isAccountNonExpired")
    private boolean isAccountNonExpired = true;

    @NotNull
    @Column(name = "isAccountNonLocked")
    private boolean isAccountNonLocked = true;

    @NotNull
    @Column(name = "isCredentialsNonExpired")
    private boolean isCredentialsNonExpired = true;

    @NotNull
    @Column(name = "isEnabled")
    private boolean isEnabled = true;
    ///
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "spusers_sproles",
            joinColumns = { @JoinColumn(name = "spuser_id")},
            inverseJoinColumns = { @JoinColumn(name = "sprole_id")}
    )
    private Set<SpRole> roles = new HashSet<>();

    private SpUser(){}

    public SpUser(@NotNull @Size(min = 5) String username,
                  @NotNull @Size(min = 6) String password,
                  @NotNull String email,
                  Set<SpRole> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

    public SpUser(@NotNull @Size(min = 5) String username, @NotNull @Size(min = 6) String password, @NotNull String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void addSpRole(SpRole role){
        roles.add(role);
        role.getUsers().add(this);
    }
    public void removeSpRole(SpRole role){
        roles.remove(role);
        role.getUsers().remove(this);
    }

    public Long getSpUserId() {
        return spUserId;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (SpRole role : this.roles) {
            if (role != null) {
                GrantedAuthority authority = new SimpleGrantedAuthority(role.getRole());
                grantedAuthorities.add(authority);
            }
        }
        return grantedAuthorities;
    }


    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public Set<SpRole> getRoles() {
        return roles;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullName) {
        this.fullName = fullName;
    }

    public void setRoles(Set<SpRole> roles) {
        this.roles = roles;
    }

    public void setSpUserId(Long spUserId) {
        this.spUserId = spUserId;
    }

    public String getAboutUser() {
        return aboutUser;
    }

    public void setAboutUser(String aboutUser) {
        this.aboutUser = aboutUser;
    }

    public String getAvatarImgUrl() {
        return avatarImgUrl;
    }

    public void setAvatarImgUrl(String avatarImgUrl) {
        this.avatarImgUrl = avatarImgUrl;
    }
}
